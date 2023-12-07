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
	private StatisticPathPanel statisticPathPanel;

	public StatisticPanel(String act, EdgeObject edgeObject) {
		this.act = act;
		this.edgeObject = edgeObject;

		setLayout(new BorderLayout());
		setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
		setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
		JLabel statisticLabel = new JLabel("Details");
		statisticLabel.setForeground(Color.WHITE);
		statisticLabel.setFont(GoalDrivenConstants.BOLD_XL_FONT);
		add(statisticLabel, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
		contentPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);

		statisticPathPanel = new StatisticPathPanel();
		statisticActivityPanel = new StatisticActivityPanel();
		
		add(contentPanel, BorderLayout.CENTER);
	}

	public void createStatisticPanelForActivity(String act, Map<String, String> frequencyMap,
			List<Object[]> waitingActData, List<Object[]> leadingActData) {
		this.act = act;
		//reset
		this.resetStatisticPanel();
		// Row 1: label
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(GoalDrivenConstants.BOLD_L_FONT);
		labelPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		labelPanel.add(label);
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
		label.setText("Activity: " + act);
		this.contentPanel.add(labelPanel, BorderLayout.NORTH);

		// Row 2: stat panel
		this.statisticActivityPanel = new StatisticActivityPanel();
		this.statisticActivityPanel.updateFrequencyPanel(frequencyMap);
		this.statisticActivityPanel.updateThroughputPanel(waitingActData, leadingActData);
		this.statisticActivityPanel
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, statisticActivityPanel.getPreferredSize().height));
		this.contentPanel.add(this.statisticActivityPanel, BorderLayout.CENTER);
	}

	public void createStatisticPanelForPath(EdgeObject edgeObject, Map<String, String> frequencyMap,
			Map<String, String> throughputMap) {
		this.edgeObject = edgeObject;
		//reset
		this.resetStatisticPanel();
		// Row 1: label
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(GoalDrivenConstants.BOLD_L_FONT);
		labelPanel.setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		labelPanel.add(label);
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
		label.setText("Path: " + edgeObject.getNode1() + " \u2192 " + edgeObject.getNode2());
		this.contentPanel.add(labelPanel, BorderLayout.NORTH);

		// Row 2: stat panel
		this.statisticPathPanel = new StatisticPathPanel();
		this.statisticPathPanel.updatePanel("Frequency", this.statisticPathPanel.getFrequencyPanel(), frequencyMap);
		this.statisticPathPanel.updatePanel("Performance", this.statisticPathPanel.getThroughputPanel(), throughputMap);
		this.statisticPathPanel
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, statisticPathPanel.getPreferredSize().height));
		this.contentPanel.add(this.statisticPathPanel, BorderLayout.CENTER);
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

	public StatisticPathPanel getStatisticPathPanel() {
		return statisticPathPanel;
	}

	public void setStatisticPathPanel(StatisticPathPanel statisticPathPanel) {
		this.statisticPathPanel = statisticPathPanel;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public EdgeObject getEdgeObject() {
		return edgeObject;
	}

	public void setEdgeObject(EdgeObject edgeObject) {
		this.edgeObject = edgeObject;
	}

}
