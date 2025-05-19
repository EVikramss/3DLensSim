package com.imaging.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imaging.geom.Point;
import com.imaging.imager.ImageSensor;

@Service
public class SetupAdapter {

	@Autowired
	private ImageSensor imageSensor;

	public void updateSensorPosition(Point newPos) {
		imageSensor.setPosition(newPos);
	}
}
