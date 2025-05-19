package com.imaging.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.imaging.adapter.CompositeAdapter;
import com.imaging.adapter.SetupAdapter;
import com.imaging.adapter.SolutionAdapter;
import com.imaging.adapter.object.NestedObjectRepresentation;
import com.imaging.geom.Point;

@RestController
public class ActionController {

	@Autowired
	private SolutionAdapter solutionAdapter;

	@Autowired
	private SetupAdapter setupAdapter;

	@Autowired
	private CompositeAdapter compositeAdapter;

	/*
	 * public ActionController(SolutionAdapter solutionAdapter, SetupAdapter
	 * setupAdapter, CompositeAdapter compositeAdapter) { this.solutionAdapter =
	 * solutionAdapter; this.setupAdapter = setupAdapter; this.compositeAdapter =
	 * compositeAdapter; }
	 */

	@GetMapping("/generateSolution")
	public ResponseEntity<String> execute() {
		return ResponseEntity.ok("{\"message\":\"" + solutionAdapter.solve() + "\"}");
	}

	@PostMapping("/updateSensorPosition")
	public List<Object> updateSensorPosition(@RequestBody Point newPos) throws IOException {
		// TBM for sensor position update
		setupAdapter.updateSensorPosition(newPos);
		return solutionAdapter.reInterpretRaysForSensorUpdate();
	}

	@PostMapping("/updateComposite")
	public ResponseEntity<String> updateComposite(@RequestBody NestedObjectRepresentation object) throws IOException {
		compositeAdapter.updateComposite(object);
		return ResponseEntity.ok("{\"message\":\"" + solutionAdapter.solve() + "\"}");
	}
}
