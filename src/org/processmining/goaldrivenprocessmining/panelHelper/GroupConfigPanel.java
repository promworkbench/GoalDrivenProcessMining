package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenController;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;

public class GroupConfigPanel extends JPanel {
	private final JTable groupTable;
	private final DefaultTableModel tableModel;
	private final JPanel displayPanel;
	private final JButton removeGroupButton;
	private final JButton doneButton;
	private final JButton cancelButton;

	private JButton removeActivityButton;

	public GroupConfigPanel() {
		setLayout(new BorderLayout());

		// Set up the table
		tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Make all cells not editable
			}
		};
		tableModel.setColumnIdentifiers(new String[] { "Groups" });
		groupTable = new JTable(tableModel);
		JScrollPane tableScrollPane = new JScrollPane(groupTable);

		// Set up the display panel
		displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

		// Add the "Remove Group" button to the display panel
		removeGroupButton = new JButton("Remove Group");
		removeGroupButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		removeGroupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = groupTable.getSelectedRow();
				GoalDrivenController.ungroupGroupConfigObject(((GroupActObject)tableModel.getValueAt(selectedRow, 0)).getGroupName());
				displayPanel.removeAll();
				revalidate();
				repaint();
			}
		});
		displayPanel.add(removeGroupButton);

		// Add components to the main panel using JSplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, displayPanel);
		splitPane.setDividerLocation(0.5); // Set the initial divider location to 50%
		add(splitPane, BorderLayout.CENTER);

		// Add Done and Cancel buttons at the bottom
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		doneButton = new JButton("Done");
		cancelButton = new JButton("Cancel");
		bottomPanel.add(doneButton);
		bottomPanel.add(cancelButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	//	public static void main(String[] args) {
	//		SwingUtilities.invokeLater(new Runnable() {
	//			@Override
	//			public void run() {
	//				JFrame frame = new JFrame("Group Table and Display Panel");
	//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//				frame.setSize(800, 400);
	//				frame.add(new GroupConfigPanel());
	//				frame.setLocationRelativeTo(null);
	//				frame.setVisible(true);
	//			}
	//		});
	//	}

	public JTable getGroupTable() {
		return groupTable;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public JPanel getDisplayPanel() {
		return displayPanel;
	}

	public JButton getRemoveGroupButton() {
		return removeGroupButton;
	}

	public JButton getRemoveActivityButton() {
		return removeActivityButton;
	}

	public void setRemoveActivityButton(JButton removeActivityButton) {
		this.removeActivityButton = removeActivityButton;
	}

	public JButton getDoneButton() {
		return doneButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void updateDisplayPanel(int selectedRow) {
		displayPanel.removeAll();
		removeGroupButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		displayPanel.add(removeGroupButton);
		GroupActObject group = (GroupActObject) tableModel.getValueAt(selectedRow, 0);
		JLabel groupNameLabel = new JLabel("Group Name: " + group.getGroupName());
		displayPanel.add(groupNameLabel);

		List<String> groupActivities = group.getListAct();
		for (String activity : groupActivities) {
			JPanel activityPanel = new JPanel(new BorderLayout());
			activityPanel.add(new JLabel(activity), BorderLayout.CENTER);
			JButton removeActivityButton = new JButton("Remove");
			removeActivityButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					GoalDrivenController.removeActInGroupConfigObject(group.getGroupName(), activity);
				}
				
			});
			removeActivityButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeActivity(selectedRow, activity);
				}
			});
			activityPanel.add(removeActivityButton, BorderLayout.EAST);
			displayPanel.add(activityPanel);
		}

		// Re-add the "Remove Group" button to the display panel

		displayPanel.revalidate();
		displayPanel.repaint();
	}


	//
	private void removeActivity(int groupIndex, String activity) {
		updateDisplayPanel(groupIndex);
	}
	//
	//    public void addGroup(String groupName) {
	//        groups.add(groupName);
	//        activities.add(new ArrayList<>());
	//        updateTable();
	//    }
	//
	//    public void addActivity(int groupIndex, String activity) {
	//        activities.get(groupIndex).add(activity);
	//        updateDisplayPanel(groupIndex);
	//    }

}
