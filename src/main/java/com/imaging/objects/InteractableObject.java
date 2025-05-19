package com.imaging.objects;

import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.ray.LightRay;

public interface InteractableObject {

	public void propagateRay(LightRay ray, Point objectParentPosition, RefractionIndex indexBefore, RefractionIndex indexAfter);
	public Intersection getIntersectionPoint(LightRay ray, Point objectParentPosition);
}
