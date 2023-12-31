package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class FilterConfigPanel extends JPanel {
	private final FilterPathPanel highLevelEdgePanel;
	private final FilterPathPanel lowLevelEdgePanel;
	private final JButton filterCloseButton;

	public FilterConfigPanel(int width) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// title
		JLabel titleLabel = new JLabel("Filter paths");
		titleLabel.setFont(GoalDrivenConstants.BOLD_M_FONT);
		add(titleLabel);
		// Create a JTabbedPane
		JTabbedPane tabbedPane = new JTabbedPane();

		//high level
		{
			highLevelEdgePanel = new FilterPathPanel();
			highLevelEdgePanel.getSaveFilterEdgeConfigurationButton().setVisible(false);
		}

		//low level
		{
			lowLevelEdgePanel = new FilterPathPanel();
		}
		JPanel filterEndPanel = new JPanel();
		filterEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		filterCloseButton = this.drawButton("Close");
		filterEndPanel.add(filterCloseButton);

		// Add tabs to the JTabbedPane
		tabbedPane.addTab("High Level", highLevelEdgePanel);
		tabbedPane.addTab("Low Level", lowLevelEdgePanel);

		// Add the JTabbedPane to the center and the button to the south
		add(tabbedPane);
		add(filterEndPanel);
	}

	public JButton drawButton(String name) {
		return new JButton(name);
	}

	public JButton getFilterCloseButton() {
		return filterCloseButton;
	}

	public FilterPathPanel getHighLevelEdgePanel() {
		return highLevelEdgePanel;
	}

	public FilterPathPanel getLowLevelEdgePanel() {
		return lowLevelEdgePanel;
	}

}
