package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.processmining.goaldrivenprocessmining.algorithms.ButtonColumn;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ActDisplayPanel extends JPanel {
	private final JButton actCancelButton;
	private final JButton actDoneButton;
	private final JTable includeTable;
	private final JTable excludeTable;
	private HashMap<AttributeClassifier, String> mapTableAttribute;

	
	
	public ActDisplayPanel(int width) {
		this.mapTableAttribute = new HashMap<>();
		double actConfigSize[][] = { { 0.5*width, 0.5*width },
				{ TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM } };
		setLayout(new TableLayout(actConfigSize));
		
		
		includeTable = this.drawConfigTable(new AttributeClassifier[0], "Exclude");
		excludeTable = this.drawConfigTable(new AttributeClassifier[0], "Include");
		Action excludeAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());
				Object[] data = new Object[] { table.getValueAt(modelRow, 0), "Include" };
				((DefaultTableModel) excludeTable.getModel()).addRow(data);
				((DefaultTableModel) table.getModel()).removeRow(modelRow);

			}
		};
		Action includeAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());
				Object[] data = new Object[] { table.getValueAt(modelRow, 0), "Exclude" };
				((DefaultTableModel) includeTable.getModel()).addRow(data);
				((DefaultTableModel) table.getModel()).removeRow(modelRow);

			}
		};
		new ButtonColumn(includeTable, excludeAct, 1);
		new ButtonColumn(excludeTable, includeAct, 1);
		
		JScrollPane includeScrollPane = new JScrollPane(includeTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane excludeScrollPane = new JScrollPane(excludeTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		actCancelButton = this.drawButton("Cancel");
		actDoneButton = this.drawButton("Done");
		actEndPanel.add(actCancelButton);
		actEndPanel.add(actDoneButton);
		
		add(includeScrollPane, "0,0");
		add(excludeScrollPane, "1,0");
		add(actEndPanel, "0,1,1,1");
	}
	
	public JButton drawButton(String name) {
		return new JButton(name);
	}
	public JTable drawConfigTable(AttributeClassifier[] acts, String type) {
		String[] columnNames = { "Activities", "" };
		if (acts.length == 0) {
			return new JTable(new DefaultTableModel(null, columnNames));
		}
		Object[][] data = new Object[acts.length][2];
		for (int i = 0; i < acts.length; i++) {
			data[i][0] = acts[i];
			data[i][1] = type;
		}
		return new JTable(new DefaultTableModel(data, columnNames));
	}
	public void updateConfigTable(JTable table, AttributeClassifier[] acts, String type) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
		for (int i = 0; i < acts.length; i++) {
			model.addRow(new Object[] { acts[i], type });
		}
	}
	public JButton getActCancelButton() {
		return actCancelButton;
	}
	public JButton getActDoneButton() {
		return actDoneButton;
	}
	public JTable getIncludeTable() {
		return includeTable;
	}
	public JTable getExcludeTable() {
		return excludeTable;
	}

	public HashMap<AttributeClassifier, String> getMapTableAttribute() {
		return mapTableAttribute;
	}

	
}
