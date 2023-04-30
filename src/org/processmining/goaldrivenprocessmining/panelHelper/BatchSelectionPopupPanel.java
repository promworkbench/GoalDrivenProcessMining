package org.processmining.goaldrivenprocessmining.panelHelper;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.panel.PanelConstants;

public class BatchSelectionPopupPanel extends JPanel {
	
	private JButton groupNodeButton;
	private JButton ungroupNodeButton;
	
	public BatchSelectionPopupPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(PanelConstants.SIDE_PANEL_CARD_COLOR);
		
		groupNodeButton = new JButton("Group");
		add(groupNodeButton);
		ungroupNodeButton = new JButton("Ungroup");
		add(ungroupNodeButton);
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
