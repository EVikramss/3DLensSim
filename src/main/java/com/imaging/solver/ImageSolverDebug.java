package com.imaging.solver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.imaging.objects.InteractableCompositeObject;
import com.imaging.ray.LightRay;
import com.imaging.ray.RayGeneratorDebug;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ImageSolverDebug {

	@Autowired
	private RayGeneratorDebug rayGenerator;

	@Autowired
	private InteractableCompositeObject composite;

	private List<LightRay> allRays = new ArrayList<LightRay>();

	public void solve(LightRay ray) {
		allRays = new ArrayList<LightRay>();
		RayGeneratorDebug.addToSecondaryRays(ray);
		while (rayGenerator.hasNext()) {
			List<LightRay> debugRays = rayGenerator.next();
			for (LightRay debugRay : debugRays) {
				composite.propagateRay(debugRay);
				if (!debugRay.hasChildren()) // consider only leaf nodes of light ray tree
					allRays.add(ray);
			}
		}
	}

	public List<LightRay> getAllRays() {
		return allRays;
	}
}
