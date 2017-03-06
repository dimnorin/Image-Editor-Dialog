package com.dimnorin.imageeditor.filter;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Adjust Brightness and Contrast
 * 
 */
public class BrightContrastFilter extends AbstractBufferedImageOp{
	/**
	 *  Contrast
	 */
	private float scale = 1.0f;
	/**
	 * Brightness
	 */
    private float offset = 10;
	
	public BrightContrastFilter(float scale, float offset) {
		super();
		this.scale = scale;
		this.offset = offset;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		RescaleOp rescale = new RescaleOp(scale, offset, null);
        rescale.filter(src, dest);
        return dest;
	}

}
