package com.imaging.common;

import com.imaging.geom.Point;

public class Validation {

	public static boolean isValidObjectPosition(Point position) {
		if ((Float) position.getX() != null && (Float) position.getY() != null && (Float) position.getZ() != null)
			return true;
		return false;
	}
}
