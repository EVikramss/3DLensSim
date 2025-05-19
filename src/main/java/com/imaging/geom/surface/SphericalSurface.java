package com.imaging.geom.surface;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.imaging.common.Util;
import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.ray.LightRay;

public class SphericalSurface implements ThreeDSurface {

	private static final Logger LOGGER = LogManager.getLogger(SphericalSurface.class);

	private float radius;
	private float sign;
	private BoundedRegion3D<Float> region;
	private float margin = 0.2f;
	private Point origin = null;

	public SphericalSurface(float radius, float sign, BoundedRegion3D<Float> region) {
		if (radius < 0.0f) {
			LOGGER.info("Negative radius passed, converting to positive");
			radius = Math.abs(radius);
		}
		this.radius = radius;

		if (sign == 0.0f) {
			LOGGER.info("Sign value of 0 passed, treating it as positive");
			sign = 1.0f;
		}
		this.sign = sign;

		this.region = getRegion(region);
		origin = new Point(0.0f, 0.0f, sign * radius);
	}

	/**
	 * Selects the smaller of the 2 regions - 'provided' and 'max region' over which
	 * sphere is defined
	 * 
	 * @param providedRegion
	 * @return
	 */
	private BoundedRegion3D<Float> getRegion(BoundedRegion3D<Float> providedRegion) {

		BoundedRegion3D<Float> output = null;

		BoundedRegion3D<Float> maxRegion = new BoundedRegion3D<Float>();
		maxRegion.maxX = radius;
		maxRegion.minX = -radius;
		maxRegion.maxY = radius;
		maxRegion.minY = -radius;

		if (sign < 0) {
			maxRegion.maxZ = radius;
			maxRegion.minZ = 0.0f;
		} else {
			maxRegion.maxZ = 0.0f;
			maxRegion.minZ = -radius;
		}

		if (providedRegion == null)
			output = maxRegion;
		else {
			output = maxRegion.surrounds(providedRegion) ? providedRegion : maxRegion;
		}

		return output;
	}

	// @formatter:off
	/**
	 * 		sign=1			  			sign=-1
	 * 
	 *        		    )	|   (
	 *       			 )	|  (
	 *       			  )	| (
	 *       			   )|(
	 *----(z0=-radius)------o-------(z0=radius)------
	 *       			   )|(
	 *       			  )	| (
	 *       			 )	|  (
	 *       			)	|   (
	 */
	// @formatter:on
	@Override
	public Float evaluateCrossSection(float x, float y) {

		if (!region.withinBoundsXY(x, y, margin))
			return null;

		Float z_val = null;
		float innerRadiusTerm = (radius * radius);
		float innerTerm = innerRadiusTerm - ((y * y) + (x * x));
		if (innerTerm > 0) {
			if (sign > 0)
				// z_val = -radius + (float) (Math.sqrt(innerTerm));
				z_val = (float) (Math.sqrt(innerTerm));
			else
				// z_val = radius - (float) (Math.sqrt(innerTerm));
				z_val = - (float) (Math.sqrt(innerTerm));
		} else {
			z_val = Float.NaN;
		}

		return z_val;
	}

	@Override
	public Intersection getIntersectionPoint(LightRay incomingRay, Point parentPosition, Point objPosition) {

		Point rayVector = incomingRay.getDirectionVector();
		Point rayStartPos = incomingRay.getStartPos();
		Point objectGlobalPos = new Point(parentPosition).add(objPosition).add(origin);
		Intersection intersection = null;
		Point intersectionPoint = null;

		float xo = rayStartPos.getX() - objectGlobalPos.getX();
		float yo = rayStartPos.getY() - objectGlobalPos.getY();
		float zo = rayStartPos.getZ() - objectGlobalPos.getZ();

		float xu = rayVector.getX();
		float yu = rayVector.getY();
		float zu = rayVector.getZ();

		float a = (xu * xu) + (yu * yu) + (zu * zu);
		float b = 2 * ((xu * xo) + (yu * yo) + (zu * zo));
		float c = (xo * xo) + (yo * yo) + (zo * zo) - (radius * radius);

		List<Float> solutions = Util.solveQuadraticEquation(a, b, c);
		if (solutions.size() > 0) {
			BoundedRegion3D<Float> translatedRegion = Util.translateRegion(region, objectGlobalPos);

			List<Point> pointSolutions = solutions.stream()
					.map(k -> new Point(rayStartPos).add(new Point(rayVector).multiply(k)))
					.filter(p -> translatedRegion.withinBoundsXYZ(p.getX(), p.getY(), p.getZ(), margin))
					.collect(Collectors.toList());

			if (pointSolutions.size() == 2) {
				// get the nearest of the 2 points
				intersectionPoint = Util.getNearestPoint(rayStartPos, pointSolutions.get(0), pointSolutions.get(1));
			} else if (pointSolutions.size() == 1) {
				intersectionPoint = pointSolutions.get(0);
			} else {
				// no solution
			}
		}

		if (intersectionPoint != null) {
			Point normal = new Point();
			normal.setX(intersectionPoint.getX() - objectGlobalPos.getX());
			normal.setY(intersectionPoint.getY() - objectGlobalPos.getY());
			normal.setZ(intersectionPoint.getZ() - objectGlobalPos.getZ());
			normal = Util.normalize(normal);

			intersection = new Intersection();
			intersection.setIntersectionPoint(intersectionPoint);
			intersection.setNormalVectorAtIntersection(normal);
		}

		return intersection;
	}

	@Override
	public Point getNormalAtPoint(Point output, Point parentPosition, Point objPosition) {
		Point objectGlobalPos = new Point(parentPosition).add(objPosition).add(origin);
		Point normal = new Point();
		normal.setX(output.getX() - objectGlobalPos.getX());
		normal.setY(output.getY() - objectGlobalPos.getY());
		normal.setZ(output.getZ() - objectGlobalPos.getZ());
		normal = Util.normalize(normal);
		return normal;
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		Map<String, Object> map = Map.of("radius", radius, "sign", sign, "region", region.toString());
		return map;
	}

	@Override
	public void updateDefiningAttributes(Map<String, Object> attributes) {
		if (attributes.containsKey("radius")) {
			Double radius = (Double) attributes.get("radius");
			setRadius(radius.floatValue());
		} else if (attributes.containsKey("sign")) {
			Double sign = (Double) attributes.get("sign");
			setSign(sign.floatValue());
		} else if (attributes.containsKey("region")) {
			String strRegion = (String) attributes.get("region");
			BoundedRegion3D<Float> region = BoundedRegion3D.parseStringAsFloat(strRegion);
			if (region != null) {
				setRegion(region);
			}
		}
	}

	@Override
	public BoundedRegion3D<Float> getCurrentBoundedRegion() {
		return region.clone();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getSign() {
		return sign;
	}

	public void setSign(float sign) {
		this.sign = sign;
	}

	public BoundedRegion3D<Float> getRegion() {
		return region;
	}

	public void setRegion(BoundedRegion3D<Float> region) {
		this.region = region;
	}
}
