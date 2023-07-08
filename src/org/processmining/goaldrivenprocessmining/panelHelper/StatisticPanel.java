package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class StatisticPanel extends JPanel {
	private String title;
	private Map<String, String> contentMap;
	private JPanel contentPanel;
	private CloseableTabbedPane statPane;
	private StatisticContentPanel statisticContentPanel;

	public StatisticPanel() {
		setLayout(new BorderLayout());
		// Create the content panel
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(GoalDrivenConstants.STATISTIC_PANEL_BACKGROUND_COLOR);
		// Create the tabbed pane
		statPane = new CloseableTabbedPane();
		

        // Create the panel and add it to a frame
        statisticContentPanel = new StatisticContentPanel(new HashMap<>(), "");
		// Add the panels to the tabbed pane
		statPane.addTab("Statistic", statisticContentPanel);
//		statPane.addTab("Tab 2", tab2Panel);
		contentPanel.add(statPane, BorderLayout.CENTER);

		// Add the content panel to the DynamicPanel, initially hidden
		add(contentPanel, BorderLayout.CENTER);
	}
	
	public void updateStatistics() {
		StatisticContentPanel statisticContentPanel = new StatisticContentPanel(this.contentMap, this.title);
		this.getStatPane().setComponentAt(0, statisticContentPanel);
		revalidate();
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getContentMap() {
		return contentMap;
	}

	public void setContentMap(Map<String, String> contentMap) {
		this.contentMap = contentMap;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public void setContentPanel(JPanel contentPanel) {
		this.contentPanel = contentPanel;
	}

	public CloseableTabbedPane getStatPane() {
		return statPane;
	}

	public void setStatPane(CloseableTabbedPane statPane) {
		this.statPane = statPane;
	}
	

}
