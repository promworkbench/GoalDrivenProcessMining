package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import info.clearthought.layout.TableLayout;

public class CaseConfigPanel extends JPanel {
	private JLabel allCaseLabel;
	private JLabel showCaseLabel;
	private JPanel chooseCaseLabelPanel;
	private JPanel chooseCasePanel;
	private JTable chooseCaseTable;
	private DefaultTableModel chooseCaseModel;
	private JPanel showCasePanel;
	private JTable showCaseTable;
	private DefaultTableModel showCaseModel;
	private JTable caseAttributeTable;
	private DefaultTableModel caseAttributeModel;
	
	private TableRowSorter<DefaultTableModel> chooseCaseTableRowSorter;
	private RangeSliderPanel rangeSlider;
	
	private long maxDuration;

	private JButton caseConfigCancelButton;
	private JButton caseConfigDoneButton;

	public CaseConfigPanel(int width) {
		// Set layout for the main panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// frequency slider
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rangeSlider = new RangeSliderPanel(400);
		rangeSlider.getRangeSlider().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				float lowerPercent = slider.getValue() / 100f;
				float upperPercent = slider.getUpperValue() / 100f;
				long lower = (long) (maxDuration * lowerPercent);
				long upper = (long) (maxDuration * upperPercent);
				filterFreq(lower, upper);
			}
		});
		sliderPanel.add(rangeSlider);
		add(sliderPanel);
		
		
		// First row: Label "All cases"
		this.allCaseLabel = new JLabel("All cases");
		this.chooseCaseLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.chooseCaseLabelPanel.add(this.allCaseLabel);
		add(this.chooseCaseLabelPanel);

		// Second row: panel for 2 tables
		double[][] sizeFullContent = { { 0.25 * width, TableLayout.FILL }, { TableLayout.FILL } };

		JPanel mainPanel = new JPanel(new TableLayout(sizeFullContent));
		add(mainPanel);
		this.createChooseCasePanel();
		this.createShowCasePanel(width);
		mainPanel.setBorder(new LineBorder(Color.GREEN, 2));

		mainPanel.add(this.chooseCasePanel, "0,0");
		mainPanel.add(this.showCasePanel, "1,0");

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.caseConfigCancelButton = new JButton("Cancel");
		this.caseConfigDoneButton = new JButton("Done");
		actEndPanel.add(this.caseConfigCancelButton);
		actEndPanel.add(this.caseConfigDoneButton);
		add(actEndPanel);

		setBorder(new LineBorder(Color.BLUE, 2));
	}

	private void filterFreq(long lower, long upper) {
		RowFilter<Object, Object> rangeFilter = new RowFilter<Object, Object>() {
			public boolean include(Entry<? extends Object, ? extends Object> entry) {
				long freq = (long) convertTimeStringToSeconds((String) entry.getValue(1));
				return freq >= lower && freq <= upper;
			}
		};
		this.chooseCaseTableRowSorter.setRowFilter(rangeFilter);
	}

	private void createChooseCasePanel() {
		this.chooseCasePanel = new JPanel(new BorderLayout());
		String[] columnNames = { "Case", "Duration", "Class" };
		Object[][] data = {};
		this.chooseCaseModel = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Make only column 0, 1 non-editable
				return column != 0 && column != 1;
			}

		};

		this.chooseCaseTable = new JTable(this.chooseCaseModel);
		this.chooseCaseTable.getColumnModel().getColumn(2)
				.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "High", "Neutral", "Low" })));
		this.chooseCaseTableRowSorter = new TableRowSorter<>(this.chooseCaseModel);
		this.chooseCaseTableRowSorter.setComparator(1, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		this.chooseCaseTable.setRowSorter(this.chooseCaseTableRowSorter);
		this.chooseCaseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.chooseCaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.chooseCaseTable.addMouseMotionListener(new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable table = chooseCaseTable;
				int row = table.rowAtPoint(e.getPoint());
				if (row >= 0) {
					table.clearSelection(); // Clear previous selections
					table.addRowSelectionInterval(row, row);
					table.setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
					table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});

		JScrollPane leftScrollPane = new JScrollPane(this.chooseCaseTable);
		this.chooseCasePanel.add(leftScrollPane, BorderLayout.CENTER);
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		this.chooseCasePanel.add(separator, BorderLayout.EAST);
		leftScrollPane.setBorder(new EmptyBorder(0, 0, 0, 5));
	}

	public void updateChooseCaseTable(List<String[]> data) {
		this.chooseCaseModel.setRowCount(0);
		for (String[] d : data) {
			this.chooseCaseModel.addRow(d);
		}
	}

	private void createShowCasePanel(double width) {

		double[][] sizeRightContent = { { 0.3 * width, TableLayout.FILL },
				{ TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.FILL } };

		this.showCasePanel = new JPanel(new TableLayout(sizeRightContent));
		// first row: Case
		this.showCaseLabel = new JLabel("Case: ");
		this.showCasePanel.add(this.showCaseLabel, "0,0,1,0");
		// second row: case attributes, case details
		JLabel caseAttlabel = new JLabel("Case attributes");
		JLabel caseDetailLabel = new JLabel("Case details");
		this.showCasePanel.add(caseAttlabel, "0,1");
		this.showCasePanel.add(caseDetailLabel, "1,1");
		// third row: tables:
		String[] columnNames = { "Attribute", "Value" };
		Object[][] data = {};
		this.caseAttributeModel = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.caseAttributeTable = new JTable(this.caseAttributeModel);
		JScrollPane leftScrollPane = new JScrollPane(this.caseAttributeTable);
		leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		this.showCaseModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.showCaseTable = new JTable(this.showCaseModel);
		JScrollPane rightScrollPane = new JScrollPane(this.showCaseTable);
		rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.showCaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		;

		this.showCasePanel.add(leftScrollPane, "0,2");
		this.showCasePanel.add(rightScrollPane, "1,2");

		this.showCasePanel.setBorder(new EmptyBorder(0, 5, 0, 0));
	}

	public void updateColumnShowCaseTable(List<String> columns) {
		for (String column : columns) {
			// Add a new column to the model
			this.showCaseModel.addColumn(column);
			// Update the table
			this.showCaseModel.fireTableStructureChanged();
		}
	}

	public void updateShowCaseTable(List<Object[]> data) {
		this.showCaseModel.setRowCount(0);
		for (Object[] d : data) {
			this.showCaseModel.addRow(d);
		}
	}

	public void updateCaseAttributeTable(List<Object[]> data) {
		this.caseAttributeModel.setRowCount(0);
		for (Object[] d : data) {
			this.caseAttributeModel.addRow(d);
		}
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

	public void filterCase(Set<Integer> displayIndex) {
		chooseCaseTable.setRowSorter(null);
		// Create a RowFilter based on the list of row indices
		RowFilter<Object, Object> rowFilter = new RowFilter<Object, Object>() {
			@Override
			public boolean include(Entry<? extends Object, ? extends Object> entry) {
				int modelRow = chooseCaseTable.convertRowIndexToModel((int) entry.getIdentifier());
				return displayIndex.contains(modelRow);
			}
		};
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(this.chooseCaseModel);
		rowSorter.setRowFilter(rowFilter);
		rowSorter.setComparator(1, Comparator.comparingDouble(this::convertTimeStringToSeconds));
		chooseCaseTable.setRowSorter(rowSorter);
	}

	public JLabel getAllCaseLabel() {
		return allCaseLabel;
	}

	public void setAllCaseLabel(JLabel allCaseLabel) {
		this.allCaseLabel = allCaseLabel;
	}

	public JPanel getChooseCasePanel() {
		return chooseCasePanel;
	}

	public void setChooseCasePanel(JPanel chooseCasePanel) {
		this.chooseCasePanel = chooseCasePanel;
	}

	public JTable getChooseCaseTable() {
		return chooseCaseTable;
	}

	public void setChooseCaseTable(JTable chooseCaseTable) {
		this.chooseCaseTable = chooseCaseTable;
	}

	public DefaultTableModel getChooseCaseModel() {
		return chooseCaseModel;
	}

	public void setChooseCaseModel(DefaultTableModel chooseCaseModel) {
		this.chooseCaseModel = chooseCaseModel;
	}

	public JPanel getShowCasePanel() {
		return showCasePanel;
	}

	public void setShowCasePanel(JPanel showCasePanel) {
		this.showCasePanel = showCasePanel;
	}

	public JTable getShowCaseTable() {
		return showCaseTable;
	}

	public void setShowCaseTable(JTable showCaseTable) {
		this.showCaseTable = showCaseTable;
	}

	public DefaultTableModel getShowCaseModel() {
		return showCaseModel;
	}

	public void setShowCaseModel(DefaultTableModel showCaseModel) {
		this.showCaseModel = showCaseModel;
	}

	public JButton getCaseConfigCancelButton() {
		return caseConfigCancelButton;
	}

	public void setCaseConfigCancelButton(JButton caseConfigCancelButton) {
		this.caseConfigCancelButton = caseConfigCancelButton;
	}

	public JButton getCaseConfigDoneButton() {
		return caseConfigDoneButton;
	}

	public void setCaseConfigDoneButton(JButton caseConfigDoneButton) {
		this.caseConfigDoneButton = caseConfigDoneButton;
	}

	public JPanel getChooseCaseLabelPanel() {
		return chooseCaseLabelPanel;
	}

	public void setChooseCaseLabelPanel(JPanel chooseCaseLabelPanel) {
		this.chooseCaseLabelPanel = chooseCaseLabelPanel;
	}

	public JLabel getShowCaseLabel() {
		return showCaseLabel;
	}

	public void setShowCaseLabel(JLabel showCaseLabel) {
		this.showCaseLabel = showCaseLabel;
	}

	
	public RangeSliderPanel getRangeSlider() {
		return rangeSlider;
	}

	public void setRangeSlider(RangeSliderPanel rangeSlider) {
		this.rangeSlider = rangeSlider;
	}

	public long getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}

	
	
}
