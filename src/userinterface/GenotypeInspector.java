package userinterface;

import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simulation.Agent;

@SuppressWarnings("serial")
public class GenotypeInspector extends JPanel {

	private Controller controller;
	private JTextField conBox = new JTextField();
	private JTextField briBox = new JTextField();
	private JTextField motBox = new JTextField();
	private JTextField dirBox = new JTextField();
	private JTextField efficiencyBox = new JTextField();
	private JTextField foodBox = new JTextField();
	
	private JTextField boxes[] = new JTextField[]{
		conBox, briBox, motBox, dirBox, efficiencyBox, foodBox
	};
	
	private DecimalFormat roundDecimal = new DecimalFormat("0.0000");

	public GenotypeInspector(Controller c) {
		controller = c;
		JLabel conLabel = new JLabel("Contrast");
		JLabel briLabel = new JLabel("Brightness");
		JLabel motLabel = new JLabel("Motility");
		JLabel dirLabel = new JLabel("Directedness");
		JLabel efficiencyLabel = new JLabel("Efficiency");
		JLabel foodLabel = new JLabel("Food consumed");

		// Layout the widgets within the panel
		
		setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));

		// Use a group layout
		GroupLayout layout = new GroupLayout(this);		
		setLayout(layout);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(conLabel)
						.addComponent(briLabel)
						.addComponent(motLabel)
						.addComponent(dirLabel)
						.addComponent(efficiencyLabel)
						.addComponent(foodLabel)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(conBox)
						.addComponent(briBox)
						.addComponent(motBox)
						.addComponent(dirBox)
						.addComponent(efficiencyBox)
						.addComponent(foodBox)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(conLabel).addComponent(conBox)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(briLabel).addComponent(briBox)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(motLabel).addComponent(motBox)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(dirLabel).addComponent(dirBox)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(efficiencyLabel).addComponent(efficiencyBox)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(foodLabel).addComponent(foodBox)
				)
		);
		
		for(JTextField box : boxes) {
			box.setEditable(false);
		}
			
		// Fill the text fields with appropriate values
		activeAgentChanged();
		
	}

	public void activeAgentChanged() {
		Agent a = controller.getActiveAgent();
		if(a == null) {
			for(JTextField box : boxes) {
				box.setText("");
			}
		} else {
			conBox.setText(roundDecimal.format(a.getContrast()));
			briBox.setText(roundDecimal.format(a.getBrightness()));
			motBox.setText(roundDecimal.format(a.getMotility()));
			dirBox.setText(roundDecimal.format(a.getDirectedness()));
			efficiencyBox.setText(roundDecimal.format(a.getEfficiency()));
			foodBox.setText(roundDecimal.format(a.getFitness()));
		}
	}

}
