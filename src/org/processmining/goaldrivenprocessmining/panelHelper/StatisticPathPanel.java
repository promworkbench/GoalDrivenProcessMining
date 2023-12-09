package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class StatisticPathPanel extends JPanel {
	private JPanel frequencyPanel;
	private JPanel throughputPanel;
	private JButton showCaseButton;

	public StatisticPathPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);

		frequencyPanel = new JPanel();
		frequencyPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		frequencyPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		add(frequencyPanel);

		throughputPanel = new JPanel();
		throughputPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		throughputPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		add(throughputPanel);

		showCaseButton = new JButton("See cases with this path");
		add(showCaseButton);
	}

	public void updatePanel(String label, JPanel panel, Map<String, String> keyValueMap) {
		panel.removeAll();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		panel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);

		// Row 1: Label with big font and orange foreground
		JLabel labelRow = new JLabel(label);
		labelRow.setFont(GoalDrivenConstants.BOLD_M_FONT);
		labelRow.setForeground(new Color(255, 89, 0));

		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, labelRow.getPreferredSize().height + 10));
		labelPanel.setBackground(GoalDrivenConstants.BORDER_COLOR);
		labelPanel.add(labelRow);
		panel.add(labelPanel);

		// Key-Value pairs: Rows with normal font and white foreground
		for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			JPanel rowPanel = this.createKeyValueRow(key, value);
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height));
			panel.add(rowPanel);
		}
		panel.add(Box.createRigidArea(new Dimension(0, 10))); // Adjust the value to set the desired gap

	}

	private JPanel createKeyValueRow(String key, String value) {
		JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rowPanel.setBackground(GoalDrivenConstants.BORDER_COLOR);

		// Key with normal font and white foreground
		JLabel keyLabel = new JLabel(key + ":");
		keyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		keyLabel.setForeground(Color.WHITE);
		rowPanel.add(keyLabel);

		// Value with normal font and white foreground
		JLabel valueLabel = new JLabel(value);
		valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		valueLabel.setForeground(Color.WHITE);
		rowPanel.add(valueLabel);

		return rowPanel;
	}

	public JPanel getFrequencyPanel() {
		return frequencyPanel;
	}

	public void setFrequencyPanel(JPanel frequencyPanel) {
		this.frequencyPanel = frequencyPanel;
	}

	public JPanel getThroughputPanel() {
		return throughputPanel;
	}

	public void setThroughputPanel(JPanel throughputPanel) {
		this.throughputPanel = throughputPanel;
	}

	public JButton getShowCaseButton() {
		return showCaseButton;
	}

	public void setShowCaseButton(JButton showCaseButton) {
		this.showCaseButton = showCaseButton;
	}

}
