package com.imaging.objects;

import com.imaging.geom.Point;
import com.imaging.geom.surface.Mesh;

public interface DrawableObject {

	public Mesh getMesh(Float xdiv, Float ydiv, Float zdiv, Point parentPosition);
}
