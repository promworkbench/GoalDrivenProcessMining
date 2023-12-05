package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class SidePanel extends JPanel {
	private final StatisticPanel statisticPanel;

	public SidePanel() {
		setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		setLayout(new BorderLayout());
		statisticPanel = new StatisticPanel(null, null);
		add(statisticPanel);
	}

	public StatisticPanel getStatisticPanel() {
		return statisticPanel;
	}

}
