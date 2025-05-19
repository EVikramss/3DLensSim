package com.imaging.compute;

import com.imaging.geom.Point;

public class Intersection {

	private Point intersectionPoint;
	private Point normalVectorAtIntersection;
	private float angleAtInterSection;

	public Point getIntersectionPoint() {
		return intersectionPoint;
	}

	public void setIntersectionPoint(Point intersectionPoint) {
		this.intersectionPoint = intersectionPoint;
	}

	public Point getNormalVectorAtIntersection() {
		return normalVectorAtIntersection;
	}

	public void setNormalVectorAtIntersection(Point normalVectorAtIntersection) {
		this.normalVectorAtIntersection = normalVectorAtIntersection;
	}

	public float getAngleAtInterSection() {
		return angleAtInterSection;
	}

	public void setAngleAtInterSection(float angleAtInterSection) {
		this.angleAtInterSection = angleAtInterSection;
	}
}
