package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.goaldrivenprocessmining.algorithms.ButtonColumn;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;

import prefuse.data.Graph;

public class FilterEdgePanel extends JPanel {
	private final RangeSliderPanel edgeSlider;
	private final JTable removingPathsTable;
	private final JTable persistentPathsTable;
	private List<String> disconnectedBeginActs;
	private List<String> disconnectedEndActs;

	public FilterEdgePanel(String label) {
		this.disconnectedBeginActs = new ArrayList<>();
		this.disconnectedEndActs = new ArrayList<>();

		setLayout(new BorderLayout());
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		edgeSlider = new RangeSliderPanel(400);
		add(edgeSlider, BorderLayout.NORTH);
		// panel for removing table and persistent path table
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new GridLayout(2, 1)); // Two rows, one for each table
		JLabel removingPathsLabel = new JLabel("Removing Paths");
		removingPathsTable = createTable();
		JScrollPane removingPathsScrollPane = new JScrollPane(removingPathsTable);

		JLabel persistentPathsLabel = new JLabel("Persistent Paths");
		persistentPathsTable = createTable();
		JScrollPane persistentPathsScrollPane = new JScrollPane(persistentPathsTable);

		Action keepAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = removingPathsTable;
				int modelRow = Integer.valueOf(e.getActionCommand());
				int realModelRow = table.convertRowIndexToView(modelRow);
				Object[] data = new Object[] { table.getValueAt(realModelRow, 0), table.getValueAt(realModelRow, 1),
						table.getValueAt(realModelRow, 2), table.getValueAt(realModelRow, 3), "Remove" };
				((DefaultTableModel) persistentPathsTable.getModel()).addRow(data);
				((DefaultTableModel) table.getModel()).removeRow(modelRow);

			}
		};
		Action removeAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = persistentPathsTable;
				int modelRow = Integer.valueOf(e.getActionCommand());
				int realModelRow = table.convertRowIndexToView(modelRow);
				Object[] data = new Object[] { table.getValueAt(realModelRow, 0), table.getValueAt(realModelRow, 1),
						table.getValueAt(realModelRow, 2), table.getValueAt(realModelRow, 3), "Keep" };
				((DefaultTableModel) removingPathsTable.getModel()).addRow(data);
				((DefaultTableModel) table.getModel()).removeRow(modelRow);

			}
		};

		new ButtonColumn(removingPathsTable, keepAct, 4);
		new ButtonColumn(persistentPathsTable, removeAct, 4);
		tablePanel.add(createTablePanel(removingPathsLabel, removingPathsScrollPane));
		tablePanel.add(createTablePanel(persistentPathsLabel, persistentPathsScrollPane));

		add(tablePanel, BorderLayout.CENTER);

		JPanel legendLabel = createLegend();
		add(legendLabel, BorderLayout.SOUTH);

	}

	public void updateCellColor(Graph graph) {
		List<String> disconnectedBeginAct = StatUtils.getDisconnectedBeginToActsFromDFG(graph);
		List<String> disconnectedEndAct = StatUtils.getDisconnectedActsToEndFromDFG(graph);

		this.setDisconnectedBeginActs(disconnectedBeginAct);
		this.setDisconnectedEndActs(disconnectedEndAct);
	}

	public boolean doesTableContainValue(JTable table, String value) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int rowCount = model.getRowCount();

		for (int row = 0; row < rowCount; row++) {
			String cellValue = (String) model.getValueAt(row, 0);

			if (value.equals(cellValue)) {
				return true; // Found the value in the specified column
			}
		}

		return false; // Value not found in the specified column
	}

	public void updateRemovingTable(List<Object[]> data) {
		DefaultTableModel model = (DefaultTableModel) this.removingPathsTable.getModel();
		model.setRowCount(0);
		for (int i = 0; i < data.size(); i++) {
			model.addRow(new Object[] { data.get(i)[0], data.get(i)[1], data.get(i)[2], data.get(i)[3], "Keep" });
		}
	}

	public void updatePersistentTable(List<String[]> data) {
		DefaultTableModel model = (DefaultTableModel) this.persistentPathsTable.getModel();
		model.setRowCount(0);
		for (int i = 0; i < data.size(); i++) {
			model.addRow(new Object[] { data.get(i)[0], data.get(i)[1], data.get(i)[2], data.get(i)[3], "Remove" });
		}
	}

	private JTable createTable() {
		String[] columnNames = { "Path", "Source", "Target", "Frequency", "Status" };
		Object[][] data = {};
		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Make only column 0, 1 non-editable
				return column != 0 && column != 1 && column != 2 && column != 3;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				// Specify the class for each column
				switch (columnIndex) {
					case 0 :
						return String.class;
					case 1 :
						return String.class;
					case 2 :
						return String.class;
					case 3 :
						return Integer.class;
					case 4 :
						return String.class;
					default :
						return Object.class;
				}
			}
		};
		JTable table = new JTable(model);
		// sort
		TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(model);
		table.setRowSorter(tr);
		// change color of cell in column source, target to highlight the not connect to begin, end nodes.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);

				if (disconnectedBeginActs.contains(value) && disconnectedEndActs.contains(value)) {
					cellComponent.setBackground(Color.RED);
				} else if (disconnectedBeginActs.contains(value)) {
					cellComponent.setBackground(Color.MAGENTA);
				} else if (disconnectedEndActs.contains(value)) {
					cellComponent.setBackground(Color.ORANGE);
				} else {
					cellComponent.setBackground(table.getBackground());
				}

				return cellComponent;
			}

		};
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		return table;
	}

	private ImageIcon createLegendImageIcon() {
		try {
			// Load your image file
			File imageFile = new File("./fig/icon/info.png");
			Image image = ImageIO.read(imageFile);
			Image resizedImage = image.getScaledInstance(12, 12, Image.SCALE_SMOOTH);

			// Scale the image to fit the label (adjust as needed)

			// Create an ImageIcon from the scaled image
			return new ImageIcon(resizedImage);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private JPanel createLegend() {
		JPanel panel = new JPanel();
		JLabel legendLabel = new JLabel("");
		ImageIcon legendImage = this.createLegendImageIcon();
		if (legendImage != null) {
			legendLabel.setIcon(legendImage);
		}
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		String legendLabelTooltip = "<html><font color='orange'> Orange: No path from this act to end node</font>"
				+ "<br><font color='#FF00FF'>Magenta: No path from begin to this act</font>"
				+ "<br><font color='red'>Red: Both conditions</font></html>";

		legendLabel.setToolTipText(legendLabelTooltip);
		panel.add(legendLabel);
		return panel;
	}

	private JPanel createTablePanel(JLabel label, JScrollPane scrollPane) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	public RangeSliderPanel getEdgeSlider() {
		return edgeSlider;
	}

	public JTable getRemovingPathsTable() {
		return removingPathsTable;
	}

	public JTable getPersistentPathsTable() {
		return persistentPathsTable;
	}

	public List<String> getDisconnectedBeginActs() {
		return disconnectedBeginActs;
	}

	public void setDisconnectedBeginActs(List<String> disconnectedBeginActs) {
		this.disconnectedBeginActs = disconnectedBeginActs;
	}

	public List<String> getDisconnectedEndActs() {
		return disconnectedEndActs;
	}

	public void setDisconnectedEndActs(List<String> disconnectedEndActs) {
		this.disconnectedEndActs = disconnectedEndActs;
	}

}
