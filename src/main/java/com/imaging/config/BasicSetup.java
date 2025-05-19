package com.imaging.config;

import java.util.Map;

import com.imaging.common.PlaneEnum;
import com.imaging.geom.Point;
import com.imaging.light.refraction.SimpleRefractionIndex;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.objects.ObjectTypeEnum;
import com.imaging.objects.impl.CircularAperture;
import com.imaging.objects.impl.Lens;

public class BasicSetup extends InteractableCompositeObject {

	public BasicSetup(Point origin, SimpleRefractionIndex index) {
		super(origin, ObjectTypeEnum.SETUP, index);

		Lens convexLens = Lens.Builder.getInstance().sphericalLens(2.0f, 2.0f, 0.5f).setPosition(new Point(0, 0, 10.0f))
				.setRefractionIndex(new SimpleRefractionIndex(1.5f)).build();
		addChildObject(convexLens);

		CircularAperture aperture = new CircularAperture(new Point(0, 0, 13.0f), 1.0f, PlaneEnum.XY);
		addChildObject(aperture);
	}

	@Override
	public Map<String, Object> getDefiningAttributes() {
		return null;
	}

	@Override
	public void updateDefiningAttributes(Map<String, Object> attributes) {
		// nothing to update
	}
}
