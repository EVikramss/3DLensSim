package com.imaging.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;

import com.imaging.common.Util;
import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.ray.LightRay;

public abstract class InteractableCompositeObject extends BasicObject {

	private InteractableCompositeObject parentObj;
	private RefractionIndex refractionIndex;
	private List<InteractableCompositeObject> childObjects = new ArrayList<InteractableCompositeObject>();
	private Map<Integer, Intersection> intersectionMap = new ManagedConcurrentWeakHashMap<Integer, Intersection>();

	public InteractableCompositeObject(Point position, ObjectTypeEnum typeEnum, RefractionIndex refractionIndex) {
		super(position, typeEnum);
		this.refractionIndex = refractionIndex;
	}

	public void addChildObject(InteractableCompositeObject childObject) {
		// update child object position w.r.t to current object
		childObject.parentObj = this;
		childObject.adjustPosition(getPosition());
		childObjects.add(childObject);

		// sort child objects
		Collections.sort(childObjects);
	}

	public void propagateRay(LightRay ray) {
		propagateRay(ray, null, null, null);
	}

	public void propagateRay(LightRay ray, Point objectParentPosition, RefractionIndex indexBefore,
			RefractionIndex indexAfter) {
		if (childObjects != null && childObjects.size() > 0) {
			Point currentCompositePos = objectParentPosition != null
					? accumulatePosition(objectParentPosition, getPosition())
					: getPosition();
			for (int counter = 0; counter < childObjects.size(); counter++) {
				InteractableCompositeObject childObject = childObjects.get(counter);

				boolean ignoreRayInteraction = shouldIgnoreInteraction(ray, childObject);
				if (ignoreRayInteraction)
					continue;

				/*
				 * RefractionIndex prevIndex = (counter == 0) ? getRefractionIndex() :
				 * childObjects.get(counter + 1).getRefractionIndex(); RefractionIndex nextIndex
				 * = (counter == childObjCount - 1) ? getRefractionIndex() :
				 * childObjects.get(counter + 1).getRefractionIndex();
				 */

				RefractionIndex prevIndex = getRefractionIndex();
				RefractionIndex nextIndex = prevIndex;

				// Propagate ray only if it did'nt spawn child rays
				if (!ray.hasChildren())
					childObject.propagateRay(ray, currentCompositePos, prevIndex, nextIndex);
			}
		}
	}

	protected boolean shouldIgnoreInteraction(LightRay ray, InteractableCompositeObject childObject) {
		boolean ignoreRayInteraction = false;
		// if non-refleted ray already interacted with object, then ignore
		if (!ray.isReflectedRay() && ray.getInteractionMap().containsKey(childObject.getId()))
			ignoreRayInteraction = true;
		else if (ray.isReflectedRay()) {
			// if reflected ray, check that the current ray (and not its parents) did not
			// interact with the object
			UUID rayUUID = ray.getInteractionMap().get(childObject.getId());
			if (rayUUID != null && rayUUID.equals(ray.getId()))
				ignoreRayInteraction = true;
		}
		return ignoreRayInteraction;
	}

	public boolean hasRefractionIndex() {
		return true;
	}

	public RefractionIndex getRefractionIndex() {
		return refractionIndex;
	}

	public void setRefractionIndex(RefractionIndex refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	public List<InteractableCompositeObject> getChildObjects() {
		return childObjects;
	}

	public InteractableCompositeObject getParentObj() {
		return parentObj;
	}

	/**
	 * Get bounded region by considering child objects. Take care to keep the child
	 * regions immutable.
	 */
	@Override
	public BoundedRegion3D<Float> getCurrentBoundedRegion() {

		List<InteractableCompositeObject> childObjects = getChildObjects();
		BoundedRegion3D<Float> region = childObjects.get(0).getCurrentBoundedRegion();

		for (int counter = 1; counter < childObjects.size(); counter++) {
			InteractableCompositeObject childObject = childObjects.get(counter);
			if (region != null)
				region = region.merge(childObject.getCurrentBoundedRegion());
			else
				region = childObject.getCurrentBoundedRegion();
		}
		
		return Util.translateRegion(region, getPosition());
	}

	@Override
	public Intersection getIntersectionPoint(LightRay ray, Point objectParentPosition) {
		throw new UnsupportedOperationException("Intersection point only supported for base objects");
	}

	public void storeInIntersectionCache(LightRay ray, Point objectParentPosition, Intersection point) {
		String keySource = ray.getId() + objectParentPosition.toString() + getDefiningAttributes().hashCode();
		intersectionMap.put(keySource.hashCode(), point);
	}

	public Intersection getFromIntersectionCache(LightRay ray, Point objectParentPosition) {
		String keySource = ray.getId() + objectParentPosition.toString() + getDefiningAttributes().hashCode();
		return intersectionMap.get(keySource.hashCode());
	}
}
