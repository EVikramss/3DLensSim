package com.imaging.geom.region;

public class BoundedRegion2D<T extends Number & Comparable<T>> {

	public T maxX = null;
	public T minX = null;
	public T maxY = null;
	public T minY = null;

	private boolean evaluateXBound(T x) {
		boolean valueConsidered = false;

		if (x.compareTo(maxX) > 0) {
			valueConsidered = true;
			maxX = x;
		} else if (x.compareTo(minX) < 0) {
			valueConsidered = true;
			minX = x;
		}

		return valueConsidered;
	}

	private boolean evaluateYBound(T y) {
		boolean valueConsidered = false;

		if (y.compareTo(maxY) > 0) {
			valueConsidered = true;
			maxY = y;
		} else if (y.compareTo(minY) < 0) {
			valueConsidered = true;
			minY = y;
		}

		return valueConsidered;
	}

	private boolean init(T x, T y) {
		maxX = x;
		minX = x;
		maxY = y;
		minY = y;

		return true;
	}

	public boolean evaluate(T x, T y) {
		if (maxX == null)
			return init(x, y);
		else {
			return evaluateXBound(x) | evaluateYBound(y);
		}
	}

	public int compareTo(T x, T y) {

		int compareVal = -1;

		if (x.compareTo(maxX) < 0 && x.compareTo(minX) > 0 && y.compareTo(maxY) < 0 && y.compareTo(minY) > 0) {
			// inside boundary
			compareVal = 1;
		} else if ((x.compareTo(maxX) == 0 && (y.compareTo(maxY) <= 0 && y.compareTo(minY) >= 0))
				|| (x.compareTo(minX) == 0 && (y.compareTo(maxY) <= 0 && y.compareTo(minY) >= 0))
				|| (y.compareTo(maxY) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0))
				|| (y.compareTo(minY) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0))) {
			// on boundary
			compareVal = 0;
		}
		
		return compareVal;
	}
}
