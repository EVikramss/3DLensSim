package com.imaging.geom.crosssections;

import java.util.Map;

import com.imaging.geom.Limit;

public class SphericalCrossSection implements CrossSection {

	private float radius;
	private Limit limit;

	public SphericalCrossSection() {
	}

	public SphericalCrossSection(float radius, float k, float a2, float a4, float a6, float a8, Limit limit) {
		this.radius = radius;
		this.limit = limit;
	}

	public Float evaluateCrossSection(float y) {

		if (!limit.withinLimit(y))
			return null;

		float z_val = radius + (float) Math.sqrt((radius * radius) - (y * y));
		return z_val;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	@Override
	public float getK() {
		return 0;
	}

	@Override
	public void setK(float k) {

	}

	@Override
	public float getA2() {
		return 0;
	}

	@Override
	public void setA2(float a2) {

	}

	@Override
	public float getA4() {
		return 0;
	}

	@Override
	public void setA4(float a4) {

	}

	@Override
	public float getA6() {
		return 0;
	}

	@Override
	public void setA6(float a6) {

	}

	@Override
	public float getA8() {
		return 0;
	}

	@Override
	public void setA8(float a8) {

	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		Map<String, Object> map = Map.of("Type", "Spherical", "radius", radius, "limit", limit.toString());
		return map;
	}
}
