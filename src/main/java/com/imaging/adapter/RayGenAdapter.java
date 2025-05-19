package com.imaging.adapter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imaging.ray.RayGenerator;

@Service
public class RayGenAdapter {

	@Autowired
	private RayGenerator rayGenerator;
	
	public Map<Float, Float> getLightSourceSettings() {
		return rayGenerator.getLightSource().getWavelenghtsAndIntensity();
	}
	
	public float[] getAngleVariationSettings() {
		return rayGenerator.getRayRange();
	}
	
	public Integer getMaxBouncesOfReflectedRays() {
		return RayGenerator.getMaxInteractionsOfReflectedRays();
	}
}
