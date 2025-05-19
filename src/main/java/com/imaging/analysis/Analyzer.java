package com.imaging.analysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.imaging.common.Util;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion2D;
import com.imaging.imager.ImageSensor;
import com.imaging.ray.LightRay;
import com.imaging.sample.Sample;

@Component
@Scope("session")
public class Analyzer {

	@Autowired
	private ImageSensor imagePlane;

	@Autowired
	private Sample sample;

	public float computeDistortionScore() {

		BufferedImage generatedImg = imagePlane.getBoundedImageForAnalysis();
		List<LightRay> marginalRays = imagePlane.getMarginalRays();
		float distortionScore = Float.NaN;

		if (marginalRays != null && marginalRays.size() > 0 && generatedImg != null) {
			// trace marginal rays back to parent rays
			List<LightRay> parentRays = marginalRays.parallelStream().map(ray -> Util.getRootRay(ray)).distinct()
					.collect(Collectors.toList());

			// get source bounds based on parents of marginal rays
			BoundedRegion2D<Float> sourceGenerationBounds = new BoundedRegion2D<Float>();
			parentRays.stream().forEach(ray -> {
				Point parentRayStartPoint = ray.getStartPos();
				float x = parentRayStartPoint.getX();
				float y = parentRayStartPoint.getY();
				sourceGenerationBounds.evaluate(x, y);
			});

			BufferedImage sourceImg = sample.getScaledImage(sourceGenerationBounds, generatedImg.getWidth(),
					generatedImg.getHeight());

			File outputfile1 = new File("src_img.jpg");
			File outputfile2 = new File("gen_img.jpg");
			try {
				if (sourceImg != null)
					ImageIO.write(sourceImg, "png", outputfile1);
				ImageIO.write(generatedImg, "png", outputfile2);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (sourceImg != null)
				distortionScore = getOverallDistortionScore(sourceImg, generatedImg);
		}

		return distortionScore;
	}

	private float getOverallDistortionScore(BufferedImage sourceImg, BufferedImage generatedImg) {
		/*
		 * BufferedImage sourceScaledImg = Util.scaleImg(sourceImg, sourceImg.getWidth()
		 * / 2, sourceImg.getHeight() / 2); float baselineEstimateDistortionScore =
		 * getComparisionScore(sourceImg, sourceScaledImg);
		 */

		int sourceImgpixelCount = sourceImg.getHeight() * sourceImg.getWidth();
		int genImgpixelCount = generatedImg.getHeight() * generatedImg.getWidth();
		float distortionScore = 0.0f;

		int height = generatedImg.getHeight();
		int width = generatedImg.getWidth();

		// scale both images to the lowest resolution image
		if (sourceImgpixelCount > genImgpixelCount) {
			sourceImg = Util.scaleImg(sourceImg, width, height);
		} else if (genImgpixelCount > sourceImgpixelCount) {
			height = sourceImg.getHeight();
			width = sourceImg.getHeight();
			generatedImg = Util.scaleImg(generatedImg, width, height);
		}

		// loop over pixel and add up difference score
		for (int counter = 0; counter < width; counter++) {
			for (int counter2 = 0; counter2 < height; counter2++) {
				int sourceImgVal = sourceImg.getRGB(counter, counter2);
				int cmpImgVal = generatedImg.getRGB(counter, counter2);
				float score = compareValues(sourceImgVal, cmpImgVal);
				distortionScore += score;
			}
		}

		return distortionScore;
	}

	/**
	 * Get square root sum of hsv differences
	 * 
	 * @param sourceImgVal
	 * @param cmpImgVal
	 * @return
	 */
	private float compareValues(int sourceImgVal, int cmpImgVal) {

		float[] hsvSource = Util.getHSV(sourceImgVal);
		float[] hsvCmp = Util.getHSV(cmpImgVal);

		float score = (float) Math.sqrt(((hsvSource[0] - hsvCmp[0]) * (hsvSource[0] - hsvCmp[0]))
				+ ((hsvSource[1] - hsvCmp[1]) * (hsvSource[1] - hsvCmp[1]))
				+ ((hsvSource[2] - hsvCmp[2]) * (hsvSource[2] - hsvCmp[2])));

		return score;
	}
}
