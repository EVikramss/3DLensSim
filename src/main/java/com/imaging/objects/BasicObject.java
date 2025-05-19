package com.imaging.objects;

import java.util.UUID;

import com.imaging.geom.Point;

public abstract class BasicObject implements Comparable<BasicObject>, InteractableObject, DefinedObject {

	private Point position;
	private UUID id;
	private ObjectTypeEnum typeEnum;

	public BasicObject(Point position, ObjectTypeEnum typeEnum) {
		this.position = position;
		this.typeEnum = typeEnum;
		id = UUID.randomUUID();
	}

	@Override
	public int compareTo(BasicObject obj) {
		// arrange objects w.r.t z-axis positions
		float thisZaxisPos = position.getZ();
		float objZaxisPos = obj.position.getZ();
		return thisZaxisPos > objZaxisPos ? 1 : thisZaxisPos < objZaxisPos ? -1 : 0;
	}

	// object position methods - start
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void adjustPosition(Point position) {
		this.position = this.position.adjustWRT(position);
	}

	protected Point accumulatePosition(Point parentPosition, Point grandParentPosition) {
		Point output = new Point(parentPosition.getX() + grandParentPosition.getX(),
				parentPosition.getY() + grandParentPosition.getY(), parentPosition.getZ() + grandParentPosition.getZ());
		return output;
	}
	// object position methods - end

	public boolean isInteractive() {
		return true;
	}

	public UUID getId() {
		return id;
	}

	public ObjectTypeEnum getTypeEnum() {
		return typeEnum;
	}
}
