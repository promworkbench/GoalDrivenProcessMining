package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.processmining.goaldrivenprocessmining.algorithms.panel.PanelConstants;

public class StatisticContentPanel extends JPanel {

	public StatisticContentPanel(Map<String, String> keyValueMap, String headerText) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(PanelConstants.STATISTIC_PANEL_BACKGROUND_COLOR);
		setBorder(BorderFactory.createLineBorder(Color.RED));
		// Create and add the header label
		add(Box.createVerticalStrut(10));
		JLabel headerLabel = new JLabel(headerText);
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(headerLabel);
		
		int spacing = 10;
        add(Box.createVerticalStrut(20));
        JPanel childPanel = new JPanel();
        childPanel.setLayout(new GridLayout(0, 1));
		// Add a JPanel for each key-value pair
		for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
			JPanel entryPanel = new JPanel();
			entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));

			JLabel keyLabel = new JLabel(entry.getKey());
			keyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			JLabel valueLabel = new JLabel(entry.getValue());
			 // Set background color to RGB(23, 157, 247)
            entryPanel.setBackground(new Color(23, 157, 247));

            // Set foreground color to white
            keyLabel.setForeground(Color.WHITE);
            valueLabel.setForeground(Color.WHITE);

            // Set black border
            entryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            entryPanel.add(keyLabel);
			entryPanel.add(valueLabel);

			childPanel.add(entryPanel);
			
			// Add spacing between child panels
            entryPanel.setBorder(BorderFactory.createCompoundBorder(
                entryPanel.getBorder(),
                new EmptyBorder(spacing, spacing, spacing, spacing)
            ));
            add(Box.createVerticalStrut(10));
		}
		add(childPanel);
	}
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a sample map and header text
            Map<String, String> keyValueMap = new HashMap<>();
            keyValueMap.put("Key 1", "Value 1");
            keyValueMap.put("Key 2", "Value 2");
            keyValueMap.put("Key 3", "Value 3");
            String headerText = "Header Text";

            // Create the panel and add it to a frame
            StatisticContentPanel myPanel = new StatisticContentPanel(keyValueMap, headerText);
            JFrame frame = new JFrame("MyPanel Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(myPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
