package com.imaging.geom;

import com.imaging.common.AxisEnum;

public class Limit {

	public float upperLimit;
	public float lowerLimit;
	public AxisEnum axis;

	public Limit(float upperLimit, float lowerLimit, AxisEnum axis) {
		super();

		if (upperLimit < lowerLimit) {
			float temp = lowerLimit;
			lowerLimit = upperLimit;
			upperLimit = temp;
		}

		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.axis = axis;
	}

	public float getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(float upperLimit) {
		this.upperLimit = upperLimit;
	}

	public float getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(float lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public AxisEnum getAxis() {
		return axis;
	}

	public void setAxis(AxisEnum axis) {
		this.axis = axis;
	}

	public boolean withinLimit(float val) {
		if (val > upperLimit || val < lowerLimit)
			return false;
		else
			return true;
	}

	@Override
	public String toString() {
		return upperLimit + "," + lowerLimit + "," + axis.toString();
	}
}
