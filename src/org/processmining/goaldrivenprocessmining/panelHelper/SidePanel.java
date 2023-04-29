package org.processmining.goaldrivenprocessmining.panelHelper;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class SidePanel extends JPanel {
	private final BatchSelectionPopupPanel batchSelectionPopupPanel;
	public SidePanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		batchSelectionPopupPanel = new BatchSelectionPopupPanel();
		add(batchSelectionPopupPanel);
		

	}
	public BatchSelectionPopupPanel getBatchSelectionPopupPanel() {
		return batchSelectionPopupPanel;
	}
	
}
