package com.imaging.objects.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imaging.common.Util;
import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.geom.surface.SphericalSurface;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.light.refraction.SimpleRefractionIndex;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.objects.ObjectTypeEnum;
import com.imaging.ray.LightRay;

public class Lens extends InteractableCompositeObject {

	public Lens(Point position, RefractionIndex index) {
		super(position, ObjectTypeEnum.LENS, index);
	}

	private List<InteractableCompositeObject> getSortedListByIntersection(
			List<InteractableCompositeObject> childObjects, LightRay ray, Point objectParentPosition) {

		childObjects = new ArrayList<InteractableCompositeObject>(childObjects);

		childObjects.sort((a, b) -> {
			// get intersection points of both objects with the ray
			Intersection aIntersection = a.getIntersectionPoint(ray, objectParentPosition);
			Intersection bIntersection = b.getIntersectionPoint(ray, objectParentPosition);

			// if ray does not intersect both, return existing order
			// but if intersects any 1 object only, return that object as leading
			if (aIntersection == null && bIntersection == null) {
				return a.compareTo(b);
			} else if (aIntersection == null) {
				return -1;
			} else if (bIntersection == null) {
				return 1;
			}

			Point aIntersectionPoint = aIntersection.getIntersectionPoint();
			Point bIntersectionPoint = bIntersection.getIntersectionPoint();
			a.storeInIntersectionCache(ray, objectParentPosition, aIntersection);
			b.storeInIntersectionCache(ray, objectParentPosition, bIntersection);

			// get distance between ray start point and intersection points
			Float aDistance = Util.distanceBtwPoints(aIntersectionPoint, ray.getStartPos());
			Float bDistance = Util.distanceBtwPoints(bIntersectionPoint, ray.getStartPos());

			// compare the distance between the 2 intersections
			return aDistance.compareTo(bDistance);
		});

		return childObjects;
	}

	@Override
	public void propagateRay(LightRay ray, Point objectParentPosition, RefractionIndex indexBefore,
			RefractionIndex indexAfter) {
		List<InteractableCompositeObject> childObjects = getChildObjects();
		if (childObjects != null && childObjects.size() > 0) {
			Point currentCompositePos = objectParentPosition != null
					? accumulatePosition(objectParentPosition, getPosition())
					: getPosition();
			boolean firstBoundaryFound = false;

			childObjects = getSortedListByIntersection(childObjects, ray, objectParentPosition);

			for (int counter = 0; counter < childObjects.size(); counter++) {
				boolean isFirstBoundary = false;
				InteractableCompositeObject childObject = childObjects.get(counter);
				if (childObject instanceof ObjectBoundary && !firstBoundaryFound) {
					firstBoundaryFound = true;
					isFirstBoundary = true;
				}

				boolean ignoreRayInteraction = shouldIgnoreInteraction(ray, childObject);
				if (ignoreRayInteraction)
					continue;

				RefractionIndex prevIndex = null;
				RefractionIndex nextIndex = null;
				if (isFirstBoundary) {
					prevIndex = indexBefore;
					nextIndex = getRefractionIndex();
				} else {
					prevIndex = getRefractionIndex();
					nextIndex = indexAfter;
				}

				// Propagate ray only if it did'nt spawn child rays
				if (!ray.hasChildren())
					childObject.propagateRay(ray, currentCompositePos, prevIndex, nextIndex);
			}
		}
	}

	public static class Builder {
		private Lens lens;
		private boolean lensPositionDefined = false;
		private boolean lensIndexDefined = false;
		private boolean lensFirstSurfaceDefined = false;
		private boolean lensSecondSurfaceDefined = false;
		private Point initPosition = Point.ORIGIN;

		/**
		 * Builder for lens. Sets all child object distances as global position w.r.t
		 * current lens position - as internally all positions are converted to parent
		 * local positions later on.
		 * 
		 */
		private Builder() {
			lens = new Lens(initPosition, new SimpleRefractionIndex(1.5f));
		}

		public static Builder getInstance() {
			return new Builder();
		}

