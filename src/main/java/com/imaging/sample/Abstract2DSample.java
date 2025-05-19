package com.imaging.sample;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.imaging.common.Util;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion2D;
import com.imaging.light.LightSource;
import com.imaging.ray.LightRay;

public abstract class Abstract2DSample implements Sample {

	private BufferedImage image;
	private int imagePixelHeight = 0;
	private int imagePixelWidth = 0;
	private float sampleHeight = 0;
	private float sampleWidth = 0;
	private float heightPerPixel = 0;
	private float widthPerPixel = 0;
	private SamplingRate samplingRate;

	// position of sample center
	private float samplePosX = 0;
	private float samplePosY = 0;
	private float samplePosZ = 0;

	// private int heightCounter;
	private int heightPixelCountForSample;
	private int widthPixelCountForSample;
	private int totalHeightStrides;
	private int totalWidthStrides;
	private int totalStrides;

	// current position values
	private float currentXPosition;
	private float currentYPosition;
	private int heightStrideCounter;
	private int widthStrideCounter;
	private boolean hasNext = true;

	/**
	 * Tiles the provided imagefile to fit the logical sample pixel space & samples
	 * from imagefile as per sampling rate.
	 * 
	 * @param imagefile
	 * @param sampleHeight
	 * @param sampleWidth
	 * @param heightPerPixel
	 * @param widthPerPixel
	 * @param samplingRate
	 * @throws IOException
	 */
	public Abstract2DSample(File imagefile, float sampleHeight, float sampleWidth, float heightPerPixel,
			float widthPerPixel, Abstract2DSample.SamplingRate samplingRate) throws IOException {

		// defines image pixel attributes
		image = ImageIO.read(imagefile);
		imagePixelHeight = image.getHeight();
		imagePixelWidth = image.getWidth();

		// defines logical pixel attributes of sample
		this.sampleHeight = sampleHeight;
		this.sampleWidth = sampleWidth;
		this.heightPerPixel = heightPerPixel;
		this.widthPerPixel = widthPerPixel;

		this.samplingRate = samplingRate;
		recalculateTotalPixelsForSample();
	}

	public void reset() {
		recalculateTotalPixelsForSample();
		hasNext = true;
	}

	public void recalculateTotalPixelsForSample() {
		// calculate logical pixels in sample
		heightPixelCountForSample = (int) (sampleHeight / heightPerPixel);
		widthPixelCountForSample = (int) (sampleWidth / widthPerPixel);

		// count strides (in terms of logical sample pixels) it takes to cover entire
		// sample if sampling using given samplingRate
		totalHeightStrides = heightPixelCountForSample / samplingRate.getHeightPixelsPerStride();
		totalWidthStrides = widthPixelCountForSample / samplingRate.getWidthPixelsPerStride();
		totalStrides = totalHeightStrides * totalWidthStrides;

		// set current stride position in sample to 0
		heightStrideCounter = 0;
		widthStrideCounter = 0;
	}

	public Integer getNextPixel() {

		// if no value return indicating end of sample
		if (!hasNext())
			return null;

		// compute start position used by light rays
		computeCurrentPositionFromStride();

		// each pixel in sample corresponds to 1 image pixel hence map stride counter
		// over logical pixels to image pixels
		int y = heightStrideCounter % imagePixelHeight;
		int x = widthStrideCounter % imagePixelWidth;
		Integer pixelValue = image.getRGB(x, y);

		// increment widthStrideCounter stride counters. Loop over to next logical row
		// if overflows. If overflows all logical rows mark as scan complete.
		widthStrideCounter += samplingRate.getWidthPixelsPerStride();
		if (widthStrideCounter >= widthPixelCountForSample) {
			widthStrideCounter = 0;
			heightStrideCounter += samplingRate.getHeightPixelsPerStride();
			if (heightStrideCounter >= heightPixelCountForSample) {
				// scan of sample complete
				pixelValue = null;
				hasNext = false;
			}
		}

		return pixelValue;
	}

	@Override
	public int getTotalStrides() {
		return totalStrides;
	}

	@Override
	public float getSampleWidth() {
		return sampleWidth;
	}

	@Override
	public float getSampleHeight() {
		return sampleHeight;
	}

	@Override
	public void setSampleWidth(float width) {
		this.sampleWidth = width;
		recalculateTotalPixelsForSample();
	}

	@Override
	public void setSampleHeight(float height) {
		this.sampleHeight = height;
		recalculateTotalPixelsForSample();
	}

	@Override
	public float getWidthPerPixel() {
		return widthPerPixel;
	}

	@Override
	public float getHeightPerPixel() {
		return heightPerPixel;
	}

	@Override
	public void setWidthPerPixel(float width) {
		this.widthPerPixel = width;
		recalculateTotalPixelsForSample();
	}

	@Override
	public void setHeightPerPixel(float height) {
		this.heightPerPixel = height;
		recalculateTotalPixelsForSample();
	}

	@Override
	public void setSamplingRate(float heightRate, float widthRate) {
		samplingRate.setHeightPixelsPerStride(heightRate);
		samplingRate.setWidthPixelsPerStride(widthRate);
	}

