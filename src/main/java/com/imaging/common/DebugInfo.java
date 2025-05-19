package com.imaging.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.imaging.compute.Intersection;
import com.imaging.geom.Point;
import com.imaging.ray.LightRay;

public class DebugInfo {

	private static List<String> dataList = new ArrayList<>();

	public static String getReportData() {
		String reportData = dataList.stream().reduce("", (a, b) -> a + "<br><br>" + b);
		dataList.clear();
		return reportData;
	}

	public static void infoForRay(LightRay ray) {
		if (dataList.size() != 0)
			dataList.add("---------------------<br>---------------------");
		StringBuffer sb = new StringBuffer();
		sb.append(ray.getId());
		sb.append(":: ");
		sb.append("Ray from ");
		sb.append(ray.getStartPos().toString());
		sb.append(" in dir ");
		sb.append(ray.getDirectionVector().toString());
		sb.append(" with wavelength ");
		sb.append(ray.getWavelength());
		dataList.add(sb.toString());
	}

	public static void addUUID(UUID uuid) {
		dataList.add("UUID of object " + uuid.toString());
	}

	public static void addIntersection(Intersection intersection) {
		dataList.add("Intersection at " + intersection.getIntersectionPoint().toString());
	}

	public static void addIndexBefore(float indexBeforeVal) {
		dataList.add("Index Before Boundary " + indexBeforeVal);
	}

	public static void addReflectionFactor(float reflectionFactor) {
		dataList.add("With Reflection Factor " + reflectionFactor);
	}

	public static void addIndexAfter(float indexAfterVal) {
		dataList.add("Index After Boundary " + indexAfterVal);
	}

	public static void addIndexRatio(float ratio) {
		dataList.add("Ratio of indexes " + ratio);
	}

	public static void addNormal(Point normal) {
		dataList.add("Normal at intersection Point " + normal);
	}

	public static void addCompute(float dotProduct, float coeff1, Point temp1, Point temp2, Point temp3) {
		StringBuffer sb = new StringBuffer();
		sb.append("Dot Product of Normal and Ray Vector ");
		sb.append(dotProduct);
		sb.append("<br> coefficient1 is ");
		sb.append(coeff1);
		sb.append("<br> temp1 is ");
		sb.append(temp1);
		sb.append("<br> temp2 is ");
		sb.append(temp2);
		sb.append("<br> temp3 is ");
		sb.append(temp3);
		dataList.add(sb.toString());
	}

	public static void addChildRayUUID(UUID uuid) {
		dataList.add("UUID of child ray " + uuid.toString());
	}
}