		public Builder sphericalLens(float radiusOfFirstSurface, float radiusOfSecondSurface, float maxThickness) {
			Point offset = lensPositionDefined ? lens.getPosition() : initPosition;
			InteractableCompositeObject object1 = new ObjectBoundary(offset,
					new SphericalSurface(radiusOfFirstSurface, -1f, null), 0.0f);
			InteractableCompositeObject object2 = new ObjectBoundary(new Point(offset).add(0.0f, 0.0f, maxThickness),
					new SphericalSurface(radiusOfSecondSurface, 1f, null), 0.0f);
			addChild(object1);
			addChild(object2);

			lensFirstSurfaceDefined = true;
			lensSecondSurfaceDefined = true;

			return this;
		}

		public Builder sphericalLens(float radiusOfFirstSurface, float radiusOfSecondSurface, float maxThickness,
				BoundedRegion3D<Float> firstSurfaceRegion, BoundedRegion3D<Float> secondSurfaceRegion) {
			Point offset = lensPositionDefined ? lens.getPosition() : initPosition;
			InteractableCompositeObject object1 = new ObjectBoundary(offset,
					new SphericalSurface(radiusOfFirstSurface, -1f, firstSurfaceRegion), 0.0f);
			InteractableCompositeObject object2 = new ObjectBoundary(new Point(offset).add(0.0f, 0.0f, maxThickness),
					new SphericalSurface(radiusOfSecondSurface, 1f, secondSurfaceRegion), 0.0f);
			addChild(object1);
			addChild(object2);

			lensFirstSurfaceDefined = true;
			lensSecondSurfaceDefined = true;

			return this;
		}

		public Builder sphericalLens(Point lensPosition, Point offset, float radiusOfFirstSurface,
				float radiusOfSecondSurface, float maxThickness) {
			lens.setPosition(lensPosition);
			offset = offset.add(lensPosition);
			InteractableCompositeObject object1 = new ObjectBoundary(offset,
					new SphericalSurface(radiusOfFirstSurface, -1f, null), 0.0f);
			InteractableCompositeObject object2 = new ObjectBoundary(new Point(offset).add(0.0f, 0.0f, maxThickness),
					new SphericalSurface(radiusOfSecondSurface, 1f, null), 0.0f);
			addChild(object1);
			addChild(object2);

			lensFirstSurfaceDefined = true;
			lensSecondSurfaceDefined = true;
			lensPositionDefined = true;

			return this;
		}

		public Builder sphericalLens(Point lensPosition, Point offset, float radiusOfFirstSurface,
				float radiusOfSecondSurface, float maxThickness, BoundedRegion3D<Float> firstSurfaceRegion,
				BoundedRegion3D<Float> secondSurfaceRegion) {
			lens.setPosition(lensPosition);
			offset = offset.add(lensPosition);
			InteractableCompositeObject object1 = new ObjectBoundary(offset,
					new SphericalSurface(radiusOfFirstSurface, -1f, firstSurfaceRegion), 0.0f);
			InteractableCompositeObject object2 = new ObjectBoundary(new Point(offset).add(0.0f, 0.0f, maxThickness),
					new SphericalSurface(radiusOfSecondSurface, 1f, secondSurfaceRegion), 0.0f);
			addChild(object1);
			addChild(object2);

			lensFirstSurfaceDefined = true;
			lensSecondSurfaceDefined = true;
			lensPositionDefined = true;

			return this;
		}

		public Builder setRefractionIndex(RefractionIndex index) {
			lens.setRefractionIndex(index);
			lensIndexDefined = true;
			return this;
		}

		public Builder setPosition(Point position) {
			lens.setPosition(position);
			lensPositionDefined = true;

			/*
			 * // convert offsets in Lens objects to global positions
			 * List<InteractableCompositeObject> childObjects = lens.getChildObjects(); for
			 * (int counter = 0; counter < childObjects.size(); counter++) {
			 * InteractableCompositeObject childObject = childObjects.get(counter);
			 * childObject.getPosition().add(position); }
			 */

			return this;
		}

		public Builder addChild(InteractableCompositeObject object) {
			lens.addChildObject(object);
			return this;
		}

		public Lens build() {
			if (lensPositionDefined && lensIndexDefined && lensFirstSurfaceDefined && lensSecondSurfaceDefined)
				return lens;
			else
				throw new RuntimeException("Missing Properties. Set position, index and first, second surfaces");
		}
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		return new HashMap<String, Object>();
	}

	@Override
	public void updateDefiningAttributes(Map<String, Object> attributes) {
		// nothing to do here
	}
}
