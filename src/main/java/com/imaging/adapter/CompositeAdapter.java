package com.imaging.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imaging.adapter.object.NestedObjectRepresentation;
import com.imaging.common.Util;
import com.imaging.common.Validation;
import com.imaging.geom.Point;
import com.imaging.geom.surface.Mesh;
import com.imaging.objects.DrawableObject;
import com.imaging.objects.InteractableCompositeObject;

@Service
public class CompositeAdapter {

	@Autowired
	private InteractableCompositeObject composite;

	private ThreadLocal<Float> xdiv;
	private ThreadLocal<Float> ydiv;

	public CompositeAdapter() {
		xdiv = new ThreadLocal<Float>();
		ydiv = new ThreadLocal<Float>();
	}

	public NestedObjectRepresentation getParentComposite(Float xdiv, Float ydiv) {

		if (xdiv == null)
			xdiv = 0.1f;

		if (ydiv == null)
			ydiv = 0.1f;

		this.xdiv.set(xdiv);
		this.ydiv.set(ydiv);
		NestedObjectRepresentation mainParentObj = addToParentObj(composite, null);
		return mainParentObj;
	}

	private NestedObjectRepresentation addToParentObj(InteractableCompositeObject composite, Point parentPosition) {

		NestedObjectRepresentation parentObj = createObjectFromComposite(composite, parentPosition);
		addAttributes(parentObj, composite);
		List<InteractableCompositeObject> childObjects = composite.getChildObjects();
		for (int counter = 0; counter < childObjects.size(); counter++) {
			NestedObjectRepresentation childObj = addToParentObj(childObjects.get(counter), parentObj.getPosition());
			parentObj.addChildObject(childObj);
		}
		return parentObj;
	}

	private NestedObjectRepresentation createObjectFromComposite(InteractableCompositeObject composite,
			Point parentPosition) {
		NestedObjectRepresentation representationObject = new NestedObjectRepresentation();
		representationObject.setRefractionIndex(composite.hasRefractionIndex() ? composite.getRefractionIndex() : null);
		if (parentPosition == null)
			representationObject.setPosition(composite.getPosition());
		else
			representationObject.setPosition(composite.getPosition().add(parentPosition));
		representationObject.setTypeEnum(composite.getTypeEnum());
		representationObject.setUuid(composite.getId());
		if (parentPosition == null)
			representationObject.setBoundedRegion(composite.getCurrentBoundedRegion());
		else
			representationObject
					.setBoundedRegion(Util.translateRegion(composite.getCurrentBoundedRegion(), parentPosition));

		if (composite instanceof DrawableObject drawableObject) {
			evaluateObjBoundaryAsMesh(representationObject, drawableObject, parentPosition);
		}

		return representationObject;
	}

	private void evaluateObjBoundaryAsMesh(NestedObjectRepresentation representationObject,
			DrawableObject drawableObject, Point parentPosition) {
		Mesh mesh = drawableObject.getMesh(xdiv.get(), ydiv.get(), null, parentPosition);
		representationObject.setMesh(mesh);
	}

	private void addAttributes(NestedObjectRepresentation object, InteractableCompositeObject composite) {
		object.setAttributes(composite.getDefiningAttributes());
	}

	public void updateComposite(NestedObjectRepresentation object) {
		try {
			processObjectsForUpdate(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processObjectsForUpdate(NestedObjectRepresentation object) {
		for (NestedObjectRepresentation childObject : object.getChildObjects()) {
			processObjectsForUpdate(childObject);
		}
		processObjectForUpdate(object);
	}

	private void processObjectForUpdate(NestedObjectRepresentation object) {
		UUID objectID = object.getUuid();
		InteractableCompositeObject relatedObject = findObject(objectID, composite);
		if (relatedObject != null) {
			// update attributes
			relatedObject.updateDefiningAttributes(object.getAttributes());

			// update position
			Point globalPosition = object.getPosition();
			if (globalPosition != null && Validation.isValidObjectPosition(globalPosition)) {
				Point relativePosition = getRelativePosition(relatedObject, globalPosition);
				relatedObject.setPosition(relativePosition);
			}
		}
	}

	private Point getRelativePosition(InteractableCompositeObject object, Point globalPosition) {
		Point output = new Point(globalPosition);
		while ((object = object.getParentObj()) != null) {
			output.subtract(object.getPosition());
		}

		return output;
	}

	private InteractableCompositeObject findObject(UUID objectID, InteractableCompositeObject searchObject) {

		for (InteractableCompositeObject childObject : searchObject.getChildObjects()) {
			if (childObject.getId().equals(objectID)) {
				return childObject;
			} else {
				findObject(objectID, childObject);
			}
		}

		return null;
	}
}
