package com.imaging.ray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.imaging.geom.LineSegment;
import com.imaging.geom.Point;

public class LightRay extends LineSegment {

	private float wavelength;
	private int color;
	private float intensity;
	private UUID id;

	private boolean isReflectedRay;
	private boolean isDebugRay;

	private LightRay parentRay = null;
	private List<LightRay> childRays = new ArrayList<LightRay>();
	private Map<UUID, UUID> interactionMap = new ConcurrentHashMap<UUID, UUID>();
	private int depth;

	public LightRay(Point startPos, Point directionVector, float wavelength, int color, float intensity) {
		super(startPos, directionVector);
		this.wavelength = wavelength;
		this.color = color;
		this.intensity = intensity;
		this.id = UUID.randomUUID();
	}

	public LightRay(LightRay ray, Point startPos, Point directionVector, float wavelength, int color,
			float intensity) {
		this(startPos, directionVector, wavelength, color, intensity);
		ray.addChildRay(this);
	}

	public float getWavelength() {
		return wavelength;
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public boolean isReflectedRay() {
		return isReflectedRay;
	}

	public void setReflectedRay(boolean isReflectedRay) {
		this.isReflectedRay = isReflectedRay;
	}

	public List<LightRay> getChildRays() {
		return childRays;
	}

	public void setChildRays(List<LightRay> childRays) {
		this.childRays = childRays;
		childRays.stream().forEach(cr -> {
			cr.setInteractionMap(interactionMap);
			cr.setDepth(depth + 1);
			cr.setParentRay(this);
		});
	}

	public void addChildRay(LightRay childRay) {
		childRays.add(childRay);
		childRay.setDepth(depth + 1);
		childRay.setParentRay(this);
		childRay.setDebugRay(isDebugRay());
		childRay.setInteractionMap(interactionMap);

		if (!isDebugRay())
			RayGenerator.addToSecondaryRays(childRay);
		else
			RayGeneratorDebug.addToSecondaryRays(childRay);
	}

	public Map<UUID, UUID> getInteractionMap() {
		return interactionMap;
	}

	public void setInteractionMap(Map<UUID, UUID> interactionMap) {
		this.interactionMap = interactionMap;
	}

	public void setStopPos(Point stopPos, UUID id) {
		interactionMap.put(id, getId());
		super.setStopPos(stopPos);
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public boolean hasChildren() {
		return childRays != null && childRays.size() > 0 ? true : false;
	}

	public void setParentRay(LightRay parentRay) {
		this.parentRay = parentRay;
	}

	public LightRay getParentRay() {
		return parentRay;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LightRay) {
			return ((LightRay) obj).hashCode() == hashCode();
		}
		return super.equals(obj);
	}

	public boolean isDebugRay() {
		return isDebugRay;
	}

	public void setDebugRay(boolean isDebugRay) {
		this.isDebugRay = isDebugRay;
	}
}
