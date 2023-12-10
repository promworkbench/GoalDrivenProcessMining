package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.goaldrivenprocessmining.algorithms.ButtonColumn;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import prefuse.data.Graph;

public class FilterEdgePanel extends JPanel {
	private RangeSliderPanel edgeSlider;
	private final JTable removingPathsTable;
	private final JTable persistentPathsTable;
	private JTable affectedCaseTable;
	private List<String> disconnectedBeginActs;
	private List<String> disconnectedEndActs;
	private JLabel affectedCaseLabel;
	private JLabel removingPathsLabel;
	private JLabel persistentPathsLabel;

	private JButton saveFilterEdgeConfigurationButton;
	// checkbox 
	private JCheckBox hideIsolateActivity;

	public FilterEdgePanel(String label) {
		this.disconnectedBeginActs = new ArrayList<>();
		this.disconnectedEndActs = new ArrayList<>();
		double filterPanelSize[][] = { { 0.5, 0.5 }, { TableLayoutConstants.FILL } };

		setLayout(new TableLayout(filterPanelSize));
		JPanel sliderPanel = this.createLeftPanel();
		add(sliderPanel, "0,0");
		// panel for removing table and persistent path table
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new GridLayout(2, 1)); // Two rows, one for each table
		removingPathsLabel = new JLabel(
				"<html><b>Removing paths: </b><br><span style='font-weight:normal;'> (0)</span></html>");
		removingPathsLabel.setToolTipText("<html>The paths removed by the slider</html>");
		removingPathsTable = this.createTable();
		JScrollPane removingPathsScrollPane = new JScrollPane(removingPathsTable);

		persistentPathsLabel = new JLabel(
				"<html><b>Persisitent paths: </b><br><span style='font-weight:normal;'> (0)</span></html>");
		persistentPathsLabel.setToolTipText("<html>The paths that are kept permanently</html>");
		persistentPathsTable = this.createTable();
		JScrollPane persistentPathsScrollPane = new JScrollPane(persistentPathsTable);

