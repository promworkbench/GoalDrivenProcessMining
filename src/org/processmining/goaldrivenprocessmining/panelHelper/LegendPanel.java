package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graph.GraphConstants;

public class LegendPanel extends JPanel {

	private final JButton legendDoneButton;
	private final JButton legendCancelButton;
	private final JComboBox<String> modeComboBox;
	private final JComboBox<String> additionalModeComboBox;

	public LegendPanel() {
		// Set layout manager to FlowLayout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// 1st row: Label "Mode" and JComboBox
		JPanel legend = new JPanel();
		legend.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel modeLabel = new JLabel("Mode");
		modeComboBox = new JComboBox<>(new String[] { GraphConstants.MODE_FREQUENCY,
				GraphConstants.MODE_MEAN_THROUGHPUT, GraphConstants.MODE_MEDIAN_THROUGHPUT,
				GraphConstants.MODE_MIN_THROUGHPUT, GraphConstants.MODE_MAX_THROUGHPUT, });
		modeComboBox.setSelectedItem(GraphConstants.MODE_FREQUENCY);
		JLabel additionalModeLabel = new JLabel("Additioinal display");
		additionalModeComboBox = new JComboBox<>(
				new String[] { "None", GraphConstants.MODE_PRIORITY, GraphConstants.MODE_DESIRABILITY, });
		additionalModeComboBox.setSelectedItem("None");
		legend.add(modeLabel);
		legend.add(modeComboBox);
		legend.add(additionalModeLabel);
		legend.add(additionalModeComboBox);

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		legendCancelButton = new JButton("Cancel");
		legendDoneButton = new JButton("Done");
		actEndPanel.add(legendCancelButton);
		actEndPanel.add(legendDoneButton);

		add(legend);
		add(actEndPanel);
	}

	public JButton getLegendDoneButton() {
		return legendDoneButton;
	}

	public JButton getLegendCancelButton() {
		return legendCancelButton;
	}

	public JComboBox<String> getModeComboBox() {
		return modeComboBox;
	}

	public JComboBox<String> getAdditionalModeComboBox() {
		return additionalModeComboBox;
	}
}
