package com.imaging.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.imaging.geom.Limit;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.ray.LightRay;

public class Util {

	public static Point getUnitVectorFromAngles(float angleX, float angleZ) {

		double temp1 = Math.sin(angleZ * Math.PI / 180.0);

		float z = (float) Math.cos(angleZ * Math.PI / 180.0);
		float x = (float) (temp1 * Math.cos(angleX * Math.PI / 180.0));
		float y = (float) (temp1 * Math.sin(angleX * Math.PI / 180.0));

		return new Point(x, y, z);
	}

	/**
	 * Solve equation of the form: a*(x^2) + b*(x) + c = 0
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static List<Float> solveQuadraticEquation(float a, float b, float c) {

		List<Float> output = new ArrayList<Float>();

		float determinent = (b * b) - (4 * a * c);
		if (determinent < 0.0f) {
			// no solution in real plane
		} else if (determinent == 0.0f) {
			float solution1 = (-b) / (2 * a);
			output.add(solution1);
		} else {
			determinent = (float) Math.sqrt(determinent);
			float solution1 = ((-b) + determinent) / (2 * a);
			float solution2 = ((-b) - determinent) / (2 * a);
			output.add(solution1);
			output.add(solution2);
		}

		return output;
	}

	public static boolean isPointWithinLimit(Limit xLimit, Limit yLimit, Point p) {

		boolean withinLimit = false;

		float x = p.getX();
		float y = p.getY();

		if (xLimit.withinLimit(x) && yLimit.withinLimit(y)) {
			withinLimit = true;
		}

		return withinLimit;
	}

	public static Point getNearestPoint(Point obsPoint, Point point1, Point point2) {

		float distance1 = distanceBtwPoints(obsPoint, point1);
		float distance2 = distanceBtwPoints(obsPoint, point2);

		return distance1 >= distance2 ? point2 : point1;
	}

	public static float distanceBtwPoints(Point a, Point b) {
		float xdiff = a.getX() - b.getX();
		float ydiff = a.getY() - b.getY();
		float zdiff = a.getZ() - b.getZ();
		return (float) Math.sqrt((xdiff * xdiff) + (ydiff * ydiff) + (zdiff * zdiff));
	}

	public static Point normalize(Point normal) {
		return new Point(normal).divide(length(normal));
	}

	public static float length(Point p) {
		return (float) Math.sqrt((p.getX() * p.getX()) + (p.getY() * p.getY()) + (p.getZ() * p.getZ()));
	}

	/**
	 * Returns value in radians & not degrees
	 * 
	 * @param a
	 * @param b
	 * @param bothVectorsNormalized
	 * @return
	 */
	public static float getAngleBetweenVectors(Point a, Point b, boolean bothVectorsNormalized) {
		if (!bothVectorsNormalized) {
			a = normalize(a);
			b = normalize(b);
		}

		float val = getDotProduct(a, b);
		val = (float) Math.acos(val);

		return val;
	}

	public static float getDotProduct(Point a, Point b) {
		return (a.getX() * b.getX()) + (a.getY() * b.getY()) + (a.getZ() * b.getZ());
	}

	public static float[] getAnglesFromVector(Point p) {
		float x = p.getX();
		float y = p.getY();
		float z = p.getZ();

		double r = Math.sqrt(x * x + y * y + z * z);

		// Calculate the angle with the x-axis (azimuthal angle, phi)
		double phi = Math.atan2(y, x);

		// Calculate the angle with the z-axis (polar angle, theta)
		double theta = Math.acos(z / r);

		// Convert angles from radians to degrees
		float angleWRTX = (float) Math.toDegrees(phi);
		float angleWRTZ = (float) Math.toDegrees(theta);

		return new float[] { angleWRTX, angleWRTZ };
	}

	public static Point getVector(Point p0, Point p1) {
		return new Point(p0.getX() - p1.getX(), p0.getY() - p1.getY(), p0.getZ() - p1.getZ());
	}

	public static Point crossProduct(Point v1, Point v2) {
		float x = (v1.getY() * v2.getZ()) - (v1.getZ() * v2.getY());
		float y = (v1.getZ() * v2.getX()) - (v1.getX() * v2.getZ());
		float z = (v1.getX() * v2.getY()) - (v1.getY() * v2.getX());
		return new Point(x, y, z);
	}

	public static Point crossProductNormalized(Point v1, Point v2) {
		float x = (v1.getY() * v2.getZ()) - (v1.getZ() * v2.getY());
		float y = (v1.getZ() * v2.getX()) - (v1.getX() * v2.getZ());
		float z = (v1.getX() * v2.getY()) - (v1.getY() * v2.getX());
		return Util.normalize(new Point(x, y, z));
	}

	public static float linearMap(float x, Float minX, Float maxX, Float minMappedVal, Float maxMappedVal) {
		float slope = (maxMappedVal - minMappedVal) / (maxX - minX);
		return minMappedVal + (slope * (x - minX));
	}

	public static List<Integer> selectRandomUniqueIndices(int count, int maxValueNotInclusive) {

		List<Integer> output = new ArrayList<Integer>();
		int maxTries = 1000;

		Random rand = new Random(System.nanoTime());
		while (count > 0 && maxTries > 0) {
			Integer value = rand.nextInt(maxValueNotInclusive);
			if (!output.contains(value)) {
				output.add(value);
				count--;
			}
			maxTries--;
		}

		if (count > 0) {
			output = new ArrayList<Integer>();
			for (int counter = 0; counter < count; counter++) {
				output.add(counter);
			}
		}

		return output;
	}

	public static Map<LightRay, Integer> getRootRays(List<LightRay> rays) {
		Map<LightRay, Integer> rootRayMap = new LinkedHashMap<LightRay, Integer>();
		for (int counter = 0; counter < rays.size(); counter++) {
			LightRay ray = rays.get(counter);
			addRootRays(ray, rootRayMap, 0);
		}
		return rootRayMap;
	}

	private static void addRootRays(LightRay ray, Map<LightRay, Integer> rootRayMap, Integer count) {
		LightRay parentRay = ray.getParentRay();
		if (parentRay != null)
			addRootRays(parentRay, rootRayMap, count + parentRay.getChildRays().size());
		else
			rootRayMap.put(ray, count);
	}

	public static boolean containsNaN(Point p) {
		return p.getX() == Float.NaN || p.getY() == Float.NaN || p.getZ() == Float.NaN;
	}

	public static float[] getHSV(int val) {
		int red = (val >> 16) & 0xFF;
		int green = (val >> 8) & 0xFF;
		int blue = val & 0xFF;
		float[] hsv = Color.RGBtoHSB(red, green, blue, null);
		return hsv;
	}

	public static int getRGBInt(float hue, float saturation, float value) {
		int rgbVal = Color.HSBtoRGB(hue, saturation, value);
		return rgbVal;
	}

	/**
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImg(BufferedImage image, int width, int height) {
		Image scaledImg = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		image = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(scaledImg, 0, 0, null);
		g2d.dispose();
		return image;
	}

	public static LightRay getRootRay(LightRay ray) {
		while (ray.getParentRay() != null) {
			ray = ray.getParentRay();
		}
		return ray;
	}

	public static BoundedRegion3D<Float> translateRegion(BoundedRegion3D<Float> region, Point position) {
		if(region != null && position != null) {
			BoundedRegion3D<Float> output = region.clone();
			output.minX += position.getX();
			output.maxX += position.getX();
			output.minY += position.getY();
			output.maxY += position.getY();
			output.minZ += position.getZ();
			output.maxZ += position.getZ();
			return output;	
		} else {
			return region;
		}
	}
}
