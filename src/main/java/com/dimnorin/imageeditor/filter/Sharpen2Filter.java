package com.dimnorin.imageeditor.filter;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Sharpen filter with another sharpen matrix 
 */
public class Sharpen2Filter extends AbstractBufferedImageOp {

	float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f, -1.0f, 0.0f };

	// new float[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 }

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		Kernel kernel = new Kernel(3, 3, elements);
		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(src, null);
	}

}
