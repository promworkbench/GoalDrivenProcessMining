package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import graph.GraphConstants;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class NewCategoryPanel extends JPanel {

	private JTextField groupNameField;
	private JComboBox<Color> cmb;
//	private JCheckBox nominal;
//	private JCheckBox ordinal;
	private JTextField addValueField;
	private JButton addValueButton;
	private DefaultTableModel valueTableModel;
	private JTable valueTable;
	
	public NewCategoryPanel() {
		setSize(new Dimension(600, 300));
		double newCatSize[][] = { { 100, 100, 100 },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM , TableLayoutConstants.FILL} };
		setLayout(new TableLayout(newCatSize));
		groupNameField = new JTextField(5);
		Color[] options = GraphConstants.CATEGORY_COLOR;
		cmb = new JComboBox<>(options);
		cmb.setRenderer(new JComboBoxColorRenderer());
		final JPanel p = new JPanel();
		p.setOpaque(true);
		p.setPreferredSize(new Dimension(200, 100));
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color cd = (Color) cmb.getSelectedItem();
				if (cd != null) {	
					p.setBackground(cd);
				}
			}
		};
		cmb.addActionListener(l);
		l.actionPerformed(null); // update current background
		
//		/*ordinal vs nominal*/
//		nominal = new JCheckBox("Nominal");
//		nominal.setSelected(true);
//        ordinal = new JCheckBox("Ordinal");
//        ordinal.setSelected(false);
//        nominal.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (nominal.isSelected()) {
//                    ordinal.setSelected(false);
//                } else {
//                    ordinal.setSelected(true);
//                }
//            }
//        });
//
//        ordinal.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (ordinal.isSelected()) {
//                    nominal.setSelected(false);
//                } else {
//                    nominal.setSelected(true);
//                }
//            }
//        });
		/********************/
        
		/* add value */
        JLabel addValue = new JLabel("Value");
        addValueField = new JTextField(5);
		addValueButton = new JButton("Add value");
		
		String[] columnNames = {"Value", "Color"};
		valueTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        valueTable = new JTable(valueTableModel);
        JScrollPane scrollPane = new JScrollPane(valueTable);
        
        // TODO: add to controller
        addValueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (valueTableModel.getRowCount() == 0) {
            		valueTableModel.addRow(new Object[] {addValueField.getText(), cmb.getSelectedItem()});
            	} else {
            		int numItem = valueTableModel.getRowCount();
            		List<Color> gradientColors = ColorUtils.getGradientFromBaseColor((Color) cmb.getSelectedItem(), numItem + 1);
            		for (int i = 0; i < numItem; i++) {
            			valueTableModel.setValueAt(gradientColors.get(i), i, 1);
            		}
            		valueTableModel.addRow(new Object[] {addValueField.getText(), gradientColors.get(numItem)});
            	}
            	
                addValueField.setText(""); // Clear the text field
            }
        });
        
        valueTable.getColumnModel().getColumn(1).setCellRenderer(new JTableColumnColorRenderer());
        /*************/
        
		
		
//		add(nominal);
//        add(ordinal);
		add(new JLabel("Category name:"), "0, 0");
		add(groupNameField, "1, 0, 2, 0");
		
		add(new JLabel("Color:"), "0, 1");
		add(cmb, "1, 1, 2, 1");
		add(addValue, "0, 2");
		add(addValueField, "1, 2");
		add(addValueButton,"2, 2");
		add(scrollPane, "0, 3, 2, 3");
		
	}

	public JTextField getGroupNameField() {
		return groupNameField;
	}

	public void setGroupNameField(JTextField groupNameField) {
		this.groupNameField = groupNameField;
	}

	public JComboBox<Color> getCmb() {
		return cmb;
	}

	public void setCmb(JComboBox<Color> cmb) {
		this.cmb = cmb;
	}

//	public JCheckBox getNominal() {
//		return nominal;
//	}
//
//	public void setNominal(JCheckBox nominal) {
//		this.nominal = nominal;
//	}
//
//	public JCheckBox getOrdinal() {
//		return ordinal;
//	}
//
//	public void setOrdinal(JCheckBox ordinal) {
//		this.ordinal = ordinal;
//	}

	public JTextField getAddValueField() {
		return addValueField;
	}

	public void setAddValueField(JTextField addValueField) {
		this.addValueField = addValueField;
	}

	public JButton getAddValueButton() {
		return addValueButton;
	}

	public void setAddValueButton(JButton addValueButton) {
		this.addValueButton = addValueButton;
	}

	public DefaultTableModel getValueTableModel() {
		return valueTableModel;
	}

	public void setValueTableModel(DefaultTableModel valueTableModel) {
		this.valueTableModel = valueTableModel;
	}

	public JTable getValueTable() {
		return valueTable;
	}

	public void setValueTable(JTable valueTable) {
		this.valueTable = valueTable;
	}
	
	
	
}
