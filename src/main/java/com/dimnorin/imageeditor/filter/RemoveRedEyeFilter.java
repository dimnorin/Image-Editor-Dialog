package com.dimnorin.imageeditor.filter;

import java.awt.Color;

public class RemoveRedEyeFilter extends PointFilter{

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		
		float redIntensity = ((float)r / ((g + b) / 2));
		if (redIntensity > 2.0f)  // 1.5 because it gives the best results
		{
		    // reduce red to the average of blue and green
			rgb = new Color((g + b) / 2, g, b).getRGB();
//			rgb = new Color(90, g, b).getRGB();
		}
		return a | (rgb & 0xffffff);
	}

}
