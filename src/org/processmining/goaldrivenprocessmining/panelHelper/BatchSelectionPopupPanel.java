package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class BatchSelectionPopupPanel extends JPanel {
	
	private JButton groupNodeButton;
	private JButton ungroupNodeButton;
	private JTextField groupNameField;
	public BatchSelectionPopupPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(GoalDrivenConstants.SIDE_PANEL_CARD_COLOR);
		groupNameField = new JTextField(5);
        Dimension preferredSize = groupNameField.getPreferredSize();
        groupNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
		add(groupNameField);
		groupNodeButton = new JButton("Group");
		add(groupNodeButton);
		ungroupNodeButton = new JButton("Ungroup");
		add(ungroupNodeButton);
	}

	public JTextField getGroupNameField() {
		return groupNameField;
	}

	public void setGroupNameField(JTextField groupNameField) {
		this.groupNameField = groupNameField;
	}

	public JButton getGroupNodeButton() {
		return groupNodeButton;
	}

	public void setGroupNodeButton(JButton groupNodeButton) {
		this.groupNodeButton = groupNodeButton;
	}

	public JButton getUngroupNodeButton() {
		return ungroupNodeButton;
	}

	public void setUngroupNodeButton(JButton ungroupNodeButton) {
		this.ungroupNodeButton = ungroupNodeButton;
	}

	
	
}
