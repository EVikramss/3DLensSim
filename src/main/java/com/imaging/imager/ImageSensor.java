package com.imaging.imager;

import java.awt.image.BufferedImage;
import java.util.List;

import com.imaging.geom.Point;
import com.imaging.ray.LightRay;

public interface ImageSensor {

	public BufferedImage interpret(List<LightRay> rays, boolean reEvaluate);
	
	public Point getPosition();
	
	public void setPosition(Point pos);
	
	public float[] getSize();
	
	public BufferedImage getInterpretedImage();
	
	public BufferedImage getBoundedImageForAnalysis();
	
	public List<LightRay> getMarginalRays();
}
