package com.imaging.objects.impl;

import java.util.Map;

import com.imaging.common.PlaneEnum;
import com.imaging.geom.CircularPlane;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.objects.ObjectTypeEnum;
import com.imaging.ray.LightRay;

public class CircularAperture extends InteractableCompositeObject {

	private CircularPlane circularPlane;

	public CircularAperture(Point position, float radius, PlaneEnum planeEnum) {
		super(position, ObjectTypeEnum.CIRCULAR_APERTURE, null);
		this.circularPlane = new CircularPlane(position, radius, planeEnum);
	}

	public void setRadius(float radius) {
		circularPlane.setRadius(radius);
	}

	public float getRadius() {
		return circularPlane.getRadius();
	}

	@Override
	public void setPosition(Point position) {
		circularPlane.setCenter(position);
		super.setPosition(position);
	}

	public PlaneEnum getPlane() {
		return circularPlane.getPlaneEnum();
	}

	@Override
	public void propagateRay(LightRay ray, Point position, RefractionIndex indexBefore, RefractionIndex indexAfter) {
		Point[] intersectionPoint = circularPlane.getIntersectionPoint(ray);
		if (intersectionPoint[1] != null) {
			// ray intersects plane outside limit, stop ray and don't generate any child
			// rays
			ray.setStopPos(intersectionPoint[1], getId());
		} else if (intersectionPoint[0] != null) {
			// ray intersects plane inside limit
			ray.setStopPos(intersectionPoint[0], getId());
			new LightRay(ray, intersectionPoint[0], ray.getDirectionVector(), ray.getWavelength(), ray.getColor(),
					ray.getIntensity());
		}
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		Map<String, Object> map = Map.of("radius", circularPlane.getRadius(), "plane",
				circularPlane.getPlaneEnum().toString());
		return map;
	}
	
	@Override
	public void updateDefiningAttributes(Map<String, Object> attributes) {
		if(attributes.containsKey("radius")) {
			Double radius = (Double) attributes.get("radius");
			circularPlane.setRadius(radius.floatValue());
		} else if(attributes.containsKey("plane")) {
			// TBD
		}
	}
	
	@Override
	public boolean hasRefractionIndex() {
		return false;
	}

	@Override
	public BoundedRegion3D<Float> getCurrentBoundedRegion() {
		return null;
	}
}
