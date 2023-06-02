package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.processmining.goaldrivenprocessmining.algorithms.panel.PanelConstants;

public class SidePanel extends JPanel {
	private final BatchSelectionPopupPanel batchSelectionPopupPanel;
	private final StatisticPanel statisticPanel;
	public SidePanel() {
		setBackground(PanelConstants.CONTENT_CARD_COLOR);
		setLayout(new BorderLayout());
		statisticPanel = new StatisticPanel();
		batchSelectionPopupPanel = new BatchSelectionPopupPanel();
		JSplitPane panelC = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statisticPanel, batchSelectionPopupPanel);
        panelC.setResizeWeight(0.75);
        panelC.setEnabled(false);
        int dividerSize = panelC.getDividerSize();
        int totalHeight = panelC.getHeight();
        int topPanelHeight = (int) (totalHeight * 0.75) - dividerSize;
        statisticPanel.setPreferredSize(new Dimension(0, topPanelHeight));
        add(panelC);
//		JPanel panel = new JPanel();
//		panel.setBackground(Color.RED);
//		add(panel, BorderLayout.CENTER);
	}
	public BatchSelectionPopupPanel getBatchSelectionPopupPanel() {
		return batchSelectionPopupPanel;
	}
//	public StatisticPanel getStatisticPanel() {
//		return statisticPanel;
//	}
	
	
}
