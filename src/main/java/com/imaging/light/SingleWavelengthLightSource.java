package com.imaging.light;

import java.util.HashMap;
import java.util.Map;

public class SingleWavelengthLightSource implements LightSource {
	
	private Map<Float, Float> wavelengthIntensityMap = new HashMap<Float, Float>();
	
	public SingleWavelengthLightSource(float wavelength, float intensity) {
		wavelengthIntensityMap.put(wavelength, intensity);
	}
	
	public void changeLightSourceProp(float wavelength, float intensity) {
		wavelengthIntensityMap.clear();
		wavelengthIntensityMap.put(wavelength, intensity);
	}

	@Override
	public Map<Float, Float> getWavelenghtsAndIntensity() {
		return wavelengthIntensityMap;
	}
}
