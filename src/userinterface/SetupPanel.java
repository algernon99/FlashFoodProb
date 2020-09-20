package userinterface;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import simulation.World;

@SuppressWarnings("serial")
public class SetupPanel extends JPanel {
	
	
	// Some defaults
	private static final int DEFAULT_SIZE_X = 51;
	private static final int DEFAULT_SIZE_Y = 51;
	private static final int DEFAULT_FOOD_SITES = 10;
	private static final int DEFAULT_AGENTS = 200;
	
	private static final boolean DEFAULT_CONTRAST_IC = false;
	private static final boolean DEFAULT_CONTRAST_LOCKED = false;
	private static final double DEFAULT_CONTRAST_COST = 0.25;

	private static final boolean DEFAULT_BRIGHTNESS_IC = false;
	private static final boolean DEFAULT_BRIGHTNESS_LOCKED = false;
	private static final double DEFAULT_BRIGHTNESS_COST = 0.25;
	
	private static final boolean DEFAULT_MOTILITY_IC = false;
	private static final boolean DEFAULT_MOTILITY_LOCKED = false;
	private static final double DEFAULT_MOTILITY_COST = 0.25;
	
	private static final boolean DEFAULT_DIRECTEDNESS_IC = false;
	private static final boolean DEFAULT_DIRECTEDNESS_LOCKED = false;
	private static final double DEFAULT_DIRECTEDNESS_COST = 0.25;
	
