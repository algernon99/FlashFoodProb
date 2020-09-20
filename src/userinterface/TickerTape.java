package userinterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

/**
 * This is a class to provide a scrolling, rewindable graph, within which individual sets can be
 * toggled.  Currently only allows integer time values, starting from zero, but this may be
 * changed in the future.
 * 
 * @author richard
 *
 */

@SuppressWarnings("serial")
public class TickerTape extends JComponent implements Iterable<Double[]> {
	
	private static final Font labelFont = new Font("Helvetica", Font.PLAIN, 10);

	private static final Color[] colours = new Color[]{
		Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE
	};
	
	private static final int RBORDER = 12;
	private static final int LBORDER = 36;
	private static final int YBORDER = 12;
	
	private boolean[] wereVisible = null;
	private List<Series> series = new ArrayList<Series>();
	
	private class Series {
		boolean visible = true;
		int offset = end-1;
		List<Double> yvalues = new ArrayList<Double>();
	}

	private int window; // Window onto the tape to display
	private int end=1; // First point past the end of the tape (start is zero)
	private boolean following = false;
	private boolean yAuto = false; // auto-scale y in units of yMax-yMin
	private int left;
	private double yMin, yMax; // Min and max y-values
	
	public TickerTape(int window, double yMin, double yMax, boolean yAuto) {
		this.window = window;
		this.yMin = yMin;
		this.yMax = yMax;
		this.yAuto = yAuto;
		
		this.left = this.end - this.window;
		
		addMouseListener(mouseFollower);
		
		setBackground(Color.WHITE);
		setOpaque(true);
	}
	
	private Series ensureSet(int set) {
		while(series.size() <= set) series.add(null);
		Series ser = series.get(set);
		if(ser == null) {
			ser = new Series();
			if(wereVisible != null && wereVisible.length > set) ser.visible = wereVisible[set];
			series.set(set, ser);
		}
		return ser;
	}
	
	public void addPoint(int set, double value) {
		Series ser = ensureSet(set);
		ser.yvalues.add(value);
		if(ser.yvalues.size() > end) end = ser.yvalues.size();
		if(isShowing() && (following || ser.offset+ser.yvalues.size()<=left+window)) repaint();
	}
	
	public void setShowing(int set, boolean show) {
		Series ser = ensureSet(set);
		if(ser.visible == show) return;
		ser.visible = show;
		if(isShowing()) repaint();
	}
	
	public boolean isSetShowing(int set) {
		Series ser = ensureSet(set);
		return ser.visible;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		int width = getWidth();
		int height = getHeight();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
		}
		
		// X-scaling
		int wstart = following ? end-window : left;
		int wend = wstart+window;
		// xscreen = xs * (xreal - wstart)
		double xs = (double)(width-LBORDER-RBORDER) / (double)(window-1);

		// Y-scaling
		double yMaxActual = yMax;
		int yBlocks = 1;
		if(yAuto) {
			// If auto-scaling y, then calculate actual maximum
			for(Series ser : series) {
				if(ser != null && ser.visible) {
					for(int i=(wstart < ser.offset ? ser.offset : wstart); i<=wend && i<ser.offset+ser.yvalues.size(); i++) {
						double y = ser.yvalues.get(i-ser.offset);
						if(y>yMaxActual) {
							yBlocks = (int)((y-yMin)/(yMax-yMin)+1);
							yMaxActual = yMin + (yMax-yMin) * yBlocks;
						}
					}							
				}
			}
			
		}
		// yscreen = y0 + yreal * ys;
		double ys = (double)(height-2*YBORDER) / (yMin - yMaxActual);
		double y0 = YBORDER - yMaxActual * ys;
		
		int n=0;

		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(LBORDER, YBORDER, width-LBORDER-RBORDER, height-2*YBORDER);
		if(yMin < 0.0 && yMax > 0.0) {
			g.drawLine((int)LBORDER, (int)(y0+0.5), width-RBORDER, (int)(y0+0.5));
		}
		// Horizontal ticks
		for(int i=0; i<window; i++) {
			g.drawLine((int)(LBORDER+xs*i+0.5), height-YBORDER, (int)(LBORDER+xs*i+0.5), height-YBORDER+2);
			g.drawLine((int)(LBORDER+xs*i+0.5), YBORDER-2, (int)(LBORDER+xs*i+0.5), YBORDER);
		}
		// Vertical ticks
		for(int i=0; i<=yBlocks; i++) {
			g.drawLine(LBORDER-2, (int)(YBORDER-i*(yMax-yMin)*ys), LBORDER, (int)(YBORDER-i*(yMax-yMin)*ys));
			g.drawLine(width-RBORDER, (int)(YBORDER-i*(yMax-yMin)*ys), width-RBORDER+2, (int)(YBORDER-i*(yMax-yMin)*ys));
		}
		
