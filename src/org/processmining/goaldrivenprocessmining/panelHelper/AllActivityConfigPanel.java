package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

import graph.GraphConstants;
import prefuse.util.ColorLib;

public class AllActivityConfigPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9055472507384816059L;
	private DefaultTableModel model;
	private JTable table;
	private JButton allActConfigCancelButton;
	private JButton allActConfigDoneButton;
	private TableRowSorter<DefaultTableModel> tr;
	private RangeSliderPanel rangeSlider;
	private JLabel allActLabel;
	private int maxActFreq = 1;

	public AllActivityConfigPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// all activity label
		JLabel allActivitiesLabel = new JLabel("Activity configuration");
		allActivitiesLabel.setFont(GoalDrivenConstants.BOLD_M_FONT);
		add(allActivitiesLabel);
		// First Row: Search Label and TextField
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel searchLabel = new JLabel("Search:");
		JTextField searchTextField = new JTextField(20);
		searchTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFilter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFilter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFilter();
			}

			private void updateFilter() {
				String filterText = searchTextField.getText().toLowerCase();
				filterAct(filterText);
			}
		});
		searchPanel.add(searchLabel);
		searchPanel.add(searchTextField);
		add(searchPanel);

		// frequency slider
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rangeSlider = new RangeSliderPanel(400);
		rangeSlider.getRangeSliderLabel1().setText("Lower frequency threshold: ");
		rangeSlider.getRangeSliderLabel1()
				.setToolTipText("<html>This is the <b>minimum</b> frequency an edge can have, <br>"
						+ "expressed as a percentage of the maximum frequency</html>");
		rangeSlider.getRangeSliderLabel2().setText("Upper frequency threshold: ");
		rangeSlider.getRangeSliderLabel2()
				.setToolTipText("<html>This is the <b>maximum</b> frequency an edge can have, <br>"
						+ "expressed as a percentage of the maximum frequency</html>");
		rangeSlider.getRangeSlider().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				float lowerPercent = slider.getValue() / 100f;
				float upperPercent = slider.getUpperValue() / 100f;
				int lower = (int) (maxActFreq * lowerPercent);
				int upper = (int) (maxActFreq * upperPercent);
				filterFreq(lower, upper);
			}
		});
		sliderPanel.add(rangeSlider);
		add(sliderPanel);

		// All Activities Label and Scrollable Table
		JPanel labelJPanel = new JPanel();
		labelJPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		allActLabel = new JLabel("Activities: ");
		labelJPanel.add(allActLabel);
		// assign button
		JButton assignButton = new JButton("Assign Value");
		assignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAssignValueDialog();
			}
		});
		labelJPanel.add(assignButton);
		// table
		String[] columnNames = { "Activity", "Frequency", "Hierarchy", "Priority", "Desirability" };
		Object[][] data = {};
		this.model = new NonEditableColumnTableModel(data, columnNames);
		this.table = new JTable(model);
		this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// change color of cell in column source, target to highlight the not connect to begin, end nodes.
		DefaultTableCellRenderer rendererDesired = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (!isSelected) {
					if (value.equals("High")) {
						cellComponent.setBackground(ColorLib.getColor(GraphConstants.NODE_HIGH_DESIRED_STROKE_COLOR));
					} else if (value.equals("Low")) {
						cellComponent.setBackground(ColorLib.getColor(GraphConstants.NODE_LOW_DESIRED_STROKE_COLOR));
					} else {
						cellComponent.setBackground(table.getBackground());
					}
				}

				return cellComponent;
			}

		};
		DefaultTableCellRenderer rendererPriority = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (!isSelected) {
					if (value.equals("High")) {
						cellComponent.setBackground(ColorLib.getColor(GraphConstants.NODE_HIGH_PRIORITY_STROKE_COLOR));
					} else if (value.equals("Low")) {
						cellComponent.setBackground(ColorLib.getColor(GraphConstants.NODE_LOW_PRIORITY_STROKE_COLOR));
					} else {
						cellComponent.setBackground(table.getBackground());
					}
				}

				return cellComponent;
			}

		};
		this.table.getColumnModel().getColumn(3).setCellRenderer(rendererPriority);
		this.table.getColumnModel().getColumn(4).setCellRenderer(rendererDesired);
		this.tr = new TableRowSorter<DefaultTableModel>(this.model);
		this.table.setRowSorter(this.tr);

		// Customize the renderer and editor for JComboBox columns
		customizeComboBoxColumn(table, 2); // Hierarchy
		customizeComboBoxColumn(table, 3); // Priority
		customizeComboBoxColumn(table, 4); // Desirability

		JScrollPane scrollPane = new JScrollPane(table);

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		allActConfigCancelButton = new JButton("Cancel");
		allActConfigDoneButton = new JButton("Apply");
		actEndPanel.add(allActConfigCancelButton);
		actEndPanel.add(allActConfigDoneButton);

		add(labelJPanel);
		add(scrollPane);
		add(actEndPanel);
	}

	private void showAssignValueDialog() {
		// Get selected rows
		int[] selectedRows = this.table.getSelectedRows();

		// Check if any row is selected
		if (selectedRows.length == 0) {
			JOptionPane.showMessageDialog(this, "Please select at least one row.");
			return;
		}

		// Create checkboxes and comboboxes
		JCheckBox hierarchyCheckBox = new JCheckBox("Assign Hierarchy:");
		JComboBox<String> hierarchyComboBox = new JComboBox<>(new String[] { "High", "Low" });
		JCheckBox priorityCheckBox = new JCheckBox("Assign Priority:");
		JComboBox<String> priorityComboBox = new JComboBox<>(new String[] { "High", "Neutral", "Low" });
		priorityComboBox.setSelectedItem("Neutral");
		JCheckBox desirabilityCheckBox = new JCheckBox("Assign Desirability:");
		JComboBox<String> desirabilityComboBox = new JComboBox<>(new String[] { "High", "Neutral", "Low" });
		desirabilityComboBox.setSelectedItem("Neutral");

		// Create a panel with checkboxes and comboboxes
		JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
		panel.add(hierarchyCheckBox);
		panel.add(hierarchyComboBox);
		panel.add(priorityCheckBox);
		panel.add(priorityComboBox);
		panel.add(desirabilityCheckBox);
		panel.add(desirabilityComboBox);

		// Add an ActionListener to each checkbox
		ActionListener checkBoxListener = e -> {
			hierarchyComboBox.setEnabled(hierarchyCheckBox.isSelected());
			priorityComboBox.setEnabled(priorityCheckBox.isSelected());
			desirabilityComboBox.setEnabled(desirabilityCheckBox.isSelected());
		};

		hierarchyCheckBox.addActionListener(checkBoxListener);
		priorityCheckBox.addActionListener(checkBoxListener);
		desirabilityCheckBox.addActionListener(checkBoxListener);

		hierarchyComboBox.setEnabled(false);
		priorityComboBox.setEnabled(false);
		desirabilityComboBox.setEnabled(false);

		// Show the dialog
		int result = JOptionPane.showConfirmDialog(this, panel, "Choose Values to Assign",
				JOptionPane.OK_CANCEL_OPTION);

		// Check if the user clicked OK
		if (result == JOptionPane.OK_OPTION) {
			// Assign the values to the selected rows only if the corresponding checkboxes are selected
			for (int row : selectedRows) {
				if (hierarchyCheckBox.isSelected()) {
					String hierarchyValue = (String) hierarchyComboBox.getSelectedItem();
					// Assuming the column for hierarchy is 2
					this.table.setValueAt(hierarchyValue, row, 2);
				}

				if (priorityCheckBox.isSelected()) {
					String priorityValue = (String) priorityComboBox.getSelectedItem();
					// Assuming the column for priority is 3
					this.table.setValueAt(priorityValue, row, 3);
				}

				if (desirabilityCheckBox.isSelected()) {
					String desirabilityValue = (String) desirabilityComboBox.getSelectedItem();
					// Assuming the column for desirability is 4
					this.table.setValueAt(desirabilityValue, row, 4);
				}
			}
		}
	}

	private void filterAct(String query) {
		RowFilter<Object, Object> categoryFilter = RowFilter.regexFilter("(?i)" + query, 0);
		this.tr.setRowFilter(categoryFilter);
		// update case number label
		int visibleRowCount = this.tr.getViewRowCount();
		this.updateAllActLabel(visibleRowCount);
	}

	private void filterFreq(int lower, int upper) {
		RowFilter<Object, Object> rangeFilter = new RowFilter<Object, Object>() {
			public boolean include(Entry<? extends Object, ? extends Object> entry) {
				int freq = (int) entry.getValue(1);
				return freq >= lower && freq <= upper;
			}
		};
		this.tr.setRowFilter(rangeFilter);
		// update case number label
		int visibleRowCount = this.tr.getViewRowCount();
		this.updateAllActLabel(visibleRowCount);
	}

	private void updateAllActLabel(int caseNum) {
		// add number of cases in label all cases
		String newLabel = "<html><b>Activities: </b><br><span style='font-weight:normal;'>" + "("
				+ Integer.toString(caseNum) + ")" + "</span></html>";
		this.allActLabel.setText(newLabel);
	}

	public void updateDefaultConfigTable(Map<String, String> mapActToHierarchy, Map<String, Integer> mapActFreq) {
		// update num activities
		this.updateAllActLabel(mapActToHierarchy.size());

		// update default config table
		this.model.setRowCount(0);
		for (Map.Entry<String, String> entry : mapActToHierarchy.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), mapActFreq.get(entry.getKey()), entry.getValue(), "Neutral",
					"Neutral" });
		}
	}

	private void customizeComboBoxColumn(JTable table, int columnIndex) {
		TableColumn column = table.getColumnModel().getColumn(columnIndex);

		// Set a custom cell editor
		if (columnIndex == 2) {
			column.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "High", "Low" })));
		} else {
			column.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "High", "Neutral", "Low" })));
		}

	}

	public static class NonEditableColumnTableModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1129274464463881537L;

		public NonEditableColumnTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			// Make only column 0, 1 non-editable
			return column != 0 && column != 1;
		}

		@Override
		public Class getColumnClass(int column) {
			switch (column) {
				case 0 :
					return String.class;
				case 1 :
					return Integer.class;
				case 2 :
					return String.class;
				case 3 :
					return String.class;
				default :
					return String.class;
			}
		}
	}

	public DefaultTableModel getModel() {
		return model;
	}

	public void setModel(DefaultTableModel model) {
		this.model = model;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public JButton getAllActConfigCancelButton() {
		return allActConfigCancelButton;
	}

	public void setAllActConfigCancelButton(JButton allActConfigCancelButton) {
		this.allActConfigCancelButton = allActConfigCancelButton;
	}

	public JButton getAllActConfigDoneButton() {
		return allActConfigDoneButton;
	}

	public void setAllActConfigDoneButton(JButton allActConfigDoneButton) {
		this.allActConfigDoneButton = allActConfigDoneButton;
	}

	public RangeSliderPanel getRangeSlider() {
		return rangeSlider;
	}

	public void setRangeSlider(RangeSliderPanel rangeSlider) {
		this.rangeSlider = rangeSlider;
	}

	public int getMaxActFreq() {
		return maxActFreq;
	}

	public void setMaxActFreq(int maxActFreq) {
		this.maxActFreq = maxActFreq;
	}

}
