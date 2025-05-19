package com.imaging.adapter.object;

import java.util.ArrayList;
import java.util.List;

import com.imaging.geom.Point;
import com.imaging.ray.LightRay;

public class LineSegmentBundle {

	private List<Point> points = new ArrayList<Point>();

	public LineSegmentBundle(LightRay ray) {
		if (ray.getParentRay() != null) {
			throw new RuntimeException("Need parent ray");
		}
		parseRay(ray);
	}

	private void parseRay(LightRay ray) {
		Point startPoint = ray.getStartPos();
		Point stopPoint = ray.getStopPos();

		if (startPoint != null && stopPoint != null) {
			points.add(startPoint);
			points.add(stopPoint);
		}

		for (LightRay chRay : ray.getChildRays())
			parseRay(chRay);
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
