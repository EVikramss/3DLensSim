package com.imaging.solver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.imaging.analysis.Analyzer;
import com.imaging.imager.ImageSensor;
import com.imaging.objects.InteractableCompositeObject;
import com.imaging.ray.LightRay;
import com.imaging.ray.RayGenerator;

/**
 * Controllers and adapter are singleton beans. Image, composite etc... are
 * session beans i.e., only threads with valid session id can create/access
 * session beans.
 * 
 * So when initializing to prevent errors, mark session beans as proxy so that a
 * proxy object can be injected into the singleton beans. And the proxy points
 * to the relevant session bean during run time.
 * 
 * When spawning async threads within a session, need a way to pass session info
 * to new thread as well..
 * 
 * 
 */

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ImageSolver {

	private static final Logger LOGGER = LogManager.getLogger(ImageSolver.class);

	@Autowired
	private RayGenerator rayGenerator;

	@Autowired
	private InteractableCompositeObject composite;

	@Autowired
	private ImageSensor imagePlane;
	private List<LightRay> allRays = new ArrayList<LightRay>();
	private float distortionScore = 0.0f;
	private volatile boolean isJobRunning = false;

	@Autowired
	private Analyzer analyzer;

	//private ExecutorService service = Executors.newSingleThreadExecutor();
	private Future<BufferedImage> future;

	public String solve() {
		String status = "Job already running!";
		
		if(!isJobRunning) {
			System.out.println("Solving ...");
			allRays = new ArrayList<LightRay>();
			rayGenerator.reset();
			while (rayGenerator.hasNext()) {
				List<LightRay> raysFromSource = rayGenerator.next();

				for (LightRay ray : raysFromSource) {
					composite.propagateRay(ray);
					if (!ray.hasChildren()) // consider only leaf nodes of light ray tree
						allRays.add(ray);
				}
			}
			
			imagePlane.interpret(allRays, false);
			
			distortionScore = analyzer.computeDistortionScore();
			isJobRunning = false;
			status = "Job Complete";
		}
		
		return status;
	}

	/*
	 * public String solve1() {
	 * 
	 * if (future != null && !future.isDone()) { // job still executing. Ignore this
	 * request return "Job already running. Request ignored"; }
	 * 
	 * // submit call as async and return future = service.submit(() -> { allRays =
	 * new ArrayList<LightRay>(); rayGenerator.reset(); while
	 * (rayGenerator.hasNext()) { List<LightRay> raysFromSource =
	 * rayGenerator.next();
	 * 
	 * for (LightRay ray : raysFromSource) { composite.propagateRay(ray); if
	 * (!ray.hasChildren()) // consider only leaf nodes of light ray tree
	 * allRays.add(ray); } }
	 * 
	 * return imagePlane.interpret(allRays, false); });
	 * 
	 * // check for any exceptions service.submit(() -> { try { future.get();
	 * distortionScore = analyzer.computeDistortionScore();
	 * System.out.println(distortionScore); } catch (InterruptedException e) {
	 * LOGGER.error(e.getMessage()); System.out.println(e.getMessage());
	 * e.printStackTrace(); } catch (ExecutionException e) {
	 * LOGGER.error(e.getMessage()); System.out.println(e.getMessage());
	 * e.printStackTrace(); } });
	 * 
	 * return "Job submitted"; }
	 */

	public boolean isComputationRunning() {
		return future != null && !future.isDone() ? true : false;
	}

	public List<LightRay> getAllRays() {
		return allRays;
		// return imagePlane.getMarginalRays();
	}

	public BufferedImage getOutputImage() {
		BufferedImage outputImg = imagePlane.getInterpretedImage();
		return outputImg;
	}

	public BufferedImage reInterpretRaysForSensorUpdate() {
		BufferedImage outputImg = imagePlane.interpret(allRays, true);
		distortionScore = analyzer.computeDistortionScore();
		System.out.println(distortionScore);
		return outputImg;
	}
}
