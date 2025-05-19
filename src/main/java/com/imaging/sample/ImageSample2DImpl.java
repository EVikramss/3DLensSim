package com.imaging.sample;

import java.io.File;
import java.io.IOException;

public class ImageSample2DImpl extends Abstract2DSample implements Sample {

	public ImageSample2DImpl(File imagefile, float sampleHeight, float sampleWidth, float heightPerPixel,
			float widthPerPixel, Abstract2DSample.SamplingRate samplingRate) throws IOException {
		super(imagefile, sampleHeight, sampleWidth, heightPerPixel,
				widthPerPixel, samplingRate);
	}
}
