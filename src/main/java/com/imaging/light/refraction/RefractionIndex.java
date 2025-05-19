package com.imaging.light.refraction;

public interface RefractionIndex {

	static final SimpleRefractionIndex AIR = new SimpleRefractionIndex(1.000293f);
	
	public float getIndexOfRefractionFor(float wavelength);
}
