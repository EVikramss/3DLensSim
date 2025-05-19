package com.imaging.geom;

public class Plane {

	// Plane of the form ax + by + cz = 1
	private float a;
	private float b;
	private float c;
	private Limit xLimit;
	private Limit yLimit;
	private Limit zLimit;

	public Plane(float a, float b, float c, Limit xLimit, Limit yLimit, Limit zLimit) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.xLimit = xLimit;
		this.yLimit = yLimit;
		this.zLimit = zLimit;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getC() {
		return c;
	}

	public void setC(float c) {
		this.c = c;
	}

	public Limit getxLimit() {
		return xLimit;
	}

	public void setxLimit(Limit xLimit) {
		this.xLimit = xLimit;
	}

	public Limit getyLimit() {
		return yLimit;
	}

	public void setyLimit(Limit yLimit) {
		this.yLimit = yLimit;
	}

	public Limit getzLimit() {
		return zLimit;
	}

	public void setzLimit(Limit zLimit) {
		this.zLimit = zLimit;
	}

	/**
	 * Returns point array with 2 values - 1st value is non null if intersection is
	 * within plane limits otherwise null, vice-versa for 2nd value
	 * 
	 * @param line
	 * @param ignoreStopPos
	 * @return
	 */
	public Point[] getIntersectionPoint(LineSegment line, boolean ignoreStopPos) {

		Point intersectionPoint = null;

		Float maxK = line.getMaxDistAlongDir();
		Point lineStartPos = line.getStartPos();
		Point lineDirVec = line.getDirectionVector();
		boolean isIntWithinPlane = false;

		float num = (a * lineStartPos.getX()) + (b * lineStartPos.getY()) + (c * lineStartPos.getZ());
		float den = (a * lineDirVec.getX()) + (b * lineDirVec.getY()) + (c * lineDirVec.getZ());
		num = 1.0f - num;

		float kval = num / den;

		if (maxK != null && kval > maxK && !ignoreStopPos) {
			// Intersects beyond line segment - hence ignore
		} else {
			float xval = lineStartPos.getX() + (kval * lineDirVec.getX());
			float yval = lineStartPos.getY() + (kval * lineDirVec.getY());
			float zval = lineStartPos.getZ() + (kval * lineDirVec.getZ());
			intersectionPoint = new Point(xval, yval, zval);

			if ((xLimit == null || (xLimit != null && xLimit.withinLimit(xval)))
					&& (yLimit == null || (yLimit != null && yLimit.withinLimit(yval)))
					&& (zLimit == null || (zLimit != null && zLimit.withinLimit(zval))))
				isIntWithinPlane = true;
		}

		return new Point[] { isIntWithinPlane ? intersectionPoint : null, isIntWithinPlane ? null : intersectionPoint };
	}
}
