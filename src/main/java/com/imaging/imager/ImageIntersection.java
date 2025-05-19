package com.imaging.imager;

import com.imaging.geom.Point;
import com.imaging.geom.Point2D;
import com.imaging.ray.LightRay;

public class ImageIntersection {

	private Point point;
	private Point2D point2d;
	private float wavelength;
	private float color;
	private float intensity;
	private LightRay ray;

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public float getWavelength() {
		return wavelength;
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public float getColor() {
		return color;
	}

	public void setColor(float color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Point2D getPoint2d() {
		return point2d;
	}

	public void setPoint2d(Point2D point2d) {
		point = null; // save space if not needed
		this.point2d = point2d;
	}

	public LightRay getRay() {
		return ray;
	}

	public void setRay(LightRay ray) {
		this.ray = ray;
	}
}
