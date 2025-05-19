package com.imaging.imager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.imaging.color.ColorMixer;
import com.imaging.common.AxisEnum;
import com.imaging.common.Util;
import com.imaging.geom.Limit;
import com.imaging.geom.Plane;
import com.imaging.geom.Point;
import com.imaging.geom.Point2D;
import com.imaging.geom.region.BoundedRegion2D;
import com.imaging.ray.LightRay;

public class ImagePlaneImpl extends Plane implements ImageSensor {

	private BufferedImage img;
	private int imgHeight;
	private int imgWidth;

	private List<ImageIntersection> intersectionPoints;
	private BoundedRegion2D<Float> sensorIntersectionBounds;

	private int freqThreshold = 10; // all pixels with hits greater than this threshold represent the image area.
	private BoundedRegion2D<Integer> filteredBounds; // bounds after filtering as per freqThreshold

	private Map<Integer, Map<Integer, List<LightRay>>> pixelToRayMapping;
	List<LightRay> marginalRays = new ArrayList<LightRay>();

	private int sampleType;
	private ColorMixer colorMixer;

	public ImagePlaneImpl(float a, float b, float c, Limit xLimit, Limit yLimit, Limit zLimit, int imgHeight,
			int imgWidth, int sampleType, ColorMixer colorMixer) {
		super(a, b, c, xLimit, yLimit, zLimit);
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
		this.sampleType = sampleType;
		this.colorMixer = colorMixer;

		img = new BufferedImage(imgWidth, imgHeight, sampleType);

		// map to count freq. of ray intersections onto mapped 2D plane over sensor
		// surface.
		pixelToRayMapping = new HashMap<Integer, Map<Integer, List<LightRay>>>();

		// init region bound structures
		sensorIntersectionBounds = new BoundedRegion2D<Float>();
		filteredBounds = new BoundedRegion2D<Integer>();
	}

	@Override
	public BufferedImage interpret(List<LightRay> rays, boolean reEvaluate) {

		// initialize img and intersection freq map
		img = new BufferedImage(imgWidth, imgHeight, sampleType);
		pixelToRayMapping = new HashMap<Integer, Map<Integer, List<LightRay>>>();

		// get intersection points with image plane
		intersectionPoints = getIntersectionPoints(rays, reEvaluate);

		int countOfPoints = intersectionPoints.size();
		if (countOfPoints <= 2) {
			System.out.println("Need any atleast 3 intersection points");
		} else {
			// project points
			computeIntersectionPointsToPlaneSpace(countOfPoints);

			// map projected points onto Buffered Image (img)
			mapIntersectionPointsToImg();

			// compute marginal rays
			computeMarginalRays();
		}

		return img;
	}

	private List<ImageIntersection> getIntersectionPoints(List<LightRay> rays, boolean reEvaluate) {

		List<ImageIntersection> intersectionPoints = rays.parallelStream().map(ray -> {
			Point[] intersectionPoint = this.getIntersectionPoint(ray, reEvaluate);
			ImageIntersection imageIntersection = null;

			if (intersectionPoint[0] != null) {
				// for rays which intersect the image plane, set the stop pos as image plane
				// intersection point, ignore remaining rays
				ray.setStopPos(intersectionPoint[0]);

				imageIntersection = new ImageIntersection();
				imageIntersection.setPoint(intersectionPoint[0]);
				imageIntersection.setColor(ray.getColor());
				imageIntersection.setIntensity(ray.getIntensity());
				imageIntersection.setWavelength(ray.getWavelength());
				imageIntersection.setRay(ray);
			}

			return imageIntersection;
		}).filter(ip -> ip != null).collect(Collectors.toList());

		return intersectionPoints;
	}