		Action keepAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = removingPathsTable;
				int modelRow = Integer.valueOf(e.getActionCommand());
				int realModelRow = table.convertRowIndexToView(modelRow);
				Object[] data = new Object[] { table.getValueAt(realModelRow, 0), table.getValueAt(realModelRow, 1),
						table.getValueAt(realModelRow, 2), table.getValueAt(realModelRow, 3), "Remove" };
				hideIsolateActivity.setSelected(false);
				((DefaultTableModel) persistentPathsTable.getModel()).addRow(data);
				updateLabel(persistentPathsLabel, "Persistent paths",
						((DefaultTableModel) persistentPathsTable.getModel()).getRowCount());
				((DefaultTableModel) table.getModel()).removeRow(modelRow);
				updateLabel(removingPathsLabel, "Removing paths",
						((DefaultTableModel) removingPathsTable.getModel()).getRowCount());

			}
		};
		Action removeAct = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = persistentPathsTable;
				int modelRow = Integer.valueOf(e.getActionCommand());
				int realModelRow = table.convertRowIndexToView(modelRow);
				Object[] data = new Object[] { table.getValueAt(realModelRow, 0), table.getValueAt(realModelRow, 1),
						table.getValueAt(realModelRow, 2), table.getValueAt(realModelRow, 3), "Keep" };
				hideIsolateActivity.setSelected(false);
				((DefaultTableModel) removingPathsTable.getModel()).addRow(data);
				updateLabel(removingPathsLabel, "Removing paths",
						((DefaultTableModel) removingPathsTable.getModel()).getRowCount());
				((DefaultTableModel) table.getModel()).removeRow(modelRow);
				updateLabel(persistentPathsLabel, "Persistent paths",
						((DefaultTableModel) persistentPathsTable.getModel()).getRowCount());

			}
		};

		new ButtonColumn(removingPathsTable, keepAct, 4);
		new ButtonColumn(persistentPathsTable, removeAct, 4);
		tablePanel.add(createTablePanel(removingPathsLabel, removingPathsScrollPane));
		tablePanel.add(createTablePanel(persistentPathsLabel, persistentPathsScrollPane));

		add(tablePanel, "1,0");

	}

	public void resetFilter() {
		// clear removing path table
		((DefaultTableModel) this.removingPathsTable.getModel()).setRowCount(0);
		// clear persistent path table
		((DefaultTableModel) this.persistentPathsTable.getModel()).setRowCount(0);
		// edge slider to 0,1 position
		edgeSlider.getRangeSlider().setValue(0);
		edgeSlider.getRangeSlider().setUpperValue(100);
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

	private JPanel createLeftPanel() {
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		edgeSlider = new RangeSliderPanel(400);
		edgeSlider.getRangeSliderLabel1().setText("Lower frequency threshold: ");
		edgeSlider.getRangeSliderLabel1()
				.setToolTipText("<html>This is the <b>minimum</b> frequency an edge can have, <br>"
						+ "expressed as a percentage of the maximum frequency</html>");
		edgeSlider.getRangeSliderLabel2().setText("Upper frequency threshold: ");
		edgeSlider.getRangeSliderLabel2()
				.setToolTipText("<html>This is the <b>maximum</b> frequency an edge can have, <br>"
						+ "expressed as a percentage of the maximum frequency</html>");

		JPanel checkboxPanel = this.createCheckboxPanel();
		JPanel legendLabel = this.createLegend();
		JPanel affectedCasePanel = this.createAffectedCaseTablePanel();
		saveFilterEdgeConfigurationButton = new JButton("Save edge configuration");

		sliderPanel.add(edgeSlider);
		sliderPanel.add(checkboxPanel);
		sliderPanel.add(legendLabel);
		sliderPanel.add(saveFilterEdgeConfigurationButton);
		sliderPanel.add(affectedCasePanel);
		return sliderPanel;
	}

	private JPanel createTablePanel(JLabel label, JScrollPane scrollPane) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createAffectedCaseTablePanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBackground(Color.BLACK);
		separator.setForeground(Color.BLACK);
		separator.setPreferredSize(new Dimension(separator.getPreferredSize().width, 5)); // Adjust thickness
		// 1st Row
		JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		affectedCaseLabel = new JLabel("Cases affected by removing paths:");
		JButton detailsButton = new JButton("See case details");

		firstRowPanel.add(affectedCaseLabel);
		firstRowPanel.add(detailsButton);

		// 2nd Row - JTable with default row sort
		String[] columnNames = { "Case", "Path" };
		Object[][] data = {};

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		affectedCaseTable = new JTable(tableModel);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		affectedCaseTable.setRowSorter(sorter);

		JScrollPane scrollPane = new JScrollPane(affectedCaseTable);

		// Add components to the main panel
		mainPanel.add(separator);
		mainPanel.add(firstRowPanel);
		mainPanel.add(scrollPane);

		return mainPanel;
	}

	private void updateLabel(JLabel label, String title, int caseNum) {
		// add number of cases in label all cases
		String newLabel = "<html><b>" + title + ": </b><br><span style='font-weight:normal;'>" + "("
				+ Integer.toString(caseNum) + ")" + "</span></html>";
		label.setText(newLabel);
	}

	private JPanel createCheckboxPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		hideIsolateActivity = new JCheckBox("Hide isolate activities");

		panel.add(hideIsolateActivity);

		return panel;
	}

	public void updateRemovingTable(List<Object[]> data) {
		// update label
		this.updateLabel(this.removingPathsLabel, "Removing paths", data.size());
		// update table
		DefaultTableModel model = (DefaultTableModel) this.removingPathsTable.getModel();
		model.setRowCount(0);
		for (int i = 0; i < data.size(); i++) {
			model.addRow(new Object[] { data.get(i)[0], data.get(i)[1], data.get(i)[2], data.get(i)[3], "Keep" });
		}
	}

	public void updatePersistentTable(List<Object[]> data) {
		// update label
		this.updateLabel(this.persistentPathsLabel, "Persistent paths", data.size());
		// update table
		DefaultTableModel model = (DefaultTableModel) this.persistentPathsTable.getModel();
		model.setRowCount(0);
		for (int i = 0; i < data.size(); i++) {
			model.addRow(new Object[] { data.get(i)[0], data.get(i)[1], data.get(i)[2], data.get(i)[3], "Remove" });
		}
	}

	public void updateAffectedCaseTable(List<Object[]> data) {
		// update label
		this.updateLabel(this.affectedCaseLabel, "Cases affected by removing paths", data.size());
		// update table
		DefaultTableModel model = (DefaultTableModel) this.affectedCaseTable.getModel();
		model.setRowCount(0);
		for (int i = 0; i < data.size(); i++) {
			model.addRow(data.get(i));
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
		String legendLabelTooltip = "<html><font color='#FF00FF'> Begin-isolate activity: No path from begin to this act </font>"
				+ "<br><font color='orange'>End-isolate activity: No path from this act to end node </font>"
				+ "<br><font color='red'>Isolate activity: Both conditions</font></html>";

		legendLabel.setToolTipText(legendLabelTooltip);
		panel.add(legendLabel);
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

	public JCheckBox getHideIsolateActivity() {
		return hideIsolateActivity;
	}

	public void setHideIsolateActivity(JCheckBox hideIsolateActivity) {
		this.hideIsolateActivity = hideIsolateActivity;
	}

	public JTable getAffectedCaseTable() {
		return affectedCaseTable;
	}

	public void setAffectedCaseTable(JTable affectedCaseTable) {
		this.affectedCaseTable = affectedCaseTable;
	}

	public JButton getSaveFilterEdgeConfigurationButton() {
		return saveFilterEdgeConfigurationButton;
	}

	public void setSaveFilterEdgeConfigurationButton(JButton saveFilterEdgeConfigurationButton) {
		this.saveFilterEdgeConfigurationButton = saveFilterEdgeConfigurationButton;
	}
}
