package com.imaging.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imaging.adapter.CompositeAdapter;
import com.imaging.adapter.RayGenAdapter;
import com.imaging.adapter.SolutionAdapter;
import com.imaging.adapter.object.LineSegmentBundle;
import com.imaging.adapter.object.NestedObjectRepresentation;

@RestController
public class DataProviderController {

	@Autowired
	private RayGenAdapter rayGenAdapter;

	@Autowired
	private SolutionAdapter solutionAdapter;

	@Autowired
	private CompositeAdapter compositeAdapter;

	@GetMapping("/data/getLightSourceSettings")
	public Map<Float, Float> getLightSourceSettings() {
		return rayGenAdapter.getLightSourceSettings();
	}

	@GetMapping("/data/getAngleVariationSettings")
	public Map<Float, Float> getAngleVariationSettings() {
		return rayGenAdapter.getLightSourceSettings();
	}

	@GetMapping("/data/getMaxBouncesOfReflectedRays")
	public Integer getMaxBouncesOfReflectedRays() {
		return rayGenAdapter.getMaxBouncesOfReflectedRays();
	}

	@GetMapping("/data/getSampledRays")
	public List<LineSegmentBundle> getSampledRays(@RequestParam int maxValues) {
		return solutionAdapter.getSampledRays(maxValues);
	}

	@GetMapping("/data/getParentComposite")
	public NestedObjectRepresentation getParentComposite(@RequestParam Float xdiv, @RequestParam Float ydiv) {
		return compositeAdapter.getParentComposite(xdiv, ydiv);
	}

	@GetMapping("/data/getGeneratedImage")
	public List<Object> getGeneratedImage() throws IOException {
		return solutionAdapter.getGeneratedImage();
	}
}
