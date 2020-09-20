package userinterface;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import simulation.World.Summary;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel {

	private Controller controller;
	
	private String series[] = new String[]{ "B", "C", "M", "D" };
	
	private TickerTape statsPlot = new TickerTape(100, -1.0, 1.0, false);
	private TickerTape domPlot = new TickerTape(100, -1.0, 1.0, false);
	private JButton exportButton = new JButton("Export...");
		
	public GraphPanel(Controller c) {
		controller = c;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		final int sesLen = series.length;
		JPanel statsLegend = new JPanel();
		statsLegend.setLayout(new BoxLayout(statsLegend, BoxLayout.LINE_AXIS));
		statsLegend.add(new JLabel("Average"));
		statsLegend.add(Box.createHorizontalGlue());
		JPanel domLegend = new JPanel();
		domLegend.setLayout(new BoxLayout(domLegend, BoxLayout.LINE_AXIS));
		domLegend.add(new JLabel("Dominant"));
		domLegend.add(Box.createHorizontalGlue());
		for(int i=0; i<sesLen; i++) {
			final JCheckBox scb = new JCheckBox(series[i], true);
			statsPlot.setShowing(i, true);
			scb.setForeground(statsPlot.getSetColor(i));
			final int stoggle = i;
			scb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					statsPlot.setShowing(stoggle, scb.isSelected());
				}
			});
			statsLegend.add(scb);
			final JCheckBox dcb = new JCheckBox(series[i], true);
			domPlot.setShowing(i, true);
			dcb.setForeground(domPlot.getSetColor(i));
			final int dtoggle = i;
			dcb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					domPlot.setShowing(dtoggle, dcb.isSelected());
				}
			});
			domLegend.add(dcb);
		}
		statsLegend.setMaximumSize(statsLegend.getPreferredSize());
		add(statsLegend);
		add(statsPlot);

		domLegend.setMaximumSize(domLegend.getPreferredSize());
		add(domLegend);
		add(domPlot);		
		
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if(chooser.showSaveDialog(controller.getFrame()) == JFileChooser.APPROVE_OPTION) {
					try {
						PrintStream out = new PrintStream(new FileOutputStream(chooser.getSelectedFile()), true);
						exportStatistics(out);
						out.close();
					} catch(FileNotFoundException error) {
						JOptionPane.showMessageDialog(controller.getFrame(), error.getLocalizedMessage(), "Error exporting data", JOptionPane.ERROR_MESSAGE);
					}
				}
			}			
		});
		exportButton.setEnabled(false);
		add(new JPanel(new FlowLayout(FlowLayout.RIGHT)).add(exportButton));
	}

	public void addStatistics() {
		Summary[] stats = controller.getStatistics();
		if(stats == null) return;
		for(Summary s : stats) {
			// Add new data to the plot
			statsPlot.addPoint(0, s.meanBrightness);
			statsPlot.addPoint(1, s.meanContrast);
			statsPlot.addPoint(2, s.meanMotility);
			statsPlot.addPoint(3, s.meanDirectedness);
			domPlot.addPoint(0, s.domBrightness);
			domPlot.addPoint(1, s.domContrast);
			domPlot.addPoint(2, s.domMotility);
			domPlot.addPoint(3, s.domDirectedness);
		}
	}
	
	public void exportStatistics(PrintStream out) {
		int t = 0;
		Iterator<Double[]> statsIt = statsPlot.iterator();
		Iterator<Double[]> domIt = domPlot.iterator();
		while(statsIt.hasNext() || domIt.hasNext()) {
			t++;
			out.print(t);
			if(statsIt.hasNext()) {
				Double[] vals = statsIt.next();
				for(Double d : vals) {
					out.print( (d == null) ? ("\t\t") : ("\t" + d) );
				}
			} else {
				for(@SuppressWarnings("unused") String s : series) {
					out.print("\t\t");
				}
			}
			if(domIt.hasNext()) {
				Double[] vals = domIt.next();
				for(Double d : vals) {
					out.print( (d == null) ? ("\t\t") : ("\t" + d) );
				}
			} else {
				for(@SuppressWarnings("unused") String s : series) {
					out.print("\t\t");
				}
			}
			out.println();
		}
	}
	
	// While the simulation is running, update the statistics
	
	private Timer animationTimer = new Timer(1000, new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) { addStatistics(); }
	});

	public void jobStarted() {
		exportButton.setEnabled(false);
		statsPlot.setFollowing(true);
		domPlot.setFollowing(true);
		animationTimer.restart();
	}

	public void jobFinished() {
		animationTimer.stop();
		addStatistics();
		statsPlot.setFollowing(false);
		domPlot.setFollowing(false);
		exportButton.setEnabled(true);
	}

	public void worldUpdated() {
		// Clear the plots
		statsPlot.clear(true);
		domPlot.clear(true);
		exportButton.setEnabled(false);
	}

}
