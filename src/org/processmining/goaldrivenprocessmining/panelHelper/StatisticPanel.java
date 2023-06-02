package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.processmining.goaldrivenprocessmining.algorithms.panel.PanelConstants;

public class StatisticPanel extends JPanel {
	private String title;
	private Map<String, String> contentMap;
	private JButton triggerButton;
	private JPanel contentPanel;
	private JTabbedPane tabbedPane;
	private StatisticContentPanel statisticContentPanel;
	private JPanel tab2Panel;

	public StatisticPanel() {
		setLayout(new BorderLayout());
		// Create the content panel
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(PanelConstants.STATISTIC_PANEL_BACKGROUND_COLOR);
		// Create the tabbed pane
		tabbedPane = new JTabbedPane();
//		tabbedPane.setBackground(PanelConstants.STATISTIC_PANEL_BACKGROUND_COLOR);
//		tabbedPane.setUI(new CustomTabbedPaneUI(PanelConstants.STATISTIC_PANEL_TAB_SELECTED_COLOR,
//				PanelConstants.STATISTIC_PANEL_TAB_UNSELECTED_COLOR));
		// Create the panels for each tab
		Map<String, String> keyValueMap = new HashMap<>();
        keyValueMap.put("Key 1", "Value 1");
        keyValueMap.put("Key 2", "Value 2");
        keyValueMap.put("Key 3", "Value 3");
        String headerText = "Header Text";

        // Create the panel and add it to a frame
        statisticContentPanel = new StatisticContentPanel(keyValueMap, headerText);
		tab2Panel = new JPanel();
		tab2Panel.setBackground(PanelConstants.STATISTIC_PANEL_BACKGROUND_COLOR);
		// Add the panels to the tabbed pane
		tabbedPane.addTab("Tab 1", statisticContentPanel);
		tabbedPane.addTab("Tab 2", tab2Panel);
		contentPanel.add(tabbedPane, BorderLayout.CENTER);

		// Add the content panel to the DynamicPanel, initially hidden
		add(contentPanel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Dynamic Panel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			StatisticPanel dynamicPanel = new StatisticPanel();
			frame.add(dynamicPanel);

			frame.pack();
			frame.setVisible(true);
		});
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

}