		// Labels
		g.setFont(labelFont);
		String label = Integer.toString(wstart);
		Rectangle2D r = labelFont.getStringBounds(label, g2.getFontRenderContext());
		g.drawString(label, LBORDER, (int)(height-YBORDER+3-r.getMinY()+0.5));
		label = Integer.toString(wend);
		r = labelFont.getStringBounds(label, g2.getFontRenderContext());
		g.drawString(label, (int)(width-RBORDER-r.getWidth()+1.5), (int)(height-YBORDER+3-r.getMinY()+0.5));
		label = Double.toString(yMin);
		r = labelFont.getStringBounds(label, g2.getFontRenderContext());
		g.drawString(label, (int)(LBORDER-3 - r.getWidth()+0.5), (int)(height-YBORDER+0.5*r.getHeight()+0.5));
		label = Double.toString(yMaxActual);
		r = labelFont.getStringBounds(label, g2.getFontRenderContext());
		g.drawString(label, (int)(LBORDER-3 - r.getWidth()+0.5), (int)(YBORDER+0.5*r.getHeight()+0.5));
		
		g.setClip(LBORDER,YBORDER,width-LBORDER-RBORDER+1,height-2*YBORDER+1);
		for(Series ser : series) {
			if(ser != null && ser.visible) {
				g.setColor(colours[n]);
				for(int i=(wstart < ser.offset ? ser.offset : wstart); i<wend && i<ser.offset+ser.yvalues.size()-1; i++) {
					double y1 = ser.yvalues.get(i-ser.offset), y2 = ser.yvalues.get(i-ser.offset+1);
					g.drawLine((int)(LBORDER+xs*(i-wstart)+0.5), (int)(y0+ys*y1+0.5), (int)(LBORDER+xs*(i-wstart+1)+0.5), (int)(y0+ys*y2+0.5));
				}							
			}
			n = (n+1) % colours.length;
		}
		g.setClip(null);
		
	}
	
	/**
	 * Reset all data series, optionally retaining the visibility attributes
	 * 
	 * @param saveVisibility keeps the visibility of the data series if true
	 */
	public void clear(boolean saveVisibility) {
		// Store the visibility of data series, so this is restored when they are recreated
		if(series.size() > 0) {
			wereVisible = new boolean[series.size()];
			for(int i=0; i<series.size(); i++) {
				Series s = series.get(i);
				wereVisible[i] = s == null ? true : s.visible;
			}
		} else {
			wereVisible = null;
		}
		series = new ArrayList<Series>();
		end = 1;
		if(!following) left = end - window;
		if(isShowing()) repaint();
	}
	
	/**
	 * A class that observes mouse movements to allow the user to set the bob positions
	 */
	private MouseAdapter mouseFollower = new MouseAdapter() {
		private Point lastClick = null; // start of drag, if drag in progress
		private int base;
		private double xs;

		@Override
		public void mousePressed(MouseEvent e) {
			lastClick = e.getPoint();
			base = left;
			xs = (double)(getWidth()-LBORDER-RBORDER) / (double)(window-1);
			
			addMouseMotionListener(mouseFollower);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(lastClick == null) return;
			Point p = e.getPoint();
			setLeftEdge(base - (int)((double)(p.x - lastClick.x)/xs));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(lastClick == null) return;
			removeMouseMotionListener(mouseFollower);
			lastClick = null;
		}
		
	};
	
	public void setFollowing(boolean follow) {
		if(following == follow) return;
		following = follow;
		if(follow) {
			removeMouseListener(mouseFollower);
		} else {
			left = end - window;
			addMouseListener(mouseFollower);
		}
	}

	protected void setLeftEdge(int setLeft) {
		if(setLeft<0) setLeft = 0;
		if(setLeft>end-window) setLeft = end-window;
		if(!following && setLeft != left) {
			left = setLeft;
			if(isShowing()) repaint();
		}
		
	}
	
	public Color getSetColor(int set) {
		return colours[set % colours.length];
	}

	private class Dumper implements Iterator<Double[]> {
		
		int cur = 0;
		int last = end;
		
		private int visibleSets[] = null;
		
		public Dumper() {
			int vis = 0;
			for(Series s : series) {
				if(s.visible) vis++;
			}
			visibleSets = new int[vis];
			int j=0;
			for(int i=0; i<series.size(); i++) {
				if(series.get(i).visible) visibleSets[j++] = i;
			}
		}
		
		@Override
		public boolean hasNext() {
			return cur < last;
		}

		@Override
		public Double[] next() {
			Double[] ret = new Double[visibleSets.length];
			int j = 0;
			for(int i : visibleSets) {
				Series s = series.get(i);
				if(cur < s.offset || cur >= s.offset + s.yvalues.size()) ret[j] = null;
				else ret[j] = s.yvalues.get(cur-s.offset);
				j++;
			}
			cur++;
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * Provide an iterator over the numerical data stored on the ticker tape (restricted to visible sets)
	 * Use this, e.g., in a loop as 
	 * for(Double[] row : tape) { 
	 * 	for(Double d : row) 
	 * 		{ System.out.print(d + "\t"); }
	 *  System.out.println();
	 * }
	 * to export data
	 * 
	 */
	
	@Override
	public Iterator<Double[]> iterator() {
		return new Dumper();
	}

	
}
