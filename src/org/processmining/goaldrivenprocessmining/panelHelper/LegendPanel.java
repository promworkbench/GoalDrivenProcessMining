package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LegendPanel extends JPanel {

	private final JButton doneButton;
	private final JButton cancelButton;

	public LegendPanel() {
		setLayout(new BorderLayout());

		// Legend panel
		JPanel legendPanel = new JPanel();
		legendPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2 rows, 2 columns, with gaps

		// First Row
		JLabel nodeColorLabel = new JLabel("Node color");
		JComboBox<String> nodeColorComboBox = new JComboBox<>(new String[] { "Frequency" });

		// Second Row
		JLabel edgeWidthLabel = new JLabel("Edge width");
		JComboBox<String> edgeWidthComboBox = new JComboBox<>(new String[] { "Frequency" });

		// Add components to the panel
		legendPanel.add(nodeColorLabel);
		legendPanel.add(nodeColorComboBox);
		legendPanel.add(edgeWidthLabel);
		legendPanel.add(edgeWidthComboBox);
		add(legendPanel, BorderLayout.CENTER);
		// Add Done and Cancel buttons at the bottom
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		doneButton = new JButton("Done");
		cancelButton = new JButton("Cancel");
		bottomPanel.add(doneButton);
		bottomPanel.add(cancelButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public JButton getDoneButton() {
		return doneButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

}
