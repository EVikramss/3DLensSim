package com.imaging.geom.region;

public class BoundedRegion3D<T extends Number & Comparable<T>> {

	public T maxX = null;
	public T minX = null;
	public T maxY = null;
	public T minY = null;
	public T maxZ = null;
	public T minZ = null;

	private void evaluateXBound(T x) {
		if (x.compareTo(maxX) > 0)
			maxX = x;
		else if (x.compareTo(minX) < 0)
			minX = x;
	}

	private void evaluateYBound(T y) {
		if (y.compareTo(maxY) > 0)
			maxY = y;
		else if (y.compareTo(minY) < 0)
			minY = y;
	}

	private void evaluateZBound(T z) {
		if (z.compareTo(maxZ) > 0)
			maxZ = z;
		else if (z.compareTo(minZ) < 0)
			minZ = z;
	}

	private void init(T x, T y, T z) {
		maxX = x;
		minX = x;
		maxY = y;
		minY = y;
		minZ = z;
		maxZ = z;
	}

	public int compareToXY(T x, T y) {

		// outside boundary
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

	public int compareToZ(T z) {

		// outside boundary
		int compareVal = -1;

		if (z.compareTo(maxZ) < 0 && z.compareTo(minZ) > 0) {
			// inside boundary
			compareVal = 1;
		} else if ((z.compareTo(maxZ) == 0) || (z.compareTo(minZ) == 0)) {
			// on boundary
			compareVal = 0;
		}

		return compareVal;
	}

	public int compareToXYZ(T x, T y, T z) {

		// outside boundary
		int compareVal = -1;

		if (x.compareTo(maxX) < 0 && x.compareTo(minX) > 0 && y.compareTo(maxY) < 0 && y.compareTo(minY) > 0
				&& z.compareTo(maxZ) < 0 && z.compareTo(minZ) > 0) {
			// inside boundary
			compareVal = 1;
		} else if ((x.compareTo(maxX) == 0
				&& (y.compareTo(maxY) <= 0 && y.compareTo(minY) >= 0 && z.compareTo(maxZ) < 0 && z.compareTo(minZ) > 0))
				|| (x.compareTo(minX) == 0 && (y.compareTo(maxY) <= 0 && y.compareTo(minY) >= 0 && z.compareTo(maxZ) < 0
						&& z.compareTo(minZ) > 0))
				|| (y.compareTo(maxY) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0 && z.compareTo(maxZ) < 0
						&& z.compareTo(minZ) > 0))
				|| (y.compareTo(minY) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0 && z.compareTo(maxZ) < 0
						&& z.compareTo(minZ) > 0))
				|| (z.compareTo(minZ) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0 && y.compareTo(maxY) < 0
						&& y.compareTo(minY) > 0))
				|| (z.compareTo(maxZ) == 0 && (x.compareTo(maxX) <= 0 && x.compareTo(minX) >= 0 && y.compareTo(maxY) < 0
						&& y.compareTo(minY) > 0))) {
			// on boundary
			compareVal = 0;
		}

		return compareVal;
	}

	public boolean withinBoundsXYZ(T x, T y, T z, T margin) {
		return x.doubleValue() < (maxX.doubleValue() + margin.doubleValue())
				&& x.doubleValue() > (minX.doubleValue() - margin.doubleValue())
				&& y.doubleValue() < (maxY.doubleValue() + margin.doubleValue())
				&& y.doubleValue() > (minY.doubleValue() - margin.doubleValue())
				&& z.doubleValue() < (maxZ.doubleValue() + margin.doubleValue())
				&& z.doubleValue() > (minZ.doubleValue() - margin.doubleValue());
	}

	public boolean withinBoundsXY(T x, T y, T margin) {
		return x.doubleValue() < (maxX.doubleValue() + margin.doubleValue())
				&& x.doubleValue() > (minX.doubleValue() - margin.doubleValue())
				&& y.doubleValue() < (maxY.doubleValue() + margin.doubleValue())
				&& y.doubleValue() > (minY.doubleValue() - margin.doubleValue());
	}

	public boolean withinBoundsZ(T z, T margin) {
		return z.doubleValue() < (maxZ.doubleValue() + margin.doubleValue())
				&& z.doubleValue() > (minZ.doubleValue() - margin.doubleValue());
	}

	public void evaluate(T x, T y, T z) {
		if (maxX == null)
			init(x, y, z);
		else {
			evaluateXBound(x);
			evaluateYBound(y);
			evaluateZBound(z);
		}
	}

	public BoundedRegion3D<T> merge(BoundedRegion3D<T> region) {
		if (region != null) {
			BoundedRegion3D<T> newRegion = new BoundedRegion3D<T>();
			newRegion.minX = minX.compareTo(region.minX) < 0 ? minX : region.minX;
			newRegion.maxX = maxX.compareTo(region.maxX) > 0 ? maxX : region.maxX;
			newRegion.minY = minY.compareTo(region.minY) < 0 ? minY : region.minY;
			newRegion.maxY = maxY.compareTo(region.maxY) > 0 ? maxY : region.maxY;
			newRegion.minZ = minZ.compareTo(region.minZ) < 0 ? minZ : region.minZ;
			newRegion.maxZ = maxZ.compareTo(region.maxZ) > 0 ? maxZ : region.maxZ;

			return newRegion;
		} else {
			return this;
		}
	}

	public boolean surrounds(BoundedRegion3D<T> region) {
		boolean surroundsProvidedRegion = false;
		if (region != null) {
			return region.maxX.compareTo(maxX) < 0 && region.minX.compareTo(minX) > 0 && region.maxY.compareTo(maxY) < 0
					&& region.minY.compareTo(minY) > 0 && region.maxZ.compareTo(maxZ) < 0
					&& region.minZ.compareTo(minZ) > 0;
		}

		return surroundsProvidedRegion;
	}

	public T getMaxX() {
		return maxX;
	}

	public void setMaxX(T maxX) {
		this.maxX = maxX;
	}

	public T getMinX() {
		return minX;
	}

	public void setMinX(T minX) {
		this.minX = minX;
	}

	public T getMaxY() {
		return maxY;
	}

	public void setMaxY(T maxY) {
		this.maxY = maxY;
	}

	public T getMinY() {
		return minY;
	}

	public void setMinY(T minY) {
		this.minY = minY;
	}

	public T getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(T maxZ) {
		this.maxZ = maxZ;
	}

	public T getMinZ() {
		return minZ;
	}

	public void setMinZ(T minZ) {
		this.minZ = minZ;
	}

	@Override
	public String toString() {
		return maxX + "," + minX + "," + maxY + "," + minY + "," + maxZ + "," + minZ;
	}

	public static BoundedRegion3D<Float> parseStringAsFloat(String strRegion) {

		BoundedRegion3D<Float> region = null;

		String[] regionStrArr = strRegion.split(",");
		if (regionStrArr.length == 6) {
			try {
				float maxX = Float.parseFloat(regionStrArr[0]);
				float minX = Float.parseFloat(regionStrArr[1]);
				float maxY = Float.parseFloat(regionStrArr[2]);
				float minY = Float.parseFloat(regionStrArr[3]);
				float maxZ = Float.parseFloat(regionStrArr[4]);
				float minZ = Float.parseFloat(regionStrArr[5]);

				region = new BoundedRegion3D<Float>();
				region.maxX = maxX;
				region.minX = minX;
				region.maxY = maxY;
				region.minY = minY;
				region.maxZ = maxZ;
				region.minZ = minZ;
			} catch (NumberFormatException e) {
			}
		}

		return region;
	}

	public BoundedRegion3D<T> clone() {
		BoundedRegion3D<T> clonedObject = new BoundedRegion3D<T>();
		clonedObject.maxX = maxX;
		clonedObject.minX = minX;
		clonedObject.maxY = maxY;
		clonedObject.minY = minY;
		clonedObject.maxZ = maxZ;
		clonedObject.minZ = minZ;
		return clonedObject;
	}
}
