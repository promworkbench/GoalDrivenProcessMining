package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

import graph.GraphConstants;

public class DisplaySettingPanel extends JPanel {

	private final JButton displaySettingDoneButton;
	private final JButton displaySettingCancelButton;
	private final JComboBox<String> modeComboBox;
	private final JComboBox<String> additionalModeComboBox;

	public DisplaySettingPanel() {
		// Set layout manager to FlowLayout
		setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Display settings");
		titleLabel.setFont(GoalDrivenConstants.BOLD_M_FONT);
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(titleLabel);
		add(titlePanel, BorderLayout.NORTH);
		// 1st row: Label "Mode" and JComboBox
		JPanel legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
		JLabel modeLabel = new JLabel("Main metric option: ");
		modeComboBox = new JComboBox<>(new String[] { GraphConstants.MODE_FREQUENCY,
				GraphConstants.MODE_MEAN_THROUGHPUT, GraphConstants.MODE_MEDIAN_THROUGHPUT,
				GraphConstants.MODE_MIN_THROUGHPUT, GraphConstants.MODE_MAX_THROUGHPUT });
		modeComboBox.setSelectedItem(GraphConstants.MODE_FREQUENCY);
		JLabel additionalModeLabel = new JLabel("Additional metric option: ");
		additionalModeComboBox = new JComboBox<>(
				new String[] { "None", GraphConstants.MODE_PRIORITY, GraphConstants.MODE_DESIRABILITY, });
		additionalModeComboBox.setSelectedItem("None");
		
		JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		modePanel.add(modeLabel);
		modePanel.add(modeComboBox);
		JPanel additionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		additionalPanel.add(additionalModeLabel);
		additionalPanel.add(additionalModeComboBox);
		
		
		legend.add(modePanel);
		legend.add(additionalPanel);

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		displaySettingCancelButton = new JButton("Cancel");
		displaySettingDoneButton = new JButton("Done");
		actEndPanel.add(displaySettingCancelButton);
		actEndPanel.add(displaySettingDoneButton);

		add(legend, BorderLayout.CENTER);
		add(actEndPanel, BorderLayout.SOUTH);
	}

	public JButton getDisplaySettingDoneButton() {
		return displaySettingDoneButton;
	}

	public JButton getDisplaySettingCancelButton() {
		return displaySettingCancelButton;
	}

	public JComboBox<String> getModeComboBox() {
		return modeComboBox;
	}

	public JComboBox<String> getAdditionalModeComboBox() {
		return additionalModeComboBox;
	}
}