	private void computeIntersectionPointsToPlaneSpace(int countOfPoints) {

		// get 3 points randomly from intersection plane
		List<Integer> indexList = Util.selectRandomUniqueIndices(3, countOfPoints);
		Point p0 = intersectionPoints.get(indexList.get(0)).getPoint();
		Point p1 = intersectionPoints.get(indexList.get(1)).getPoint();
		Point p2 = intersectionPoints.get(indexList.get(2)).getPoint();

		// generate u and v vectors on the plane
		Point v1 = Util.getVector(p0, p1);
		Point v2 = Util.getVector(p0, p2);

		Point planeNormal = Util.crossProduct(v1, v2);
		Point u = Util.crossProductNormalized(v1, planeNormal);
		Point v = Util.crossProductNormalized(planeNormal, u);

		// project intersection point onto u and v vectors
		intersectionPoints.stream().forEach(ip -> {
			Point p = ip.getPoint();
			float x = (p.getX() * u.getX()) + (p.getY() * u.getY()) + (p.getZ() * u.getZ());
			float y = (p.getX() * v.getX()) + (p.getY() * v.getY()) + (p.getZ() * v.getZ());

			sensorIntersectionBounds.evaluate(x, y);

			Point2D point2D = new Point2D(x, y);
			ip.setPoint2d(point2D);
		});
	}

