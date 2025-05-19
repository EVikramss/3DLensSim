package com.imaging.objects.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.imaging.common.Util;
import com.imaging.compute.ComputeRefractionReflection;
import com.imaging.compute.Intersection;
import com.imaging.geom.Limit;
import com.imaging.geom.Point;
import com.imaging.geom.crosssections.CrossSection;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.geom.surface.Mesh;
import com.imaging.geom.surface.ThreeDSurface;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.objects.DrawableObject;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.objects.ObjectTypeEnum;
import com.imaging.ray.LightRay;

public class ObjectBoundary extends InteractableCompositeObject implements DrawableObject {

	private CrossSection sideCrossSection;
	private CrossSection topCrossSection;
	private ThreeDSurface surfaceEq;
	private float reflectionFactor;

	public ObjectBoundary(Point position, CrossSection sideCrossSection, CrossSection topCrossSection, Limit xLimit,
			float reflectionFactor) {
		super(position, ObjectTypeEnum.OBJECT_BOUNDARY_CROSSSECTION, null);
		this.sideCrossSection = sideCrossSection;
		this.topCrossSection = topCrossSection;
		this.reflectionFactor = reflectionFactor;
	}

	public ObjectBoundary(Point position, ThreeDSurface surfaceEq, float reflectionFactor) {
		super(position, ObjectTypeEnum.OBJECT_BOUNDARY_3DSURFACE, null);
		this.surfaceEq = surfaceEq;
		this.reflectionFactor = reflectionFactor;
	}

	@Override
	public void propagateRay(LightRay ray, Point objectParentPosition, RefractionIndex indexBefore,
			RefractionIndex indexAfter) {
		Intersection output = getIntersectionPoint(ray, objectParentPosition);
		if (output != null) {
			// ray intersects the surface
			ray.setStopPos(output.getIntersectionPoint(), getId());
			ComputeRefractionReflection.computeChildRays(ray, output, indexBefore, indexAfter, reflectionFactor,
					getId());
		}
	}

	@Override
	public Intersection getIntersectionPoint(LightRay ray, Point objectParentPosition) {
		Intersection output = null;
		if (surfaceEq != null) {
			output = surfaceEq.getIntersectionPoint(ray, objectParentPosition, getPosition());
		} else {
			// TBD
		}
		return output;
	}

	@Override
	public RefractionIndex getRefractionIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRefractionIndex() {
		return false;
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		if (surfaceEq != null)
			return surfaceEq.getDefiningAttributes();
		else {
			Map<String, Object> attributes = sideCrossSection.getDefiningAttributes();
			attributes.putAll(topCrossSection.getDefiningAttributes());
			return attributes;
		}
	}

	@Override
	public void updateDefiningAttributes(Map<String, Object> attributes) {
		// TODO Auto-generated method stub
	}

	@Override
	public BoundedRegion3D<Float> getCurrentBoundedRegion() {
		if (surfaceEq != null) {
			BoundedRegion3D<Float> region = surfaceEq.getCurrentBoundedRegion();
			return Util.translateRegion(region, getPosition());
		} else {
			// TODO Auto-generated method stub
		}

		return null;
	}

	public Float evaluateZ(float x, float y) {
		Float output = null;
		if (surfaceEq != null) {
			output = surfaceEq.evaluateCrossSection(x, y);
		} else {
			// TBD
		}

		return output;
	}

	@Override
	public Mesh getMesh(Float xdiv, Float ydiv, Float zdiv, Point parentPosition) {

		Mesh mesh = new Mesh();

		List<Float> xValList = new ArrayList<Float>();
		List<Float> yValList = new ArrayList<Float>();
		List<Float> zValList = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();

		parentPosition = parentPosition.add(getPosition());

		BoundedRegion3D<Float> boundedRegion = null;
		if (surfaceEq != null) {
			boundedRegion = surfaceEq.getCurrentBoundedRegion();
		} else {
			// TODO Auto-generated method stub
		}

		for (float yCounter = boundedRegion.minY; yCounter <= boundedRegion.maxY; yCounter += ydiv) {
			for (float xCounter = boundedRegion.minX; xCounter <= boundedRegion.maxX; xCounter += xdiv) {
				Float zval = evaluateZ(xCounter, yCounter);
				if (!zval.isNaN()) {
					/*
					 * xValList.add(xCounter + parentPosition.getX()); yValList.add(yCounter +
					 * parentPosition.getY()); zValList.add(zval + parentPosition.getZ());
					 */

					xValList.add(xCounter);
					yValList.add(yCounter);
					zValList.add(zval);
				}
			}
		}

		// generate indices for triangle
		int xPoints = (int) ((boundedRegion.maxX - boundedRegion.minX) / xdiv);
		int yPoints = (int) ((boundedRegion.maxY - boundedRegion.minY) / ydiv);
		for (int j = 0; j < yPoints - 1; j++) {
			for (int i = 0; i < xPoints - 1; i++) {
				// @formatter:off
				/*
				 * 2	3
				 * 0	1
				 * 
				 * indices - [0, 2, 3], [0, 3, 1]
				 */
				// @formatter:on
				int index0 = i + (j * xPoints);
				int index1 = i + (j * xPoints) + 1;
				int index2 = i + ((j + 1) * xPoints);
				int index3 = i + ((j + 1) * xPoints) + 1;

				indices.add(index0);
				indices.add(index2);
				indices.add(index3);
				indices.add(index0);
				indices.add(index3);
				indices.add(index1);
			}
		}

		mesh.setxValList(xValList);
		mesh.setyValList(yValList);
		mesh.setzValList(zValList);
		mesh.setIndices(indices);

		return mesh;
	}
}
