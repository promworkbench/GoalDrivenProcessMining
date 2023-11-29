package org.processmining.goaldrivenprocessmining.panelHelper;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MultiRowSelectionExample extends JFrame {

	private JTable table;

	public MultiRowSelectionExample() {
		setTitle("Multi-Row Selection Example");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initComponents();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponents() {
		// Sample data for the table
		Object[][] data = { { "John", "Doe", 28 }, { "Jane", "Doe", 32 }, { "Bob", "Smith", 45 },
				{ "Alice", "Johnson", 29 } };

		// Column names
		String[] columnNames = { "First Name", "Last Name", "Age" };

		// Create a DefaultTableModel
		DefaultTableModel model = new DefaultTableModel(data, columnNames);

		// Create JTable with the model
		table = new JTable(model);

		// Enable multiple row selection
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Add the table to the frame
		getContentPane().add(new JScrollPane(table));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MultiRowSelectionExample();
			}
		});
	}
}
