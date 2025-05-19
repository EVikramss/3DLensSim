package com.imaging.color;

import java.util.List;

import com.imaging.common.Util;

public class HSVMixer implements ColorMixer {

	@Override
	public int mixColors(List<Integer> colors) {

		float h = 0.0f;
		float s = 0.0f;
		float v = 0.0f;

		for (int counter = 0; counter < colors.size(); counter++) {
			Integer color = colors.get(counter);
			float[] hsvColor = Util.getHSV(color);
			h += hsvColor[0];
			s += hsvColor[1];
			v += hsvColor[2];
		}
		
		h /= (float) colors.size();
		s /= (float) colors.size();
		v /= (float) colors.size();

		return Util.getRGBInt(h, s, v);
	}

}