	private JSpinner  sizexSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_SIZE_X, 1, null, 1));
	private JSpinner  sizeySpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_SIZE_Y, 1, null, 1));
	private JSpinner  foodSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_FOOD_SITES, 0, null, 1));
	private JSpinner  agnSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_AGENTS, 1, null, 1));

	// The four behaviours
	private JCheckBox contrastInitial = new JCheckBox("Active", DEFAULT_CONTRAST_IC);
	private JCheckBox contrastLock = new JCheckBox("Locked", DEFAULT_CONTRAST_LOCKED);
	private JSpinner  contrastCostSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_CONTRAST_COST, 0.0, null, 0.1));
	
	private JCheckBox brightnessInitial = new JCheckBox("Active", DEFAULT_BRIGHTNESS_IC);
	private JCheckBox brightnessLock = new JCheckBox("Locked", DEFAULT_BRIGHTNESS_LOCKED);
	private JSpinner  brightnessCostSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_BRIGHTNESS_COST, 0.0, null, 0.1));

	private JCheckBox motilityInitial = new JCheckBox("Active", DEFAULT_MOTILITY_IC);
	private JCheckBox motilityLock = new JCheckBox("Locked", DEFAULT_MOTILITY_LOCKED);
	private JSpinner  motilityCostSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_MOTILITY_COST, 0.0, null, 0.1));
	
	private JCheckBox directednessInitial = new JCheckBox("Active", DEFAULT_DIRECTEDNESS_IC);
	private JCheckBox directednessLock = new JCheckBox("Locked", DEFAULT_DIRECTEDNESS_LOCKED);
	private JSpinner  directednessCostSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_DIRECTEDNESS_COST, 0.0, null, 0.1));

	
	public SetupPanel() {
		
		// Create subpanels for the different bits
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// General panel
		JPanel generalPanel = new JPanel();
		GroupLayout generalLayout = new GroupLayout(generalPanel);
		generalPanel.setLayout(generalLayout);
		generalPanel.setBorder(BorderFactory.createTitledBorder("World"));
							
		JLabel sizexLabel = new JLabel("X size");
		JLabel sizeyLabel = new JLabel("Y size");
		JLabel foodLabel = new JLabel("Sites with food");
		JLabel agnLabel = new JLabel("Number of agents");

		// Define the controls that appear as we look left-to-right across the panel
		generalLayout.setHorizontalGroup(generalLayout.createSequentialGroup()
			.addGroup(generalLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(sizexLabel)
				.addComponent(sizeyLabel)
				.addComponent(foodLabel)
				.addComponent(agnLabel)
				)
			.addGroup(generalLayout.createParallelGroup()
				.addComponent(sizexSpinner)
				.addComponent(sizeySpinner)
				.addComponent(foodSpinner)
				.addComponent(agnSpinner)
				)
			);
		
		// Define the controls the appear as we look top-to-bottom down the panel
		generalLayout.setVerticalGroup(generalLayout.createSequentialGroup()
			.addGroup(generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(sizexLabel)
				.addComponent(sizexSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
			.addGroup(generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(sizeyLabel)
				.addComponent(sizeySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
			.addGroup(generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(foodLabel)
						.addComponent(foodSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
			.addGroup(generalLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(agnLabel)
						.addComponent(agnSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
			);

		// Initial strategy grid
		JPanel strategyPanel = new JPanel();
		GroupLayout strategyLayout = new GroupLayout(strategyPanel);
		strategyPanel.setLayout(strategyLayout);
		strategyPanel.setBorder(BorderFactory.createTitledBorder("Initial strategy"));
		
		JLabel costLabel = new JLabel("Cost");
		JLabel brightnessLabel = new JLabel("Brightness");
		JLabel contrastLabel = new JLabel("Constrast");
		JLabel motilityLabel = new JLabel("Motility");
		JLabel directednessLabel = new JLabel("Directedness");
		
		// Define the columns of the layout
		strategyLayout.setHorizontalGroup(strategyLayout.createSequentialGroup()
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(brightnessLabel)
						.addComponent(contrastLabel)
						.addComponent(motilityLabel)
						.addComponent(directednessLabel)
						)
				.addGroup(strategyLayout.createParallelGroup()
						.addComponent(brightnessInitial)
						.addComponent(contrastInitial)
						.addComponent(motilityInitial)
						.addComponent(directednessInitial)
						)
				.addGroup(strategyLayout.createParallelGroup()
						.addComponent(brightnessLock)
						.addComponent(contrastLock)
						.addComponent(motilityLock)
						.addComponent(directednessLock)
						)
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(costLabel)
						.addComponent(brightnessCostSpinner)
						.addComponent(contrastCostSpinner)
						.addComponent(motilityCostSpinner)
						.addComponent(directednessCostSpinner)
						)
				);
		
		// Define the rows of the layout
		strategyLayout.setVerticalGroup(strategyLayout.createSequentialGroup()
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(costLabel)
						)
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(brightnessLabel)
						.addComponent(brightnessInitial)
						.addComponent(brightnessLock)
						.addComponent(brightnessCostSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(contrastLabel)
						.addComponent(contrastInitial)
						.addComponent(contrastLock)
						.addComponent(contrastCostSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(motilityLabel)
						.addComponent(motilityInitial)
						.addComponent(motilityLock)
						.addComponent(motilityCostSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(strategyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(directednessLabel)
						.addComponent(directednessInitial)
						.addComponent(directednessLock)
						.addComponent(directednessCostSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				);
		
		add(generalPanel);
		add(strategyPanel);
		
	}

	/**
	 * Set the state of the world passed to this method to match the values in the setup boxes
	 * 
	 * @param world world to configure
	 */
	public void configureWorld(World world) {

		world.setSize((Integer) sizexSpinner.getValue(), (Integer) sizeySpinner.getValue());
		world.assignFood((Integer) foodSpinner.getValue());
		world.assignAgents((Integer) agnSpinner.getValue(), 
				brightnessInitial.isSelected() ? 1.0 : 0.0,
				contrastInitial.isSelected() ? 1.0 : 0.0,
				motilityInitial.isSelected() ? 1.0 : 0.0,
				directednessInitial.isSelected() ? 1.0 : 0.0 );

		// Set other attributes of the world
		world.setContrastWeight((Double)contrastCostSpinner.getValue());
		world.setContrastMutability(!contrastLock.isSelected());

		world.setBrightnessWeight((Double)brightnessCostSpinner.getValue());
		world.setBrightnessMutability(!brightnessLock.isSelected());

		world.setMotilityWeight((Double)motilityCostSpinner.getValue());
		world.setMotilityMutability(!motilityLock.isSelected());

		world.setDirectednessWeight((Double)directednessCostSpinner.getValue());
		world.setDirectednessMutability(!directednessLock.isSelected());
		
	}
}
