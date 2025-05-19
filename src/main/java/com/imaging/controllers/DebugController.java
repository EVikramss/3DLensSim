package com.imaging.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imaging.common.DebugInfo;
import com.imaging.common.Util;
import com.imaging.geom.Point;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.ray.LightRay;
import com.imaging.solver.ImageSolverDebug;

@RestController
public class DebugController {

	@Autowired
	private ImageSolverDebug imageSolverDebug;

	@Autowired
	private InteractableCompositeObject composite;

	@PostMapping("/debug")
	public String debugLightRay(@RequestParam Map<String, String> params) {

		Float x = Float.parseFloat(params.get("x"));
		Float y = Float.parseFloat(params.get("y"));
		Float z = Float.parseFloat(params.get("z"));

		Float xDir = Float.parseFloat(params.get("xdir"));
		Float yDir = Float.parseFloat(params.get("ydir"));
		Float zDir = Float.parseFloat(params.get("zdir"));

		Point startPos = new Point(x, y, z);
		Point direction = Util.normalize(new Point(xDir, yDir, zDir));
		float wavelength = Float.parseFloat(params.get("wavelength"));
		int color = (int) Float.parseFloat(params.get("color"));
		float intensity = Float.parseFloat(params.get("intensity"));
		LightRay ray = new LightRay(startPos, direction, wavelength, color, intensity);
		ray.setDebugRay(true);

		imageSolverDebug.solve(ray);

		String reportData = DebugInfo.getReportData();
		String compositeData = getCompositeData(composite, new StringBuffer()).toString();
		String debugData = getTitle("Composite Info") + compositeData + getTitle("Composite Info End")
				+ getTitle("Ray Info") + reportData + getTitle("Ray Info End");

		return debugData;
	}

	private String getTitle(String title) {
		return "<br>------------------------------- " + title + " -------------------------------<br><br>";
	}
	
	private StringBuffer getCompositeData(InteractableCompositeObject composite, StringBuffer sb) {
		sb.append("Composite: " + composite.getId() + ", " + composite.getClass() + "<br>");
		for (InteractableCompositeObject compositeChild : composite.getChildObjects()) {
			getCompositeData(compositeChild, sb);
		}
		return sb;
	}
}
