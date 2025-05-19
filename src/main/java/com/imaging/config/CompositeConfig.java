package com.imaging.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.imaging.geom.Point;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.objects.InteractableCompositeObject;

@Configuration
public class CompositeConfig {

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public InteractableCompositeObject configureComposite() throws IOException {
		return new BasicSetup(Point.ORIGIN, RefractionIndex.AIR);
	}
}
