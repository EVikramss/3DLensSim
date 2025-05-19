package com.imaging.adapter.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.imaging.geom.Point;
import com.imaging.geom.region.BoundedRegion3D;
import com.imaging.geom.surface.Mesh;
import com.imaging.light.refraction.RefractionIndex;
import com.imaging.objects.ObjectTypeEnum;

public class NestedObjectRepresentation {

	private RefractionIndex refractionIndex;
	private Point position;
	private ObjectTypeEnum typeEnum;
	private List<NestedObjectRepresentation> childObjects = new ArrayList<NestedObjectRepresentation>();
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private UUID uuid;
	private BoundedRegion3D<Float> boundedRegion;
	
	// DS for representing object as Mesh
	private Mesh mesh;

	public BoundedRegion3D<Float> getBoundedRegion() {
		return boundedRegion;
	}

	public void setBoundedRegion(BoundedRegion3D<Float> boundedRegion) {
		this.boundedRegion = boundedRegion;
	}

	public RefractionIndex getRefractionIndex() {
		return refractionIndex;
	}

	public void setRefractionIndex(RefractionIndex refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public List<NestedObjectRepresentation> getChildObjects() {
		return childObjects;
	}

	public void setChildObjects(List<NestedObjectRepresentation> childObjects) {
		this.childObjects = childObjects;
	}

	public void addChildObject(NestedObjectRepresentation childObject) {
		this.childObjects.add(childObject);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public ObjectTypeEnum getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(ObjectTypeEnum typeEnum) {
		this.typeEnum = typeEnum;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
}
