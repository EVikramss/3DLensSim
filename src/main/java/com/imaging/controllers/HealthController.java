package com.imaging.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	boolean isHealthy = true;
	
	@GetMapping("/health")
	public ResponseEntity<String> getHealth() {
		ResponseEntity<String> response = null;
		if(isHealthy) {
			response = new ResponseEntity<>("healthy", HttpStatus.ACCEPTED);
		} else {
			response = new ResponseEntity<>("unhealthy", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	@GetMapping("/flipHealth")
	public String flipHealth() {
		isHealthy = !isHealthy;
		return "success";
	}
}
