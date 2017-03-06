package com.dimnorin.imageeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import com.dimnorin.imageeditor.filter.AbstractBufferedImageOp;

/**
 * 
 * Panel to draw image 
 *
 */
public class ImagePanel extends JPanel {
	/**
	 * Working image sample
	 */
	private Image img;
	/**
	 * Bounds of original image
	 */
	private Dimension imgSize;
	/**
	 * Internal images to apply filters and adjustments
	 */
	private BufferedImage biSrc;
	private BufferedImage biDst;
	/**
	 * Rectangles used to draw selection
	 */
	private Rectangle curRect = null;
	private Rectangle rectDraw = null;
	private Rectangle prevRectDrawn = new Rectangle();

	public ImagePanel(String img) {
		this(new ImageIcon(img).getImage());
	}

	public ImagePanel(Image image) {
		// this.img = image;

		setImg(image);

		addMouseListener(new MListener());
		addMouseMotionListener(new MListener());
	}
	/**
	 * Set a new image to panel
	 * @param image
	 */
	public void setImg(Image image){
		biSrc = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics2D big = biSrc.createGraphics();
		big.drawImage(image, 0, 0, null);

		biDst = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

		img = biSrc;
		this.imgSize = new Dimension(img.getWidth(this), img.getHeight(this));
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // <-- Insert me here and watch problem go away

		// Scale image to fit panel bounds and draw it on panel center
		Dimension boundary = getSize();
		Dimension scaled = getScaledDimension(imgSize, boundary);

		BufferedImage resizedImage = new BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImage.createGraphics();
		g2.drawImage(img, 0, 0, scaled.width, scaled.height, this);
		g2.dispose();
		g.drawImage(resizedImage, (boundary.width - scaled.width) / 2, (boundary.height - scaled.height) / 2, this);

		if (curRect != null) {
			// Draw selection rectangle
			float thickness = 4;
			g2.setStroke(new BasicStroke(thickness));
			g.setColor(Color.RED);
			g.drawRect(rectDraw.x, rectDraw.y, rectDraw.width - 1, rectDraw.height - 1);
		}
	}
	/**
	 * Apply filters to image
	 * @param filters
	 */
	public void applyFilter(List<AbstractBufferedImageOp> filters) {
		BufferedImage bi = null;
		for (AbstractBufferedImageOp filter : filters) {
			bi = filter.filter(bi != null ? bi : biSrc, biDst);
			// bi = biDst;
		}
		if (bi != null)
			img = bi;
	}
	/**
	 * Do image crop
	 */
	public void doCrop(){
		if (curRect != null) {
			// Do calculation to convert coordinates from panel to original image
			Dimension boundary = getSize();
			Dimension scaled = getScaledDimension(imgSize, boundary);
			
			int dx = (boundary.width - scaled.width) / 2;
			int dy = (boundary.height - scaled.height) / 2;
			float k = (float)scaled.width / imgSize.width; 
			
			int x = (int)((rectDraw.x - dx) / k);
			int y = (int)((rectDraw.y - dy) / k);
			int w = (int)((rectDraw.width - 1) / k);
			int h = (int)((rectDraw.height - 1) / k);
			
			BufferedImage bi = biSrc.getSubimage(x, y, w, h);
			setImg(bi);
			curRect = null;
		}
	}
	/**
	 * Get current image
	 * @return
	 */
	public Image getImg() {
		return img;
	}
	/**
	 * Scale image to fit panel bounds and calculate it dimensions
	 * @param imageSize
	 * @param boundary
	 * @return
	 */
	private Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {
		double widthRatio = boundary.getWidth() / imageSize.getWidth();
		double heightRatio = boundary.getHeight() / imageSize.getHeight();
		double ratio = Math.min(widthRatio, heightRatio);

		return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
	}
	/**
	 * Update selection rectangle
	 * @param compW
	 * @param compH
	 */
	private void updateDrawableRect(int compW, int compH) {
		int x = curRect.x;
		int y = curRect.y;
		int w = curRect.width;
		int h = curRect.height;

		if (w < 0) {
			w = 0 - w;
			x = x - w + 1;

			if (x < 0) {
				w += x;
				x = 0;
			}
		}

		if (h < 0) {
			h = 0 - h;
			y = y - h + 1;
			if (y < 0) {
				h += y;
				y = 0;
			}
		}

		if ((x + w) > compW) {
			w = compW - x;
		}

		if ((y + h) > compH) {
			h = compH - y;
		}

		if (rectDraw != null) {
			prevRectDrawn.setBounds(rectDraw.x, rectDraw.y, rectDraw.width, rectDraw.height);
			rectDraw.setBounds(x, y, w, h);
		} else {
			rectDraw = new Rectangle(x, y, w, h);
		}
	}
	/**
	 * Listen to mouse events to draw selection rectangle 
	 * 
	 */
	private class MListener extends MouseInputAdapter {

		@Override
		public void mousePressed(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();

			curRect = new Rectangle(x, y, 0, 0);
			updateDrawableRect(getWidth(), getHeight());
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updateSize(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			updateSize(e);
		}

		void updateSize(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			curRect.setSize(x - curRect.x, y - curRect.y);
			updateDrawableRect(getWidth(), getHeight());

			Rectangle totalRepaint = rectDraw.union(prevRectDrawn);
			repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);
		}
	}
}
