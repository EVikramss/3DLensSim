package com.imaging.geom.surface;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

	List<Float> xValList = new ArrayList<Float>();
	List<Float> yValList = new ArrayList<Float>();
	List<Float> zValList = new ArrayList<Float>();
	List<Integer> indices = new ArrayList<Integer>();

	public List<Float> getxValList() {
		return xValList;
	}

	public void setxValList(List<Float> xValList) {
		this.xValList = xValList;
	}

	public List<Float> getyValList() {
		return yValList;
	}

	public void setyValList(List<Float> yValList) {
		this.yValList = yValList;
	}

	public List<Float> getzValList() {
		return zValList;
	}

	public void setzValList(List<Float> zValList) {
		this.zValList = zValList;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}
}
