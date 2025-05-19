package com.imaging.light.refraction;

public class SimpleRefractionIndex implements RefractionIndex {

	private float constantIndex;
	
	public SimpleRefractionIndex(float constantIndex) {
		this.constantIndex = constantIndex;
	}
	
	@Override
	public float getIndexOfRefractionFor(float wavelength) {
		return constantIndex;
	}

	public float getConstantIndex() {
		return constantIndex;
	}

	public void setConstantIndex(float constantIndex) {
		this.constantIndex = constantIndex;
	}
}