	private void mapIntersectionPointsToImg() {

		Map<Integer, Map<Integer, List<Integer>>> colorAccumulator = new HashMap<Integer, Map<Integer, List<Integer>>>();

		// consolidate all rays at pixel locations into map
		for (int counter = 0; counter < intersectionPoints.size(); counter++) {
			ImageIntersection intersection = intersectionPoints.get(counter);
			Point2D point = intersection.getPoint2d();

			float x = point.getX();
			float y = point.getY();

			int i = (int) Util.linearMap(x, sensorIntersectionBounds.minX, sensorIntersectionBounds.maxX, 0.0f,
					(float) imgWidth);
			int j = (int) Util.linearMap(y, sensorIntersectionBounds.minY, sensorIntersectionBounds.maxY, 0.0f,
					(float) imgHeight);
			if (checkBounds(i, j)) {
				// update rays contributing to pixel
				Map<Integer, List<LightRay>> subMap = pixelToRayMapping.computeIfAbsent(i,
						e -> new HashMap<Integer, List<LightRay>>());
				List<LightRay> rayList = subMap.get(j);
				if (rayList == null) {
					rayList = new ArrayList<LightRay>();
					subMap.put(j, rayList);
				}
				rayList.add(intersection.getRay());

				// accumulate color into pixels
				Map<Integer, List<Integer>> colorSubMap = colorAccumulator.computeIfAbsent(i,
						e -> new HashMap<Integer, List<Integer>>());
				List<Integer> colorList = colorSubMap.get(j);
				if (colorList == null) {
					colorList = new ArrayList<Integer>();
					colorSubMap.put(j, colorList);
				}
				colorList.add(intersection.getRay().getColor());
			}
		}

		// iterate over colorAccumulator and set mixed colors on image
		Iterator<Integer> iter = colorAccumulator.keySet().iterator();
		while (iter.hasNext()) {
			Integer i = iter.next();
			Map<Integer, List<Integer>> colorSubMap = colorAccumulator.get(i);

			Iterator<Integer> iter2 = colorSubMap.keySet().iterator();
			while (iter2.hasNext()) {
				Integer j = iter2.next();
				List<Integer> colorList = colorSubMap.get(j);
				int finalColor = colorMixer.mixColors(colorList);
				img.setRGB(i, j, finalColor);
			}
		}

		/*
		 * File outputfile = new File("saved_image.png"); try { ImageIO.write(img,
		 * "png", outputfile); } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	private void computeMarginalRays() {

		filteredBounds = new BoundedRegion2D<Integer>();

		// iterate over pixelToRayMapping to identify bounds of image
		Iterator<Integer> iter = pixelToRayMapping.keySet().iterator();
		while (iter.hasNext()) {
			Integer i = iter.next();
			Map<Integer, List<LightRay>> subMap = pixelToRayMapping.get(i);

			Iterator<Integer> iter2 = subMap.keySet().iterator();
			while (iter2.hasNext()) {
				Integer j = iter2.next();
				List<LightRay> rayList = subMap.get(j);

				int freq = rayList.size();
				if (freq > freqThreshold) {
					// identify bounds of plane which have total rays contributing > freqThreshold
					filteredBounds.evaluate(i, j);
				}
			}
		}

		// return without further computation if no image
		if (filteredBounds.maxX == null)
			return;

		// iterate over pixelToRayMapping again to identify rays falling exactly on
		// filteredBounds
		marginalRays = new ArrayList<LightRay>();
		iter = pixelToRayMapping.keySet().iterator();
		while (iter.hasNext()) {
			Integer i = iter.next();
			Map<Integer, List<LightRay>> subMap = pixelToRayMapping.get(i);

			Iterator<Integer> iter2 = subMap.keySet().iterator();
			while (iter2.hasNext()) {
				Integer j = iter2.next();
				List<LightRay> rayList = subMap.get(j);
				// all rays falling on boundary - mark as marginal
				if (filteredBounds.compareTo(i, j) == 0) {
					marginalRays.addAll(rayList);
				}
			}
		}
	}

	private boolean checkBounds(int i, int j) {
		if (i >= 0 && i < imgWidth && j >= 0 && j < imgHeight)
			return true;
		return false;
	}

	public BufferedImage getImg() {
		return img;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgParams(int imgHeight, int imgWidth) {
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
		img = new BufferedImage(imgWidth, imgHeight, sampleType);
		pixelToRayMapping = new HashMap<Integer, Map<Integer, List<LightRay>>>();
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public void reImage() {
		img = new BufferedImage(imgWidth, imgHeight, sampleType);
		pixelToRayMapping = new HashMap<Integer, Map<Integer, List<LightRay>>>();
		mapIntersectionPointsToImg();
	}

	@Override
	public Point getPosition() {
		// TBM
		return new Point(0.0f, 0.0f, 1.0f / getC());
	}

	@Override
	public void setPosition(Point pos) {
		// TBM
		setC(1.0f / pos.getZ());
		setzLimit(new Limit(pos.getZ() + 0.5f, pos.getZ() - 0.5f, AxisEnum.Z));
	}

	@Override
	public float[] getSize() {
		Float xsize = null;
		Float ysize = null;

		if (sensorIntersectionBounds.maxX != null && sensorIntersectionBounds.minX != null
				&& sensorIntersectionBounds.maxY != null && sensorIntersectionBounds.minY != null) {
			xsize = sensorIntersectionBounds.maxX - sensorIntersectionBounds.minX;
			ysize = sensorIntersectionBounds.maxY - sensorIntersectionBounds.minY;
		}

		if (xsize == null || xsize == Float.NaN || ysize == null || ysize == Float.NaN) {
			xsize = 5.0f;
			ysize = xsize * ((float) imgHeight / (float) imgWidth);
		}

		return new float[] { xsize, ysize };
	}

	@Override
	public BufferedImage getInterpretedImage() {
		return img;
	}

	@Override
	public BufferedImage getBoundedImageForAnalysis() {

		BufferedImage boundedImage = null;

		if (filteredBounds != null && filteredBounds.maxX != null) {
			if (filteredBounds.maxX - filteredBounds.minX > 0 && filteredBounds.maxY - filteredBounds.minY > 0)
				boundedImage = img.getSubimage(filteredBounds.minX, filteredBounds.minY,
						filteredBounds.maxX - filteredBounds.minX, filteredBounds.maxY - filteredBounds.minY);
		} else {
			boundedImage = img;
		}

		return boundedImage;
	}

	@Override
	public List<LightRay> getMarginalRays() {
		return marginalRays;
	}
}
