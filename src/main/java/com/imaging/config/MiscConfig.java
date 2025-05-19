package com.imaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MiscConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Increase the maximum nesting depth
		objectMapper.getFactory()
				.setStreamWriteConstraints(StreamWriteConstraints.builder().maxNestingDepth(20000).build());
		return objectMapper;
	}
}
