package com.imaging.geom;

import com.imaging.common.PlaneEnum;

/**
 * Circlular plane of the forms 1) (x-x0)^2 + (y-y0)^2 = r^2 at z = z0 2)
 * (y-y0)^2 + (z-z0)^2 = r^2 at x = x0 3) (z-z0)^2 + (x-x0)^2 = r^2 at y = y0
 */
public class CircularPlane {

	private Point center;
	private float radius;
	private PlaneEnum planeEnum;

	public CircularPlane(Point center, float radius, PlaneEnum planeEnum) {
		this.center = center;
		this.radius = radius;
		this.planeEnum = planeEnum;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public PlaneEnum getPlaneEnum() {
		return planeEnum;
	}

	public void setPlaneEnum(PlaneEnum planeEnum) {
		this.planeEnum = planeEnum;
	}

	/**
	 * Returns point array with 2 values - 1st value is non null if intersection is
	 * within plane limits otherwise null, vice-versa for 2nd value
	 * 
	 * @param line
	 * @param ignoreStopPos
	 * @return
	 */
	public Point[] getIntersectionPoint(LineSegment line) {

		Float maxK = line.getMaxDistAlongDir();
		Point lineStartPos = line.getStartPos();
		Point lineDirVec = line.getDirectionVector();

		Point intersectionPoint = null;
		boolean isIntWithinCircle = false;
		float kval = 0;

		switch (planeEnum) {
		case XY:
			kval = (center.getZ() - lineStartPos.getZ()) / lineDirVec.getZ();
			float xint = lineStartPos.getX() + (kval * lineDirVec.getX());
			float yint = lineStartPos.getY() + (kval * lineDirVec.getY());
			float value = ((xint - center.getX()) * (xint - center.getX()))
					+ ((yint - center.getY()) * (yint - center.getY()));
			if (value < radius * radius) {
				// isIntWithinCircle = true;
				intersectionPoint = new Point(xint, yint, center.getZ());
			}
			break;
		case YZ:
			kval = (center.getX() - lineStartPos.getX()) / lineDirVec.getX();
			yint = lineStartPos.getY() + (kval * lineDirVec.getY());
			float zint = lineStartPos.getZ() + (kval * lineDirVec.getZ());
			value = ((zint - center.getZ()) * (zint - center.getZ()))
					+ ((yint - center.getY()) * (yint - center.getY()));
			if (value < radius * radius) {
				// isIntWithinCircle = true;
				intersectionPoint = new Point(center.getX(), yint, zint);
			}
			break;
		case ZX:
			kval = (center.getY() - lineStartPos.getY()) / lineDirVec.getY();
			xint = lineStartPos.getX() + (kval * lineDirVec.getX());
			zint = lineStartPos.getZ() + (kval * lineDirVec.getZ());
			value = ((xint - center.getX()) * (xint - center.getX()))
					+ ((zint - center.getZ()) * (zint - center.getZ()));
			if (value < radius * radius) {
				// isIntWithinCircle = true;
				intersectionPoint = new Point(xint, center.getY(), zint);
			}
			break;
		}

		if (maxK != null && kval > maxK) {
			// Intersects beyond line segment - hence ignore
			intersectionPoint = null;
		}

		return new Point[] { isIntWithinCircle ? intersectionPoint : null,
				isIntWithinCircle ? null : intersectionPoint };
	}
}
