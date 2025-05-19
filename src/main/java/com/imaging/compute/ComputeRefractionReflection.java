package com.imaging.compute;

import java.util.UUID;

import com.imaging.common.DebugInfo;
import com.imaging.common.Util;
import com.imaging.geom.Point;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.ray.LightRay;

public class ComputeRefractionReflection {

	/**
	 * Read
	 * 'https://physics.stackexchange.com/questions/435512/snells-law-in-vector-form'
	 * for reference
	 * 
	 * @param ray
	 * @param intersection
	 * @param indexBefore
	 * @param indexAfter
	 * @param reflectionFactor
	 * @param uuid
	 */
	public static void computeChildRays(LightRay ray, Intersection intersection, RefractionIndex indexBefore,
			RefractionIndex indexAfter, float reflectionFactor, UUID uuid) {

		if (ray.isDebugRay()) {
			computeChildRaysForDebug(ray, intersection, indexBefore, indexAfter, reflectionFactor, uuid);
		} else {
			// TBD reflection
			Point normal = intersection.getNormalVectorAtIntersection();
			Point rayVector = ray.getDirectionVector();

			float lightRayWavelength = ray.getWavelength();
			float indexBeforeVal = indexBefore.getIndexOfRefractionFor(lightRayWavelength);
			float indexAfterVal = indexAfter.getIndexOfRefractionFor(lightRayWavelength);
			float ratio = indexBeforeVal / indexAfterVal;

			float dotProduct = Util.getDotProduct(normal, rayVector);
			float coeff1 = (1 - (dotProduct * dotProduct)) * (ratio * ratio);
			if (coeff1 <= 1.0) {
				coeff1 = (float) Math.sqrt(1 - coeff1);

				Point temp1 = new Point(normal).multiply(coeff1);
				Point temp2 = new Point(rayVector).subtract(new Point(normal).multiply(dotProduct)).multiply(ratio);
				temp1 = temp1.add(temp2);

				// TBD light intensity/color losses
				if (!Util.containsNaN(temp1))
					new LightRay(ray, intersection.getIntersectionPoint(), temp1, lightRayWavelength, ray.getColor(),
							ray.getIntensity());
			}
		}
	}

	public static void computeChildRaysForDebug(LightRay ray, Intersection intersection, RefractionIndex indexBefore,
			RefractionIndex indexAfter, float reflectionFactor, UUID uuid) {

		// TBD reflection
		Point normal = intersection.getNormalVectorAtIntersection();
		Point rayVector = ray.getDirectionVector();

		float lightRayWavelength = ray.getWavelength();
		float indexBeforeVal = indexBefore.getIndexOfRefractionFor(lightRayWavelength);
		float indexAfterVal = indexAfter.getIndexOfRefractionFor(lightRayWavelength);
		float ratio = indexBeforeVal / indexAfterVal;

		float dotProduct = Util.getDotProduct(normal, rayVector);
		float coeff1 = (1 - (dotProduct * dotProduct)) * (ratio * ratio);
		coeff1 = (float) Math.sqrt(1 - coeff1);

		Point temp1 = new Point(normal).multiply(coeff1);
		Point temp2 = new Point(rayVector).subtract(new Point(normal).multiply(dotProduct)).multiply(ratio);
		Point temp3 = new Point(temp1).add(temp2);

		// TBD light intensity/color losses
		LightRay refractedRay = new LightRay(ray, intersection.getIntersectionPoint(), temp3, lightRayWavelength,
				ray.getColor(), ray.getIntensity());

		DebugInfo.infoForRay(ray);
		DebugInfo.addUUID(uuid);
		DebugInfo.addIntersection(intersection);
		DebugInfo.addIndexBefore(indexBeforeVal);
		DebugInfo.addIndexAfter(indexAfterVal);
		DebugInfo.addIndexRatio(ratio);
		DebugInfo.addReflectionFactor(reflectionFactor);
		DebugInfo.addNormal(normal);
		DebugInfo.addCompute(dotProduct, coeff1, temp1, temp2, temp3);
		DebugInfo.addChildRayUUID(refractedRay.getId());
	}

}
