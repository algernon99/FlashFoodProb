package userinterface;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class StatePanel extends JPanel {
	
	private JButton refreshButton = new JButton("Refresh");
	private AgentInfoTable infoTable;
	private GenotypeInspector geneInspector;
	
	public StatePanel(Controller c) {

		infoTable = new AgentInfoTable(c);
		geneInspector = new GenotypeInspector(c);

		// Bind buttons to actions
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { infoTable.updateTable(); }
		});

		// Lay out the components
		refreshButton.setEnabled(false);
		setLayout(new BorderLayout());
		add(refreshButton, BorderLayout.PAGE_START);
		add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoTable, geneInspector));
	}
	
	public void jobStarted() {
		infoTable.clearSelection();
		refreshButton.setEnabled(true);
	}

	public void jobFinished() {
		refreshButton.setEnabled(false);
		infoTable.updateTable();
	}

	public void worldUpdated() {
		infoTable.updateTable();
	}
	
	public void activeAgentChanged() {
		geneInspector.activeAgentChanged();
	}
	
}
