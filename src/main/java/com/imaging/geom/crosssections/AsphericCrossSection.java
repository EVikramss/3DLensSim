package com.imaging.geom.crosssections;

import java.util.Map;

import com.imaging.geom.Limit;

public class AsphericCrossSection implements CrossSection {

	private float radius;
	private float k;
	private float a2;
	private float a4;
	private float a6;
	private float a8;
	private Limit limit;

	public AsphericCrossSection() {
	}

	public AsphericCrossSection(float radius, float k, float a2, float a4, float a6, float a8, Limit Limit) {
		this.radius = radius;
		this.k = k;
		this.a2 = a2;
		this.a4 = a4;
		this.a6 = a6;
		this.a8 = a8;
		this.limit = Limit;
	}

	public Float evaluateCrossSection(float y) {

		if (!limit.withinLimit(y))
			return null;

		float temp1 = (y * y);
		float temp2 = (1.0f + k) * temp1 / (radius * radius);
		temp2 = 1.0f + (float) Math.sqrt(1.0f - temp2);
		temp2 = temp2 * radius;

		float z_val = (temp1 / temp2);

		// add polynomial terms
		if (a2 != 0.0f)
			z_val += a2 * temp1;

		if (a4 != 0.0f)
			z_val += a4 * temp1 * temp1;

		if (a6 != 0.0f)
			z_val += a6 * temp1 * temp1 * temp1;

		if (a8 != 0.0f)
			z_val += a8 * temp1 * temp1 * temp1 * temp1;

		return z_val;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	public float getA2() {
		return a2;
	}

	public void setA2(float a2) {
		this.a2 = a2;
	}

	public float getA4() {
		return a4;
	}

	public void setA4(float a4) {
		this.a4 = a4;
	}

	public float getA6() {
		return a6;
	}

	public void setA6(float a6) {
		this.a6 = a6;
	}

	public float getA8() {
		return a8;
	}

	public void setA8(float a8) {
		this.a8 = a8;
	}

	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit Limit) {
		this.limit = Limit;
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		Map<String, Object> map = Map.of("Type", "Aspheric", "radius", radius, "k", k, "a2", a2, "a4", a4, "a6", a6,
				"a8", a8, "limit", limit.toString());
		return map;
	}
}
