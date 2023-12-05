package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

public class StatisticPanel extends JPanel {

	private String act;
	private JPanel contentPanel;
	private EdgeObject edgeObject;
	private StatisticActivityPanel statisticActivityPanel;

	public StatisticPanel(String act, EdgeObject edgeObject) {
		this.act = act;
		this.edgeObject = edgeObject;

		setLayout(new BorderLayout());
		setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
		setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
		JLabel statisticLabel = new JLabel("Statistic");
		statisticLabel.setForeground(Color.WHITE);
		statisticLabel.setFont(GoalDrivenConstants.BOLD_XL_FONT);
		add(statisticLabel, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
		contentPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);

		// Row 1: label
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(GoalDrivenConstants.BOLD_L_FONT);
		labelPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		labelPanel.add(label);
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
		if (this.act != null) {
			label.setText("Activity: " + this.act);
		} else if (this.edgeObject != null) {
			label.setText("Path: " + this.edgeObject.getNode1() + " \u2192 " + this.edgeObject.getNode2());
		}
		contentPanel.add(labelPanel, BorderLayout.NORTH);

		// Row 2: stat panel
		statisticActivityPanel = new StatisticActivityPanel();
		statisticActivityPanel
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, statisticActivityPanel.getPreferredSize().height));
		contentPanel.add(statisticActivityPanel, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}

	public void createStatisticPanelForActivity(String act, Map<String, String> frequencyMap,
			List<Object[]> waitingActData, List<Object[]> leadingActData) {
		//reset
		this.resetStatisticPanel();
		this.act = act;
		// Row 1: label
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(GoalDrivenConstants.BOLD_L_FONT);
		labelPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		labelPanel.add(label);
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
		label.setText("Activity: " + this.act);
		this.contentPanel.add(labelPanel, BorderLayout.NORTH);

		// Row 2: stat panel
		this.statisticActivityPanel = new StatisticActivityPanel();
		this.statisticActivityPanel.updateFrequencyPanel(frequencyMap);
		this.statisticActivityPanel.updateThroughputPanel(waitingActData, leadingActData);
		this.statisticActivityPanel
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, statisticActivityPanel.getPreferredSize().height));
		this.contentPanel.add(this.statisticActivityPanel, BorderLayout.CENTER);
	}

	public void resetStatisticPanel() {
		Component[] components = this.contentPanel.getComponents();
		for (Component component : components) {
			this.contentPanel.remove(component);
		}

		this.contentPanel.revalidate();
		this.contentPanel.repaint();
	}

	public StatisticActivityPanel getStatisticActivityPanel() {
		return statisticActivityPanel;
	}

	public void setStatisticActivityPanel(StatisticActivityPanel statisticActivityPanel) {
		this.statisticActivityPanel = statisticActivityPanel;
	}

}
