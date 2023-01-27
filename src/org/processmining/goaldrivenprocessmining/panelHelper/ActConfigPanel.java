package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapValueGroupObject;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ActConfigPanel extends JPanel {
	private final JButton actConfigCancelButton;
	private final JButton actConfigDoneButton;
	private final JButton actConfigNewGroupButton;
	private final JComboBox<GroupActObject> groupComboBox;
	private final AttributeClassifier[] allUniqueValues;
	private final JScrollPane allGroupPane;
	private final JPanel allGroupPanel;
	private final JTable actConfigTable;
	private int allGroupIndex;
	private GridBagConstraints c;
	private MapValueGroupObject mapActGroup;

	public ActConfigPanel(int width) {
		double actConfigSize[][] = { { 0.6 * width, 0.4 * width },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM } };
		setLayout(new TableLayout(actConfigSize));
		this.mapActGroup = new MapValueGroupObject();
		// new group button
		JPanel actStartPanel = new JPanel();
		actStartPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		actConfigNewGroupButton = this.drawButton("New group");
		actStartPanel.add(actConfigNewGroupButton);
		add(actStartPanel, "0,0,1,0");
		// table
		groupComboBox = new JComboBox<GroupActObject>();
		allUniqueValues = new AttributeClassifier[0];
		actConfigTable = this.drawActConfigTable(allUniqueValues);
		this.setUpGroupColumn(actConfigTable, actConfigTable.getColumnModel().getColumn(1), groupComboBox);
		JScrollPane scrollPane = new JScrollPane(actConfigTable);
		add(scrollPane, "0, 1");
		// all group pane
		initializeConstraint();
		allGroupPanel = new JPanel();
		
		allGroupPanel.setLayout(new GridBagLayout());
		allGroupPane = new JScrollPane(allGroupPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(allGroupPane, "1, 1");
		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		actConfigCancelButton = this.drawButton("Cancel");
		actConfigDoneButton = this.drawButton("Done");
		actEndPanel.add(actConfigCancelButton);
		actEndPanel.add(actConfigDoneButton);
		add(actEndPanel, "0,2,1,2");
	}
	private void initializeConstraint() {
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		allGroupIndex = 0;
		c.gridx = 0;
		c.gridy = allGroupIndex;
	}
	
	public GroupActObject[] getAllGroupAct() {
		GroupActObject[] res = new GroupActObject[allGroupPanel.getComponentCount()];
		for (int i = 0; i < res.length; i++) {
			GroupActConfig t = (GroupActConfig) allGroupPanel.getComponent(i);
			res[i] = new GroupActObject(t.getGroupName(), t.getGroupColor());
		}
		
		return res;
	}
	
	public void addNextGroup(JPanel p) {
		allGroupIndex += 1;
		c.gridy = allGroupIndex;
		allGroupPanel.add(p, c);
	}

	private JButton drawButton(String name) {
		return new JButton(name);
	}

	private JTable drawActConfigTable(AttributeClassifier[] acts) {
		String[] columnNames = { "Activities", "Group" };
		if (acts.length == 0) {
			return new JTable(new DefaultTableModel(null, columnNames));
		}
		Object[][] data = new Object[acts.length][2];
		for (int i = 0; i < acts.length; i++) {
			data[i][0] = acts[i];
			data[i][1] = new GroupActObject("", Color.WHITE);
		}
		return new JTable(new DefaultTableModel(data, columnNames));
	}

	private void setUpGroupColumn(JTable table, TableColumn groupColumn, JComboBox<GroupActObject> groupComboBox) {
		//Set up the editor for the sport cells.
		groupColumn.setCellEditor(new DefaultCellEditor(groupComboBox));

		//Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		groupColumn.setCellRenderer(renderer);
	}
//	public static void main(String[] args) {
//		Color c = Color.WHITE;
//		System.out.println(String.valueOf(c.getRGB()));
//		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
//		System.out.println(hex);
//	}

	public JButton getActConfigCancelButton() {
		return actConfigCancelButton;
	}

	public JButton getActConfigDoneButton() {
		return actConfigDoneButton;
	}

	public JButton getActConfigNewGroupButton() {
		return actConfigNewGroupButton;
	}

	public JComboBox<GroupActObject> getGroupComboBox() {
		return groupComboBox;
	}

	public JTable getActConfigTable() {
		return actConfigTable;
	}

	public JScrollPane getAllGroupPane() {
		return allGroupPane;
	}

	public JPanel getAllGroupPanel() {
		return allGroupPanel;
	}
	public MapValueGroupObject getMapActGroup() {
		return mapActGroup;
	}
	

}
