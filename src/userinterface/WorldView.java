package userinterface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;




@SuppressWarnings("serial")
public class WorldView extends JComponent {
	
	// Controller to view the world of
	private Controller controller;
	
	// Active site
	private Point active = null;
	
	public WorldView(Controller c) {
		controller = c;

		setOpaque(true);
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(600,600));
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		// Obtain a snapshot of the system in a thread-safe way
		Controller.Site[][] snapshot = controller.getSnapshot();
		if(snapshot == null) return;
		
		int sx = snapshot.length, sy = snapshot[0].length;

		double patchWidth = (double)(getWidth()-1)/(double)sx;
		double patchHeight = (double)(getHeight()-1)/(double)sy;
		
		// Draw grid lines
		g.setColor(Color.BLACK);
		for(int x=0; x<=sx; x++) {
			g.drawLine((int)(x*patchWidth), 0, (int)(x*patchWidth), getHeight()-1);
		}
		for(int y=0; y<=sy; y++) {
			g.drawLine(0, (int)(y*patchHeight), getWidth()-1, (int)(y*patchHeight));
		}
		
		
		for(int x=0; x<sx; x++) {
			for(int y=0; y<sy; y++) {
				Controller.Site s = snapshot[x][y];
				if(s == null) continue;
				int tlx = (int)(x*patchWidth), tly = (int)(y*patchHeight);
				int nlx = (int)((x+1)*patchWidth), nly = (int)((y+1)*patchHeight);
				if(s.food) {
					g.setColor(Color.GREEN);
					// Get the positions of this and the next lines that bound the square
					g.fillRect(tlx+1, tly+1, nlx-tlx-1, nly-tly-1);
				}
				if(active != null && active.x == x && active.y == y) {
					g.setColor(Color.BLACK);
					g.drawRect(tlx+1, tly+1, nlx-tlx-2, nly-tly-2);
				}
				// Find the centre of the patch
				double mx = (x + 0.5) * patchWidth, my = (y + 0.5) * patchHeight;
				for(int a=0; a<s.agents; a++) {
					// Calculate position to plot the agent
					double cx = mx, cy = my;
					if(s.agents > 1) {
						// Offset
						cx += 0.25*patchWidth * Math.sin(2.0*Math.PI*a / (double)s.agents);
						cy += 0.25*patchHeight * Math.cos(2.0*Math.PI*a / (double)s.agents);
					}
					// Fill the body, yellow if lit up
					g.setColor(a < s.lit ? Color.YELLOW : Color.GRAY);
					g.fillOval((int)(cx - 0.125*patchWidth), (int)(cy-0.125*patchHeight), (int)(0.25*patchWidth), (int)(0.25*patchHeight));
					// Add a black outline
					g.setColor(Color.BLACK);
					g.drawOval((int)(cx - 0.125*patchWidth), (int)(cy-0.125*patchHeight), (int)(0.25*patchWidth), (int)(0.25*patchHeight));				
				}
			}
		}
		
	}
	
	private Timer animationTimer = new Timer(40, new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) { if(controller.isSnapshotInvalid()) repaint(); }
	});

	public void jobStarted() {
		animationTimer.restart();
	}

	public void jobFinished() {
		animationTimer.stop();
		repaint();		
	}

	public void setActiveSite(int x, int y) {
		active = new Point(x,y);
		repaint();
	}
	
	public void clearActiveSite() {
		active = null;
		repaint();
	}

}
