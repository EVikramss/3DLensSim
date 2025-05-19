package com.imaging.geom.surface;

import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.objects.DefinedObject;
import com.imaging.ray.LightRay;

public interface ThreeDSurface extends DefinedObject {

	// evaluate such surface always passes through origin ? And also boundary passes through origin or z=0?
	// given x, y get z value from center of object 
	public Float evaluateCrossSection(float x, float y);
	
	public Intersection getIntersectionPoint(LightRay incomingRay, Point parentPosition, Point Point);
	
	Point getNormalAtPoint(Point output, Point parentPosition, Point objPosition);
}
