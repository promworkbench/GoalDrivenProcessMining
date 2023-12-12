package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

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
	private JPanel filterPanel;

	private JTabbedPane caseAttributeDetailsPanel;

	private TableRowSorter<DefaultTableModel> chooseCaseTableRowSorter;
	private RangeSliderPanel rangeSlider;

	private long maxDuration;

	private JButton showGoodButton;
	private JButton showBadButton;

	private JButton caseConfigCancelButton;
	private JButton caseConfigDoneButton;
	// for filter
	private Set<Integer> displayIndex;

	public CaseConfigPanel(int width) {
		// for filter
		this.displayIndex = new HashSet<>();
		// Set layout for the main panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// First row: Label "All cases"
		JLabel titleLable = new JLabel("Case configuration");
		titleLable.setFont(GoalDrivenConstants.BOLD_M_FONT);
		add(titleLable);
		// Filter panel if there is filter
		filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(filterPanel);

		// frequency slider
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rangeSlider = new RangeSliderPanel(width);
		rangeSlider.getRangeSliderLabel1().setText("Lower duration threshold: ");
		rangeSlider.getRangeSliderLabel1()
				.setToolTipText("<html>This is the <b>minimum</b> duration an edge can have, <br>"
						+ "expressed as a percentage of the maximum duration</html>");
		rangeSlider.getRangeSliderLabel2().setText("Upper duration threshold: ");
		rangeSlider.getRangeSliderLabel2()
				.setToolTipText("<html>This is the <b>maximum</b> duration an edge can have, <br>"
						+ "expressed as a percentage of the maximum duration</html>");
		rangeSlider.getRangeSlider().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				float lowerPercent = slider.getValue() / 100f;
				float upperPercent = slider.getUpperValue() / 100f;
				long lower = (long) (maxDuration * lowerPercent) - 1;
				long upper = (long) (maxDuration * upperPercent) + 1;
				filterFreq(lower, upper, displayIndex);
			}
		});
		sliderPanel.add(rangeSlider);
		add(sliderPanel);

		// Create a JTabbedPane
		JTabbedPane tabbedPane = new JTabbedPane();

		//case overview
		JPanel caseOverviewPanel = this.createCaseOverviewPanel(width);

		//case statistic
		this.caseAttributeDetailsPanel = new JTabbedPane();
		tabbedPane.addTab("Case overview", caseOverviewPanel);
		tabbedPane.addTab("Case attribute details", this.caseAttributeDetailsPanel);

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.caseConfigCancelButton = new JButton("Cancel");
		this.caseConfigDoneButton = new JButton("Done");
		actEndPanel.add(this.caseConfigCancelButton);
		actEndPanel.add(this.caseConfigDoneButton);
		add(tabbedPane);
		add(actEndPanel);

	}

	public void updateFilterOnPath(EdgeObject edgeObject, Set<Integer> displayIndex1) {
		this.displayIndex = displayIndex1;
		// update filter panel
		this.filterPanel.removeAll();
		JLabel filterLabel = new JLabel("Contain path: " + edgeObject.getNode1() + " \u2192 " + edgeObject.getNode2());
		filterLabel.setFont(GoalDrivenConstants.PLAIN_M_FONT);
		this.filterPanel.add(filterLabel);
		JButton clearButton = new JButton("Clear filtering");
		this.filterPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// reset filter
				chooseCaseTable.setRowSorter(chooseCaseTableRowSorter);
				updateAllCaseLabel(chooseCaseModel.getRowCount());
				displayIndex = new HashSet<>();
				filterPanel.removeAll();
				revalidate();
				repaint();
			}
		});
		// reset show case panel
		this.resetShowCasePanel();
		// update table
		this.filterCase(displayIndex);

	}

	private JPanel createCaseOverviewPanel(double width) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		this.chooseCaseLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.allCaseLabel = new JLabel("Cases:");
		JButton assignButton = new JButton("Assign class");
		showGoodButton = new JButton("Show good cases"); showBadButton = new JButton("Show bad cases");
		assignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAssignValueDialog();
			}
		});
		this.chooseCaseLabelPanel.add(this.allCaseLabel);
		this.chooseCaseLabelPanel.add(assignButton);
		this.chooseCaseLabelPanel.add(showGoodButton);
		this.chooseCaseLabelPanel.add(showBadButton);
		panel.add(this.chooseCaseLabelPanel);

		// Second row: panel for 2 tables
		double[][] sizeFullContent = { { 0.4 * width, TableLayout.FILL }, { TableLayout.FILL } };

		JPanel mainPanel = new JPanel(new TableLayout(sizeFullContent));
		this.createChooseCasePanel();
		this.createShowCasePanel(width);

		mainPanel.add(this.chooseCasePanel, "0,0");
		mainPanel.add(this.showCasePanel, "1,0");
		panel.add(mainPanel);
		return panel;
	}

	private void updateCaseAttributeDetailsPanel(List<Integer> caseIndex) {
		// calculate case attributes of the cases
		Map<String, Map<String, Integer>> mapCaseAttribute = StatUtils.getMapCaseAttribute(caseIndex);
		// reset tab:
		this.caseAttributeDetailsPanel.removeAll();
		// each key is a tab 
		for (Map.Entry<String, Map<String, Integer>> entry : mapCaseAttribute.entrySet()) {
			JPanel panel = this.createAttributeTablePanel(entry.getValue());
			this.caseAttributeDetailsPanel.addTab(entry.getKey(), panel);
		}
	}

	private void showAssignValueDialog() {
		// Get selected rows
		int[] selectedRows = this.chooseCaseTable.getSelectedRows();

		// Check if any row is selected
		if (selectedRows.length == 0) {
			JOptionPane.showMessageDialog(this, "Please select at least one row.");
			return;
		}

		// Create checkboxes and comboboxes
		JComboBox<String> classCaseComboBox = new JComboBox<>(new String[] { "Good", "Neutral", "Bad" });

		// Create a panel with checkboxes and comboboxes
		JPanel panel = new JPanel();
		panel.add(classCaseComboBox);

		// Show the dialog
		int result = JOptionPane.showConfirmDialog(this, panel, "Choose class value to assign",
				JOptionPane.OK_CANCEL_OPTION);

		// Check if the user clicked OK
		if (result == JOptionPane.OK_OPTION) {
			// Assign the values to the selected rows only if the corresponding checkboxes are selected
			for (int row : selectedRows) {
				this.chooseCaseTable.setValueAt(classCaseComboBox.getSelectedItem(), row, 2);
			}
		}
	}

	private void filterFreq(long lower, long upper, Set<Integer> displayIndex) {
		chooseCaseTable.setRowSorter(this.chooseCaseTableRowSorter);
		RowFilter<Object, Object> rangeFilter = new RowFilter<Object, Object>() {
			public boolean include(Entry<? extends Object, ? extends Object> entry) {
				long freq = (long) StatUtils.convertTimeStringToSeconds((String) entry.getValue(1));
				Boolean isDisplayedable = true;
				if (!displayIndex.isEmpty()) {
					isDisplayedable = displayIndex.contains((int) entry.getIdentifier());
				}
				return (freq >= lower && freq <= upper) && isDisplayedable;
			}
		};
		this.chooseCaseTableRowSorter.setRowFilter(rangeFilter);
		// update case number label
		int visibleRowCount = this.chooseCaseTableRowSorter.getViewRowCount();
		this.updateAllCaseLabel(visibleRowCount);

		// get all view case from Log
		List<Integer> visibleTraces = new ArrayList<>();
		for (int i = 0; i < visibleRowCount; i++) {
			int modelRow = this.chooseCaseTable.convertRowIndexToModel(i);
			visibleTraces.add(modelRow);
		}
		// update attribute details panel
		this.updateCaseAttributeDetailsPanel(visibleTraces);
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
		rowSorter.setComparator(1, Comparator.comparingDouble(StatUtils::convertTimeStringToSeconds));
		chooseCaseTable.setRowSorter(rowSorter);
		// update case number label
		int visibleRowCount = rowSorter.getViewRowCount();
		this.updateAllCaseLabel(visibleRowCount);
		// get all view case from Log
		List<Integer> visibleTraces = new ArrayList<>();
		for (int i = 0; i < visibleRowCount; i++) {
			int modelRow = this.chooseCaseTable.convertRowIndexToModel(i);
			visibleTraces.add(modelRow);
		}
		// update attribute details panel
		this.updateCaseAttributeDetailsPanel(visibleTraces);
	}

	private JPanel createAttributeTablePanel(Map<String, Integer> dataMap) {
		JPanel panel = new JPanel(new BorderLayout());

		// Create table model and set data
		DefaultTableModel tableModel = new DefaultTableModel() {
			// Override the getColumnClass method to specify the class of the values in each column
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 1) { // Column index 1 corresponds to the "Age" column
					return Integer.class; // Set the class of this column to Integer
				}
				return super.getColumnClass(columnIndex);
			}
		};
		tableModel.addColumn("Value");
		tableModel.addColumn("Frequency");

		for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
			tableModel.addRow(new Object[] { entry.getKey(), entry.getValue() });
		}

		// Create sortable table with the model
		JTable table = new JTable(tableModel);
		table.setAutoCreateRowSorter(true);

		// Add table to a scroll pane
		JScrollPane scrollPane = new JScrollPane(table);

		// Add scroll pane to the panel
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
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
				.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "Good", "Neutral", "Bad" })));
		this.chooseCaseTableRowSorter = new TableRowSorter<>(this.chooseCaseModel);
		this.chooseCaseTableRowSorter.setComparator(1,
				Comparator.comparingDouble(StatUtils::convertTimeStringToSeconds));
		this.chooseCaseTable.setRowSorter(this.chooseCaseTableRowSorter);
		this.chooseCaseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.chooseCaseTable.addMouseMotionListener(new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable table = chooseCaseTable;
				int row = table.rowAtPoint(e.getPoint());
				if (table.getSelectedRows().length <= 1) {
					if (row >= 0) {
						table.clearSelection(); // Clear previous selections
						table.addRowSelectionInterval(row, row);
						table.setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				} else {
					// Rows are selected, disable hover effect
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
		// add number of cases in label all cases
		this.updateAllCaseLabel(data.size());
		List<Integer> showIndex = new ArrayList<>();
		int index = 0;
		// add data to table
		this.chooseCaseModel.setRowCount(0);
		for (String[] d : data) {
			this.chooseCaseModel.addRow(d);
			showIndex.add(index);
			index++;
		}
		// update case attributes details
		this.updateCaseAttributeDetailsPanel(showIndex);
	}

	private void updateAllCaseLabel(int caseNum) {
		// add number of cases in label all cases
		String newLabel = "<html><b>Cases: </b><br><span style='font-weight:normal;'>" + "(" + Integer.toString(caseNum)
				+ ")" + "</span></html>";
		this.allCaseLabel.setText(newLabel);
	}

	private void createShowCasePanel(double width) {

		double[][] sizeRightContent = { { 0.25 * width, TableLayout.FILL },
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

		this.showCasePanel.add(leftScrollPane, "0,2");
		this.showCasePanel.add(rightScrollPane, "1,2");

		this.showCasePanel.setBorder(new EmptyBorder(0, 5, 0, 0));
	}

	private void resetShowCasePanel() {
		// remove label for case
		this.showCaseLabel.setText("Case: ");
		// remove data in tables
		this.caseAttributeModel.setRowCount(0);
		this.showCaseModel.setRowCount(0);

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

	public void updateShowCaseAttributeTable(List<Object[]> data) {
		this.caseAttributeModel.setRowCount(0);
		for (Object[] d : data) {
			this.caseAttributeModel.addRow(d);
		}
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

	public JButton getShowBadButton() {
		return showBadButton;
	}

	public void setShowBadButton(JButton showBadButton) {
		this.showBadButton = showBadButton;
	}

	public JButton getShowGoodButton() {
		return showGoodButton;
	}

	public void setShowGoodButton(JButton showGoodButton) {
		this.showGoodButton = showGoodButton;
	}

	public JPanel getFilterPanel() {
		return filterPanel;
	}

	public void setFilterPanel(JPanel filterPanel) {
		this.filterPanel = filterPanel;
	}

	public Set<Integer> getDisplayIndex() {
		return displayIndex;
	}

	public void setDisplayIndex(Set<Integer> displayIndex) {
		this.displayIndex = displayIndex;
	}

}
