package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

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

	public AllActivityConfigPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
				filter(filterText);
			}
		});
		searchPanel.add(searchLabel);
		searchPanel.add(searchTextField);
		add(searchPanel);

		// Second Row: All Activities Label and Scrollable Table
		JLabel allActivitiesLabel = new JLabel("All activities");
		String[] columnNames = { "Activity", "Hierarchy", "Priority", "Desirability" };
		Object[][] data = { { "Register", "Hierarchy", "Priority", "Desirability" },{ "Fill Report", "Hierarchy", "Priority", "Desirability" },

				// Add more rows as needed
		};

		this.model = new NonEditableColumnTableModel(data, columnNames);
		this.table = new JTable(model);
		this.tr = new TableRowSorter<DefaultTableModel>(this.model);
		this.table.setRowSorter(this.tr);

		// Customize the renderer and editor for JComboBox columns
		customizeComboBoxColumn(table, 1); // Hierarchy
		customizeComboBoxColumn(table, 2); // Priority
		customizeComboBoxColumn(table, 3); // Desirability

		JScrollPane scrollPane = new JScrollPane(table);

		// cancel done button
		JPanel actEndPanel = new JPanel();
		actEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		allActConfigCancelButton = new JButton("Cancel");
		allActConfigDoneButton = new JButton("Done");
		actEndPanel.add(allActConfigCancelButton);
		actEndPanel.add(allActConfigDoneButton);

		add(allActivitiesLabel);
		add(scrollPane);
		add(actEndPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("My Custom JPanel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			AllActivityConfigPanel customPanel = new AllActivityConfigPanel();
			frame.getContentPane().add(customPanel);

			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	private void filter(String query) {
		this.tr.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0));
	}

	public void updateDefaultConfigTable(Map<String, String> mapActToHierarchy) {
		this.model.setRowCount(0);
		for (Map.Entry<String, String> entry : mapActToHierarchy.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), entry.getValue(), "High", "Neutral" });
		}
	}

	private void customizeComboBoxColumn(JTable table, int columnIndex) {
		TableColumn column = table.getColumnModel().getColumn(columnIndex);

		// Set a custom cell renderer
		column.setCellRenderer(new ComboBoxCellRenderer());

		// Set a custom cell editor
		if (columnIndex == 1) {
			column.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "High", "Low" })));
		} else {
			column.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[] { "High", "Neutral", "Low" })));
		}

	}

	private static class ComboBoxCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1817070725673981243L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof Component) {
				return (Component) value;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}
	}
	
	private static class NonEditableColumnTableModel extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -1129274464463881537L;

		public NonEditableColumnTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            // Make only column A non-editable
            return column != 0;
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
}
