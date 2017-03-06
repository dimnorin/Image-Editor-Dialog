package com.dimnorin.imageeditor;

import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedList;

import com.dimnorin.imageeditor.filter.AbstractBufferedImageOp;
import com.dimnorin.imageeditor.filter.BrightContrastFilter;
import com.dimnorin.imageeditor.filter.HSBAdjustFilter;
import com.dimnorin.imageeditor.filter.RemoveRedEyeFilter;
import com.dimnorin.imageeditor.filter.Sharpen2Filter;

/**
 * Image operation controller
 * 
 */
public class ImageAdjuster {
	/**
	 *  Contrast
	 */
	public static final int DEF_SCALE = 1;
	/**
	 * Brightness
	 */
	public static final int DEF_OFFSET = 10;
	/**
	 * Hue
	 */
	public static final int DEF_HUE = 0;
	/**
	 * Saturation
	 */
	public static final int DEF_SATURATION = 1;
	
	/**
	 *  Contrast
	 */
	private float scale = DEF_SCALE;
	/**
	 * Brightness
	 */
    private float offset = DEF_OFFSET;
    
    private float hue = DEF_HUE;
    
    private float saturation = DEF_SATURATION;
    /**
     * Original image, will be used to do image reset
     */
    private Image originalImage;
    /**
     * Image draw panel
     */
    private ImagePanel imagePanel;
    /**
     * List of filters will be applied to image
     */
    private LinkedList<AbstractBufferedImageOp> filters = new LinkedList<>();
    
    
    public ImageAdjuster(ImagePanel imgPanel) {
		super();
		this.imagePanel = imgPanel;
		this.originalImage = imgPanel.getImg();
	}
    /**
     * Adjust image parameters
     * @param scale
     * @param offset
     * @param hue
     * @param saturation
     */
    public void adjust(float scale, float offset, float hue, float saturation){
    	this.scale = scale;
    	this.offset = offset;
    	this.hue = hue;
		this.saturation = saturation;
		
//		System.out.println(String.format("scale=%f, offset=%f, hue=%f, satur=%f", scale, offset, hue, saturation));
    	
    	ArrayList<AbstractBufferedImageOp> workFilters = new ArrayList<>();
    	workFilters.add(new BrightContrastFilter(scale, offset));
    	workFilters.add(new HSBAdjustFilter(hue, saturation, 1));
    	workFilters.addAll(filters);
    	
    	imagePanel.applyFilter(workFilters);
		imagePanel.repaint();
    }
	/**
	 * Adjust image contrast
	 * @param scale
	 */
	public void adjustContrast(float scale){
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Adjust image brightness
	 * @param offset
	 */
	public void adjustBrightness(float offset){
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Adjust image hue
	 * @param hue
	 */
	public void adjustHue(float hue){
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Adjust image saturation
	 * @param saturation
	 */
	public void adjustSaturation(float saturation){
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Add sharpen filter
	 */
	public void addSharpen(){
		filters.add(new Sharpen2Filter());
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Ad remove red eye filter
	 */
	public void addRemoveRedEye(){
		filters.add(new RemoveRedEyeFilter());
		adjust(scale, offset, hue, saturation);
	}
	/**
	 * Remove last applied filter
	 */
	public void undoLastFilter(){
		if(filters.size() > 0){
			filters.removeLast();
			adjust(scale, offset, hue, saturation);
		}
	}
	/**
	 * Crop image
	 */
	public void doCrop(){
		imagePanel.doCrop();
		imagePanel.repaint();
	}
	/**
	 * Save image
	 */
	public void doSave(){
		//Save should be here
	}
	/**
	 * Reset image to it's original condition
	 */
	public void resetImage(){
		imagePanel.setImg(originalImage);
		filters.clear();
		
		scale = DEF_SCALE;
		offset = DEF_OFFSET;
		hue = DEF_HUE;
		saturation = DEF_SATURATION;
		
		imagePanel.repaint();
	}
}
