package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

public class StatisticActivityPanel extends JPanel {
	private JPanel frequencyPanel;
	private JPanel throughputPanel;
	private JTable waitingTable;
	private JTable leadingTable;

	public StatisticActivityPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		waitingTable = this.createThroughputTable();
		leadingTable = this.createThroughputTable();

		frequencyPanel = new JPanel();
		frequencyPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		frequencyPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		add(frequencyPanel);

		throughputPanel = new JPanel();
		throughputPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		throughputPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		add(throughputPanel);
	}

	public JPanel updateFrequencyPanel(Map<String, String> keyValueMap) {
		JPanel panel = this.frequencyPanel;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		panel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);

		// Row 1: Label with big font and orange foreground
		JLabel labelRow = new JLabel("Frequency");
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

		return panel;
	}

	public JPanel updateThroughputPanel(List<Object[]> waitingActsData, List<Object[]> leadingActsData) {
		JPanel panel = this.throughputPanel;

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
		panel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
		// Row 1: Label with big font and orange foreground
		JLabel labelRow = new JLabel("Throughput");
		labelRow.setFont(GoalDrivenConstants.BOLD_M_FONT);
		labelRow.setForeground(new Color(255, 89, 0));

		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, labelRow.getPreferredSize().height + 10));
		labelPanel.setBackground(GoalDrivenConstants.BORDER_COLOR);
		labelPanel.add(labelRow);
		panel.add(labelPanel);

		// Row 2: label waiting time
		JLabel waitingTimeRow = new JLabel("This activity comes from");
		waitingTimeRow.setForeground(Color.WHITE);
		JPanel waitingTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		waitingTimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, waitingTimeRow.getPreferredSize().height));
		waitingTimePanel.setBackground(GoalDrivenConstants.BORDER_COLOR);
		waitingTimePanel.add(waitingTimeRow);
		panel.add(waitingTimePanel);

		// Row 3: waiting time table
		waitingTable = this.createThroughputTable();
		JScrollPane scrollPane = new JScrollPane(waitingTable);
		JViewport viewport = scrollPane.getViewport();
		viewport.setBackground(GoalDrivenConstants.BORDER_COLOR);
		this.addRowToTable(waitingTable, waitingActsData);
		scrollPane.setBackground(GoalDrivenConstants.BORDER_COLOR);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 120));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane);

		// Row 4: label leading time
		JLabel leadingTimeRow = new JLabel("This activity goes to");
		leadingTimeRow.setForeground(Color.WHITE);
		JPanel leadingTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leadingTimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, leadingTimeRow.getPreferredSize().height));
		leadingTimePanel.setBackground(GoalDrivenConstants.BORDER_COLOR);
		leadingTimePanel.add(leadingTimeRow);
		panel.add(leadingTimePanel);

		// Row 5: waiting time table
		leadingTable = this.createThroughputTable();
		JScrollPane leadingPane = new JScrollPane(leadingTable);
		viewport = leadingPane.getViewport();
		viewport.setBackground(GoalDrivenConstants.BORDER_COLOR);
		leadingPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.addRowToTable(leadingTable, leadingActsData);
		leadingPane.setPreferredSize(new Dimension(leadingPane.getPreferredSize().width, 120));
		leadingPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		leadingPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(leadingPane);

		return panel;
	}

	private JTable createThroughputTable() {
		// Create a DefaultTableModel with data and column names
		DefaultTableModel model = new DefaultTableModel(new Object[][] {
				// Add more rows as needed
		}, new Object[] { "Activity", "Frequency", "Mean", "Median", "Min", "Max" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(model);
		rowSorter.setComparator(1, Comparator.comparingInt(value -> Integer.parseInt((String) value)));
		rowSorter.setComparator(2, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		rowSorter.setComparator(3, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		rowSorter.setComparator(4, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		rowSorter.setComparator(5, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		table.setRowSorter(rowSorter);

		// Set the background color
		table.setBackground(GoalDrivenConstants.BORDER_COLOR);

		// Set the foreground color
		table.setForeground(Color.WHITE);

		// Set the selection background color
		table.setSelectionBackground(Color.DARK_GRAY);

		// Set the selection foreground color
		table.setSelectionForeground(Color.WHITE);

		// Set the grid color
		table.setGridColor(Color.WHITE);

		// Set the font color for the header
		JTableHeader header = table.getTableHeader();
		header.setBackground(GoalDrivenConstants.BORDER_COLOR);
		header.setForeground(Color.WHITE);

		return table;
	}

	private double convertTimeStringToSeconds(String value) {
		// Extract numeric part and string
		Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*([a-zA-Z]+)");
		Matcher matcher = pattern.matcher(value);

		if (matcher.find()) {
			double number = Double.parseDouble(matcher.group(1));

			String unit = matcher.group(2).toLowerCase();
			// Map of time units to seconds
			int secondsInMinute = 60;
			int secondsInHour = 60 * secondsInMinute;
			int secondsInDay = 24 * secondsInHour;
			int secondsInMonth = 30 * secondsInDay; // Assuming an average month
			int secondsInYear = 12 * secondsInMonth; // Assuming an average year

			// Convert to seconds
			switch (unit) {
				case "mins" :
					return number * secondsInMinute;
				case "hrs" :
					return number * secondsInHour;
				case "d" :
					return number * secondsInDay;
				case "mo" :
					return number * secondsInMonth;
				case "yrs" :
					return number * secondsInYear;
				default :
					// Handle unknown units or return 0
					return 0;
			}
		}

		return 0; // Return 0 if no match is found
	}

	public void addRowToTable(JTable table, List<Object[]> data) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);

		for (Object[] d : data) {
			model.addRow(d);
		}
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

	public JTable getWaitingTable() {
		return waitingTable;
	}

	public void setWaitingTable(JTable waitingTable) {
		this.waitingTable = waitingTable;
	}

	public JTable getLeadingTable() {
		return leadingTable;
	}

	public void setLeadingTable(JTable leadingTable) {
		this.leadingTable = leadingTable;
	}

}
