package com.imaging.config;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.imaging.color.ColorMixer;
import com.imaging.color.HSVMixer;
import com.imaging.common.AxisEnum;
import com.imaging.geom.Limit;
import com.imaging.imager.ImagePlaneImpl;
import com.imaging.imager.ImageSensor;
import com.imaging.light.LightSource;
import com.imaging.light.SingleWavelengthLightSource;
import com.imaging.sample.Abstract2DSample;
import com.imaging.sample.ImageSample2DImpl;
import com.imaging.sample.Sample;

@Configuration
public class ImageSensorAndSourceConfig {

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ImageSensor configureImagingPlane(Sample sample) {
		return new ImagePlaneImpl(0, 0, 1.0f / 14.0f, new Limit(1.5f, -1.5f, AxisEnum.X),
				new Limit(1.5f, -1.5f, AxisEnum.Y), new Limit(10.5f, 9.5f, AxisEnum.Z), 600, 939, sample.getType(),
				configureColorMixer());
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Sample configureSample() throws IOException {
		return new ImageSample2DImpl(
				new File("study.jpg"), 3.13f, 2.0f,
				0.003333f, 0.003333f, new Abstract2DSample.SamplingRate(2, 2));
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public LightSource configureLightSource() {
		return new SingleWavelengthLightSource(700.0f, 10.0f);
	}

	@Bean
	public ColorMixer configureColorMixer() {
		return new HSVMixer();
	}
}