	@Override
	public float[] getSamplingRate() {
		return new float[] { samplingRate.getHeightPixelsPerStride(), samplingRate.getWidthPixelsPerStride() };
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public float[] getPosition() {
		return new float[] { samplePosX, samplePosY, samplePosZ };
	}

	@Override
	public void setPosition(float x, float y, float z) {
		this.samplePosX = x;
		this.samplePosY = y;
		this.samplePosZ = z;
	}

	@Override
	public List<LightRay> generateNextSetofRays(List<Float> xAngleRange, List<Float> yAngleRange, LightSource source) {

		List<LightRay> output = new ArrayList<LightRay>();
		Map<Float, Float> wavelengthMap = source.getWavelenghtsAndIntensity();

		Integer colorValue = getNextPixel();
		if (colorValue != null) {
			for (Float xAngle : xAngleRange) {
				for (Float yAngle : yAngleRange) {
					Iterator<Float> iter = wavelengthMap.keySet().iterator();
					while (iter.hasNext()) {
						float waveLength = iter.next();
						float intensity = wavelengthMap.get(waveLength);

						Point startPos = new Point(currentXPosition, currentYPosition, samplePosZ);
						Point rayVector = Util.getUnitVectorFromAngles(xAngle, yAngle);
						LightRay ray = new LightRay(startPos, rayVector, waveLength, colorValue, intensity);
						output.add(ray);
					}
				}
			}
		}

		return output;
	}

	/**
	 * update current positions to indicate ray start position
	 */
	private void computeCurrentPositionFromStride() {
		// get current position after striding in terms of sample width & height (as a
		// continuous sample)
		currentXPosition = ((float) ((float) widthStrideCounter / (float) widthPixelCountForSample)) * sampleWidth;
		currentYPosition = ((float) ((float) heightStrideCounter / (float) heightPixelCountForSample)) * sampleHeight;

		// translate co-ordinates w.r.t center of sample
		currentXPosition -= (sampleWidth / 2.0f);
		currentYPosition -= (sampleHeight / 2.0f);

		// translate co-ordinates w.r.t global position of sample
		currentXPosition += samplePosX;
		currentYPosition += samplePosY;
	}

	/**
	 * Get pixel position starting from top left corner of sample when looking at
	 * sample facing the -ve z direction.
	 * 
	 * @param xpos
	 * @param ypos
	 * @return
	 */
	private Integer[] getSamplePixelPosFromGlobalPos(float xpos, float ypos) {

		// translate co-ordinates w.r.t global position of sample
		xpos -= samplePosX;
		ypos -= samplePosY;

		// translate co-ordinates w.r.t center of sample
		xpos += (sampleWidth / 2.0f);
		ypos += (sampleHeight / 2.0f);

		int widthPixel = (int) ((xpos / sampleWidth) * ((float) widthPixelCountForSample));
		int heightPixel = (int) ((ypos / sampleHeight) * ((float) heightPixelCountForSample));

		return new Integer[] { widthPixel, heightPixel };
	}

	@Override
	public BufferedImage getScaledImage(BoundedRegion2D<Float> sourceGenerationBounds, int width, int height) {

		// get logical pixel space points corresponding to min and max positions
		Integer[] minPoint = getSamplePixelPosFromGlobalPos(sourceGenerationBounds.minX, sourceGenerationBounds.minY);
		Integer[] maxPoint = getSamplePixelPosFromGlobalPos(sourceGenerationBounds.maxX, sourceGenerationBounds.maxY);

		// get logical pixel lengths
		int pixelLengthPerRow = maxPoint[0] - minPoint[0];
		int noOfRows = maxPoint[1] - minPoint[1];

		// get output image size after accounting for stride values & create output
		// image
		int outputWidth = pixelLengthPerRow / samplingRate.widthPixelsPerStride;
		int outputHeight = noOfRows / samplingRate.heightPixelsPerStride;

		BufferedImage output = null;
		if (outputWidth > 0 && outputHeight > 0) {
			output = new BufferedImage(outputWidth, outputHeight, image.getType());

			// set output image values
			for (int counter1 = 0; counter1 < pixelLengthPerRow; counter1 = counter1
					+ samplingRate.widthPixelsPerStride) {
				for (int counter2 = 0; counter2 < noOfRows; counter2 = counter2 + samplingRate.heightPixelsPerStride) {
					int pixelWidthPos = counter1 + minPoint[0];
					int pixelHeightPos = counter2 + minPoint[1];

					pixelWidthPos = pixelWidthPos % imagePixelWidth;
					pixelHeightPos = pixelHeightPos % imagePixelHeight;

					Integer pixelValue = image.getRGB(pixelWidthPos, pixelHeightPos);

					int xposInOutput = counter1 / samplingRate.widthPixelsPerStride;
					int yposInOutput = counter2 / samplingRate.heightPixelsPerStride;
					if (xposInOutput < outputWidth && yposInOutput < outputHeight)
						output.setRGB(xposInOutput, yposInOutput, pixelValue);
				}
			}

			output = Util.scaleImg(output, width, height);
		}

		return output;
	}

	public static class SamplingRate {
		int heightPixelsPerStride;
		int widthPixelsPerStride;

		public SamplingRate(int heightPixelsPerStride, int widthPixelsPerStride) {
			this.heightPixelsPerStride = heightPixelsPerStride;
			this.widthPixelsPerStride = widthPixelsPerStride;
		}

		public int getHeightPixelsPerStride() {
			return heightPixelsPerStride;
		}

		public void setHeightPixelsPerStride(int heightPixelsPerStride) {
			this.heightPixelsPerStride = heightPixelsPerStride;
		}

		public int getWidthPixelsPerStride() {
			return widthPixelsPerStride;
		}

		public void setWidthPixelsPerStride(int widthPixelsPerStride) {
			this.widthPixelsPerStride = widthPixelsPerStride;
		}

		public void setHeightPixelsPerStride(float heightPixelsPerStride) {
			this.heightPixelsPerStride = (int) heightPixelsPerStride;
		}

		public void setWidthPixelsPerStride(float widthPixelsPerStride) {
			this.widthPixelsPerStride = (int) widthPixelsPerStride;
		}
	}

	public int getType() {
		return image.getType();
	}
}
