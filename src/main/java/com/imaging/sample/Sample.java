package com.imaging.sample;

import java.awt.image.BufferedImage;
import java.util.List;

import com.imaging.geom.region.BoundedRegion2D;
import com.imaging.light.LightSource;
import com.imaging.ray.LightRay;

public interface Sample {

	float getSampleWidth();

	float getSampleHeight();

	void setSampleWidth(float width);

	void setSampleHeight(float height);
	
	float getWidthPerPixel();

	float getHeightPerPixel();

	void setWidthPerPixel(float width);

	void setHeightPerPixel(float height);
	
	void setSamplingRate(float heightRate, float widthRate);
	
	float[] getSamplingRate();
	
	boolean hasNext();
	
	int getTotalStrides();
	
	void reset();
	
	float[] getPosition();

	void setPosition(float x, float y, float z);
	
	int getType();

	List<LightRay> generateNextSetofRays(List<Float> xAngleRange, List<Float> yAngleRange, LightSource source);

	BufferedImage getScaledImage(BoundedRegion2D<Float> sourceGenerationBounds, int width, int height);
}
