package com.dimnorin.imageeditor;

import javax.swing.JDialog;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import net.miginfocom.swing.MigLayout;
import javax.swing.JSlider;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * 
 * Image editor dialog 
 *
 */
public class ImageEditorDialog extends JDialog{
	// Default values
	public static final int BRIGHTNESS_MULT = 1;
	public static final int CONTRAST_MULT = 10;
	public static final int HUE_MULT = 360;
	public static final int SATURATION_MULT = 100;
	
	/**
	 * Initial image file
	 */
	private File imageFile;
	/**
	 * Initial image
	 */
	private Image image;
	/**
	 * Image adjustments controller
	 */
	private ImageAdjuster imageAdjuster;
	/**
	 * Panel to show adjusted image
	 */
	private ImagePanel imgPanel;
	
	public ImageEditorDialog(File imageFile) {
		this.imageFile = imageFile;
		this.image = new ImageIcon(imageFile.getAbsolutePath()).getImage();
		
		setTitle("Edit Photo");
		setSize(new Dimension(800, 600));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		imgPanel = new ImagePanel(image);
		imgPanel.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(imgPanel, BorderLayout.CENTER);
		this.imageAdjuster = new ImageAdjuster(imgPanel);
		
		JPanel propsPanel = new JPanel();
		getContentPane().add(propsPanel, BorderLayout.NORTH);
		propsPanel.setLayout(new BoxLayout(propsPanel, BoxLayout.X_AXIS));
		
		JPanel adjPanel = new JPanel();
		adjPanel.setBorder(new TitledBorder(null, "Adjustments", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		adjPanel.setLayout(new MigLayout("", "[][][][][grow][grow]", "[grow][]"));
		propsPanel.add(adjPanel);
		
		JLabel lblBrightness = new JLabel("Brightness");
		adjPanel.add(lblBrightness, "cell 0 0");
		
		JSlider slBrightness = new JSlider(0, 255, BRIGHTNESS_MULT * ImageAdjuster.DEF_OFFSET);
		slBrightness.setMinorTickSpacing(5);
		slBrightness.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();

		        int value = source.getValue() / BRIGHTNESS_MULT;
		        imageAdjuster.adjustBrightness(value);
			}
		});
		adjPanel.add(slBrightness, "cell 1 0");
		
		JLabel lblHue = new JLabel("Hue");
		adjPanel.add(lblHue, "cell 2 0");
		
		JSlider slHue = new JSlider(0, 360, HUE_MULT * ImageAdjuster.DEF_HUE);
		slHue.setMinorTickSpacing(10);
		slHue.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();

				float value = (float)source.getValue() / HUE_MULT;
		        imageAdjuster.adjustHue(value);
			}
		});
		adjPanel.add(slHue, "cell 3 0");
		
		JLabel lblContrast = new JLabel("Contrast");
		adjPanel.add(lblContrast, "cell 0 1");
		
		JSlider slContrast = new JSlider(0, 20, CONTRAST_MULT * ImageAdjuster.DEF_SCALE);
		slContrast.setMinorTickSpacing(1);
		slContrast.addChangeListener(new ChangeListener() {
			@Override	
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();

		        float value = (float)source.getValue() / CONTRAST_MULT;
		        imageAdjuster.adjustContrast(value);
			}
		});
		adjPanel.add(slContrast, "cell 1 1");
		
		JLabel lblSaturation = new JLabel("Saturation");
		adjPanel.add(lblSaturation, "cell 2 1");
		
		JSlider slSaturation = new JSlider(0, 400, SATURATION_MULT * ImageAdjuster.DEF_SATURATION);
		slSaturation.setMinorTickSpacing(10);
		slSaturation.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();

		        float value = (float)source.getValue() / SATURATION_MULT;
		        imageAdjuster.adjustSaturation(value);
			}
		});
		adjPanel.add(slSaturation, "cell 3 1");
		
		JPanel cropPanel = new JPanel();
		cropPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		cropPanel.setBorder(new TitledBorder(null, "Crop", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		propsPanel.add(cropPanel);
		
		JButton btnCrop_1 = new JButton("Crop");
		btnCrop_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageAdjuster.doCrop();
			}
		});
		cropPanel.add(btnCrop_1);
		
		JPanel filtersPanel = new JPanel();
		filtersPanel.setBorder(new TitledBorder(null, "Filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		propsPanel.add(filtersPanel);
		
		JComboBox<String> cmbFilters = new JComboBox<>();
		cmbFilters.addItem("Sharpen");
		cmbFilters.addItem("Clear Red Eye");
		filtersPanel.setLayout(new MigLayout("", "[][]", "[][]"));
		filtersPanel.add(cmbFilters, "cell 0 0 2 1,growx,aligny center");
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String)cmbFilters.getSelectedItem()) {
				case "Sharpen":
					imageAdjuster.addSharpen();
					break;
				case "Clear Red Eye":
					imageAdjuster.addRemoveRedEye();
					break;
				default:
					break;
				}
			}
		});
		
		JButton btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageAdjuster.undoLastFilter();
			}
		});
		filtersPanel.add(btnUndo, "cell 0 1");
		filtersPanel.add(btnApply, "cell 1 1,alignx left,aligny top");
		
		JPanel footerPanel = new JPanel();
		getContentPane().add(footerPanel, BorderLayout.SOUTH);
		
		JButton btnResetImage = new JButton("Reset Photo");
		btnResetImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageAdjuster.resetImage();
				
				slBrightness.setValue(BRIGHTNESS_MULT * ImageAdjuster.DEF_OFFSET); 
				slContrast.setValue(CONTRAST_MULT * ImageAdjuster.DEF_SCALE);
				slHue.setValue(HUE_MULT * ImageAdjuster.DEF_HUE);
				slSaturation.setValue(SATURATION_MULT * ImageAdjuster.DEF_SATURATION);
			}
		});
		footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		footerPanel.add(btnResetImage);
		
		JButton btnApplyToAll = new JButton("Apply to All");
		footerPanel.add(btnApplyToAll);
		
		JButton btnApply_1 = new JButton("Apply");
		footerPanel.add(btnApply_1);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footerPanel.add(btnClose);
		
	}
	
	public static void main(String[] args){
		File f = new File("d:\\Downloads\\Red-eye_effect.png");
		new ImageEditorDialog(f).setVisible(true);;
	}
}
