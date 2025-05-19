package com.imaging.ray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.imaging.light.LightSource;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RayGeneratorDebug {

	private float xAngleStart;
	private float xAngleStop;
	private float xAngleSteps;
	private float yAngleStart;
	private float yAngleStop;
	private float yAngleSteps;
	private List<Float> xAngleRange;
	private List<Float> yAngleRange;

	private LightSource lightSource;

	private static List<LightRay> secondaryRays = new ArrayList<LightRay>();
	private static int maxInteractionsOfReflectedRays;

	@Autowired
	public RayGeneratorDebug(LightSource lightSource) {
		this.lightSource = lightSource;
		setRayRange(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);
	}

	public boolean hasNext() {
		boolean hasNext = false;
		if (!hasNext) {
			if (secondaryRays != null && secondaryRays.size() > 0)
				hasNext = true;
		}

		return hasNext;
	}

	public List<LightRay> next() {
		List<LightRay> rays = new ArrayList<LightRay>();
		if (rays == null || rays.size() == 0) {
			if (secondaryRays != null && secondaryRays.size() > 0) {
				rays = secondaryRays;
				// System.out.println("Reading secondary rays " + secondaryRays.size());
				secondaryRays = new ArrayList<LightRay>();
			}
		}

		return rays;
	}

	public void setRayRange(float xAngleStart, float xAngleStop, float xAngleSteps, float yAngleStart, float yAngleStop,
			float yAngleSteps) {
		this.xAngleStart = xAngleStart;
		this.xAngleStop = xAngleStop;
		this.xAngleSteps = xAngleSteps;
		this.yAngleStart = yAngleStart;
		this.yAngleStop = yAngleStop;
		this.yAngleSteps = yAngleSteps;
		generateAngleList();
	}

	private void generateAngleList() {
		xAngleRange = new ArrayList<Float>();
		yAngleRange = new ArrayList<Float>();

		for (float i = xAngleStart; i <= xAngleStop; i = i + xAngleSteps) {
			xAngleRange.add(i);
		}

		for (float i = yAngleStart; i <= yAngleStop; i = i + yAngleSteps) {
			yAngleRange.add(i);
		}
	}

	public float[] getRayRange() {
		return new float[] { xAngleStart, xAngleStop, xAngleSteps, yAngleStart, yAngleStop, yAngleSteps };
	}

	public static synchronized void addToSecondaryRays(LightRay ray) {
		if (ray.isReflectedRay() && ray.getDepth() > maxInteractionsOfReflectedRays)
			return;
		secondaryRays.add(ray);
	}

	public static synchronized void addToSecondaryRays(List<LightRay> rays) {
		rays = rays.stream().filter(ray -> !(ray.isReflectedRay() && ray.getDepth() > maxInteractionsOfReflectedRays))
				.collect(Collectors.toList());
		secondaryRays.addAll(rays);
	}

	public static int getMaxInteractionsOfReflectedRays() {
		return maxInteractionsOfReflectedRays;
	}

	public static void setMaxInteractionsOfReflectedRays(int maxInteractionsOfReflectedRays) {
		RayGeneratorDebug.maxInteractionsOfReflectedRays = maxInteractionsOfReflectedRays;
	}

	public void reset() {
		secondaryRays.clear();
	}

	public LightSource getLightSource() {
		return lightSource;
	}
}
