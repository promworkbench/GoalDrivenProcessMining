package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

import graph.GraphConstants;

public class DisplaySettingPanel extends JPanel {

	private final JComboBox<String> modeComboBox;
	private final JComboBox<String> additionalModeComboBox;
	private final JComboBox<String> caseModeComboBox;

	public DisplaySettingPanel() {
		// Set layout manager to FlowLayout
		setLayout(new FlowLayout(FlowLayout.CENTER));
		// 1st row: Label "Mode" and JComboBox
		JLabel modeLabel = new JLabel("Main metric option: ");
		modeLabel.setFont(GoalDrivenConstants.PLAIN_S_FONT);
		modeLabel.setForeground(Color.WHITE);
		modeComboBox = new JComboBox<>(new String[] { GraphConstants.MODE_FREQUENCY,
				GraphConstants.MODE_MEAN_THROUGHPUT, GraphConstants.MODE_MEDIAN_THROUGHPUT,
				GraphConstants.MODE_MIN_THROUGHPUT, GraphConstants.MODE_MAX_THROUGHPUT });
		modeComboBox.setSelectedItem(GraphConstants.MODE_FREQUENCY);
		// additional metric
		JLabel additionalModeLabel = new JLabel("Additional metric option: ");
		additionalModeLabel.setFont(GoalDrivenConstants.PLAIN_S_FONT);
		additionalModeLabel.setForeground(Color.WHITE);
		additionalModeComboBox = new JComboBox<>(
				new String[] { "None", GraphConstants.MODE_PRIORITY, GraphConstants.MODE_DESIRABILITY, });
		additionalModeComboBox.setSelectedItem("None");
		// case case option
		JLabel caseMode = new JLabel("Case mode: ");
		caseMode.setFont(GoalDrivenConstants.PLAIN_S_FONT);
		caseMode.setForeground(Color.WHITE);
		caseModeComboBox = new JComboBox<>(new String[] { "None", GraphConstants.CASE_GOOD, GraphConstants.CASE_BAD });
		caseModeComboBox.setSelectedItem("None");

		JPanel compoundPanel = new JPanel();
		compoundPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
		compoundPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		compoundPanel.add(modeLabel);
		compoundPanel.add(modeComboBox);
		compoundPanel.add(additionalModeLabel);
		compoundPanel.add(additionalModeComboBox);
		compoundPanel.add(caseMode);
		compoundPanel.add(caseModeComboBox);

		add(compoundPanel);

	}

	public JComboBox<String> getModeComboBox() {
		return modeComboBox;
	}

	public JComboBox<String> getAdditionalModeComboBox() {
		return additionalModeComboBox;
	}

	public JComboBox<String> getCaseModeComboBox() {
		return caseModeComboBox;
	}

}
