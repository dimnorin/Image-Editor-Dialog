package com.dimnorin.imageeditor.filter;

import java.awt.*;

public class HSBAdjustFilter extends PointFilter {
	
	public float hFactor, sFactor, bFactor;
	protected float[] hsb = new float[3];
	
	public HSBAdjustFilter() {
		this(0, 0, 0);
	}

	public HSBAdjustFilter(float r, float g, float b) {
		hFactor = r;
		sFactor = g;
		bFactor = b;
		canFilterIndexColorModel = true;
	}

	public void setHFactor( float hFactor ) {
		this.hFactor = hFactor;
	}
	
	public float getHFactor() {
		return hFactor;
	}
	
	public void setSFactor( float sFactor ) {
		this.sFactor = sFactor;
	}
	
	public float getSFactor() {
		return sFactor;
	}
	
	public void setBFactor( float bFactor ) {
		this.bFactor = bFactor;
	}
	
	public float getBFactor() {
		return bFactor;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		Color.RGBtoHSB(r, g, b, hsb);
		// Modify the hue value for the pixel based on the
		// current value of the Hue slider. Note that this
		// statement performs addition instead of
		// multiplication.
		hsb[0] += hFactor;
		// Modify the saturation value for the pixel based
		// on the current value of the Sat slider. Note
		// that multiplication is used here.
		hsb[1] *= sFactor;
		// If the computed value is greter than 1.0, clip it
		// at 1.0.
		if (hsb[1] > 1.0)
			hsb[1] = 1.0f;
		// Modify the brightness value for the pixel based
		// on the current value of the Bright slider. Once
		// again, multiplication is used here.
		hsb[2] *= bFactor;
		if (hsb[2] > 1.0)
			hsb[2] = 1.0f;

		rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
		return a | (rgb & 0xffffff);
	}

	public String toString() {
		return "Colors/Adjust HSB...";
	}
}

