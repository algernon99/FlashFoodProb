package userinterface;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * A panel for iterating the basic dynamics of the model
 *
 * @author richard
 *
 */

@SuppressWarnings("serial")
public class RunPanel extends JPanel {

	private final static int DEFAULT_SWEEPS = 1000;
	private final static int DEFAULT_GENERATIONS = 1000;
	private final static double DEFAULT_MUTATION = 0.01;

	private Controller controller;

	private JButton oneSweepButton = new JButton("Sweep");
	private JButton multiSweepButton = new JButton("Sweeps");
	private JButton regenerateButton = new JButton("Regenerate");
	private JButton multiGenerationButton = new JButton("Generations");
	private JButton stopButton = new JButton("Stop");
	private JButton resetButton = new JButton("Reset");

	private JSpinner sweepsSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_SWEEPS, 1, null, 1));
	private JSpinner generationsSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_GENERATIONS, 1, null, 1));
	private JSpinner mutSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_MUTATION, 0.00, 1.00, 0.005));

	public RunPanel(Controller c) {

		controller = c;

		// Bind buttons to controller actions
		oneSweepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { controller.runSweep(1); }
		});

		regenerateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.runRegeneration((Double)mutSpinner.getValue()); }
		});

		multiSweepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { controller.runSweep((Integer)sweepsSpinner.getValue()); }
		});

		multiGenerationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.runGenerations((Integer)generationsSpinner.getValue(), (Integer)sweepsSpinner.getValue(), (Double)mutSpinner.getValue());
			}
		});

		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.stopJob(false);
			}
		});
		stopButton.setEnabled(false);

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.Reset();
			}

		});


		// Layout the widgets within the panel

		setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

		// Use a group layout
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		JLabel mutLabel = new JLabel("Mutate");

		// Define the controls that appear as we look left-to-right across the panel
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(sweepsSpinner)
			.addGroup(layout.createParallelGroup()
				.addComponent(oneSweepButton)
				.addComponent(multiSweepButton)
				)
			.addGroup(layout.createParallelGroup()
				.addComponent(generationsSpinner)
				)
			.addGroup(layout.createParallelGroup()
				.addComponent(regenerateButton)
				.addComponent(multiGenerationButton)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(mutLabel)
				.addComponent(mutSpinner)
				)
			.addGroup(layout.createParallelGroup()
					.addComponent(resetButton)
					.addComponent(stopButton)
				)
			);

		// Define the controls the appear as we look top-to-bottom down the panel
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(oneSweepButton)
				.addComponent(regenerateButton)
				.addComponent(mutLabel)
				.addComponent(resetButton)
				)
			.addGroup(layout.createParallelGroup()
				.addComponent(sweepsSpinner)
				.addComponent(multiSweepButton)
				.addComponent(generationsSpinner)
				.addComponent(multiGenerationButton)
				.addComponent(mutSpinner)
				.addComponent(stopButton)
				)
			);

		// Force vertically-arranged buttons to have the same width
		layout.linkSize(SwingConstants.HORIZONTAL, oneSweepButton, multiSweepButton);
		layout.linkSize(SwingConstants.HORIZONTAL, regenerateButton, multiGenerationButton);
		layout.linkSize(SwingConstants.HORIZONTAL, resetButton, stopButton);

		// That's it! The layout manager now positions the widgets automatically
	}

	public void jobStarted() {
		// Dim all buttons and spinners
		for(Component cmp : getComponents()) {
			if(cmp.getClass() == JButton.class || cmp.getClass() == JSpinner.class || cmp.getClass() == JComboBox.class || cmp.getClass() == JCheckBox.class) cmp.setEnabled(cmp == stopButton);
		}
	}

	public void jobFinished() {
		// Re-enable all buttons and spinners
		for(Component cmp : getComponents()) {
			if(cmp.getClass() == JButton.class || cmp.getClass() == JSpinner.class || cmp.getClass() == JComboBox.class || cmp.getClass() == JCheckBox.class) cmp.setEnabled(cmp != stopButton);
		}
	}

}
