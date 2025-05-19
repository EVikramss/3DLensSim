package com.imaging.objects;

import java.util.Map;

import com.imaging.geom.region.BoundedRegion3D;

public interface DefinedObject {

	public Map<String, Object> getDefiningAttributes();

	public void updateDefiningAttributes(Map<String, Object> attributes);

	public BoundedRegion3D<Float> getCurrentBoundedRegion();
}
