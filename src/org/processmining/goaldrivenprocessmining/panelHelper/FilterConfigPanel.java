package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class FilterConfigPanel extends JPanel {
	private final FilterEdgePanel highLevelEdgePanel;
	private final FilterEdgePanel lowLevelEdgePanel;
	private final JButton filterCloseButton;

	public FilterConfigPanel(int width) {
		setLayout(new BorderLayout());
		// Create a JTabbedPane
		JTabbedPane tabbedPane = new JTabbedPane();

		//high level
		{
			highLevelEdgePanel = new FilterEdgePanel("High level edges");
		}

		//low level
		{
			lowLevelEdgePanel = new FilterEdgePanel("Low level edges");
		}
		JPanel filterEndPanel = new JPanel();
		filterEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		filterCloseButton = this.drawButton("Close");
		filterEndPanel.add(filterCloseButton);

		// Add tabs to the JTabbedPane
		tabbedPane.addTab("High Level", highLevelEdgePanel);
		tabbedPane.addTab("Low Level", lowLevelEdgePanel);

		// Add the JTabbedPane to the center and the button to the south
		add(tabbedPane, BorderLayout.CENTER);
		add(filterEndPanel, BorderLayout.SOUTH);
	}

	public JButton drawButton(String name) {
		return new JButton(name);
	}

	public JButton getFilterCloseButton() {
		return filterCloseButton;
	}

	public FilterEdgePanel getHighLevelEdgePanel() {
		return highLevelEdgePanel;
	}

	public FilterEdgePanel getLowLevelEdgePanel() {
		return lowLevelEdgePanel;
	}

}
