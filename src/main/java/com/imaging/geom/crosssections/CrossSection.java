package com.imaging.geom.crosssections;

import java.util.Map;

import com.imaging.common.AxisEnum;
import com.imaging.geom.Limit;

public interface CrossSection {
	
	public Float evaluateCrossSection(float y);
	public float getRadius();
	public void setRadius(float radius);
	public float getK();
	public void setK(float k);
	public float getA2();
	public void setA2(float a2);
	public float getA4();
	public void setA4(float a4);
	public float getA6();
	public void setA6(float a6);
	public float getA8();
	public void setA8(float a8);
	public Limit getLimit();
	public void setLimit(Limit Limit);
	public Map<String, Object> getDefiningAttributes();

	static class CrossSectionBuilder {

		private CrossSection crossSection;
		
		private CrossSectionBuilder() {
		}
		
		public static CrossSectionBuilder getInstance() {
			return new CrossSectionBuilder();
		}

		public CrossSectionBuilder sphericalLens(float radius) {
			Limit limit = new Limit(10, 10, AxisEnum.Y);
			crossSection = new SphericalCrossSection(-radius, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, limit);
			return this;
		}
		
		public CrossSectionBuilder sphericalLens(float radius, Limit limit) {
			crossSection = new SphericalCrossSection(radius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, limit);
			return this;
		}

		public CrossSection build() {
			return crossSection;
		}
	}
}
