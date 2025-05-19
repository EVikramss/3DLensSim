package com.imaging.adapter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imaging.adapter.object.LineSegmentBundle;
import com.imaging.common.Util;
import com.imaging.geom.Point;
import com.imaging.imager.ImageSensor;
import com.imaging.ray.LightRay;
import com.imaging.solver.ImageSolver;

@Service
public class SolutionAdapter {

	@Autowired
	private ImageSolver solver;

	@Autowired
	private ImageSensor imageSensor;

	public boolean isComputationRunning() {
		return solver.isComputationRunning();
	}

	public List<LineSegmentBundle> getAllRays() {
		List<LightRay> rays = solver.getAllRays();
		Map<LightRay, Integer> rootRayMap = Util.getRootRays(rays);
		List<LineSegmentBundle> allRays = rootRayMap.keySet().stream().map(r -> new LineSegmentBundle(r))
				.collect(Collectors.toList());
		return allRays;
	}

	public List<LineSegmentBundle> getSampledRays(int maxValues) {
		List<LightRay> rays = solver.getAllRays();
		Map<LightRay, Integer> rootRayMap = Util.getRootRays(rays);
		List<LineSegmentBundle> allRays = new ArrayList<LineSegmentBundle>();

		if (rays.size() > 0) {
			Double averageTotalChildrenPerRay = rootRayMap.keySet().stream().mapToInt(k -> rootRayMap.get(k)).average()
					.getAsDouble();
			int rayCount = (int) (((double) maxValues) / averageTotalChildrenPerRay);
			int samplingFreq = rootRayMap.size() / rayCount;
			
			if(samplingFreq == 0)
				samplingFreq = 1;

			Iterator<LightRay> iter = rootRayMap.keySet().iterator();
			int counter = 0;
			int rayCounter = 0;
			while (iter.hasNext()) {
				LightRay ray = iter.next();
				if (counter % samplingFreq == 0) {
					LineSegmentBundle bundle = new LineSegmentBundle(ray);
					rayCounter += bundle.getPoints().size() / 2;
					allRays.add(bundle);

					if (rayCounter > maxValues)
						break;
				}
				counter++;
			}
		}
		return allRays;
	}

	public List<Object> getGeneratedImage() throws IOException {

		Point pos = imageSensor.getPosition();
		float[] size = imageSensor.getSize();
		byte[] byteArr = null;

		BufferedImage image = solver.getOutputImage();
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			byteArr = baos.toByteArray();
		}

		return Arrays.asList(new Object[] { pos, byteArr, size });
	}

	public String solve() {
		return solver.solve();
	}

	public List<Object> reInterpretRaysForSensorUpdate() throws IOException {

		BufferedImage image = solver.reInterpretRaysForSensorUpdate();
		byte[] byteArr = null;
		Point pos = imageSensor.getPosition();
		float[] size = imageSensor.getSize();

		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			byteArr = baos.toByteArray();
		}

		return Arrays.asList(new Object[] { pos, byteArr, size });
	}

}
