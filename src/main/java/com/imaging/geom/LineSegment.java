package com.imaging.geom;

import com.imaging.common.Util;

public class LineSegment {

	// start positions
	private Point startPos;

	// stop positions
	private Point stopPos;

	// angles
	private Point directionVector;

	public LineSegment(Point startPos, Point directionVector) {
		this.startPos = startPos;
		this.directionVector = Util.normalize(directionVector);
	}

	public void setStopPos(Point stopPos) {
		this.stopPos = stopPos;
	}

	public Point getStartPos() {
		return startPos;
	}

	public void setStartPos(Point startPos) {
		this.startPos = startPos;
	}

	public Point getStopPos() {
		return stopPos;
	}

	public Point getDirectionVector() {
		return directionVector;
	}

	public void setDirectionVector(Point directionVector) {
		this.directionVector = directionVector;
	}

	public Float getMaxDistAlongDir() {

		Float output = null;

		if (stopPos != null) {
			Point diff = new Point(stopPos).subtract(startPos);
			if (directionVector.getX() != 0.0f)
				output = diff.getX() / directionVector.getX();
			else if (directionVector.getY() != 0.0f)
				output = diff.getY() / directionVector.getY();
			else if (directionVector.getZ() != 0.0f)
				output = diff.getZ() / directionVector.getZ();
		}

		return output;
	}
}
