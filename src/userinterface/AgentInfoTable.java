package userinterface;

import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import simulation.Agent;

@SuppressWarnings("serial")
public class AgentInfoTable extends JScrollPane {
	
	private Controller controller;
	private static final String[] colNames = { "Agt", "Sps", "B", "C", "M", "D", "Yum", "Lit" };
	private static final Class<?>[] colTypes = { Integer.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Boolean.class, Boolean.class };
	private Agent[] agents = new Agent[0];
	
	private class RoundedDoubleRenderer extends DefaultTableCellRenderer {
		private DecimalFormat roundToTwoDP = new DecimalFormat("0.00");
		
		@Override
		public void setValue(Object value) {
			setText(value == null ? "" : roundToTwoDP.format(value));
		}
		
	}
	
	private class AgentTableModel extends AbstractTableModel {
		
		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}
		
		@Override
		public Class<?> getColumnClass(int column) {
			return colTypes[column];
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public int getRowCount() {
			return agents.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Agent a = agents[rowIndex];
			switch(columnIndex) {
			case 0:
				return rowIndex;
			case 1:
				return a.getSpeciesId();
			case 2:
				return a.getBrightness();
			case 3:
				return a.getContrast();
			case 4:
				return a.getMotility();
			case 5:
				return a.getDirectedness();
			case 6:
				return a.isOnFood();
			case 7:
				return a.isLightOn();
			}
			return null;
		}
		
	}

	AgentTableModel tableModel = new AgentTableModel();
	JTable agentTable = new JTable(tableModel);

	public AgentInfoTable(Controller c) {
		controller = c;
		agents = c.getAgentsStable();
		
		// Display of numbers
		agentTable.setDefaultRenderer(Double.class, new RoundedDoubleRenderer());
	
		// Selections
		agentTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		agentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(lsm.isSelectionEmpty()) controller.setActiveAgent(null);
				else {
					Agent activeAgent = agents[agentTable.convertRowIndexToModel(lsm.getLeadSelectionIndex())];
					controller.setActiveAgent(activeAgent);
				}
			}
		});
		
		// Sorting
		agentTable.setAutoCreateRowSorter(true);

		//Scrolling 
		agentTable.setPreferredScrollableViewportSize(new Dimension(350,150));

		// Embed in the scrollable region
		setViewportView(agentTable);
	}
	
	public void updateTable() {
		agents = controller.getAgentsStable();
		tableModel.fireTableDataChanged();
	}
	
	public void clearSelection() {
		agentTable.getSelectionModel().clearSelection();
		controller.setActiveAgent(null);
	}
}
