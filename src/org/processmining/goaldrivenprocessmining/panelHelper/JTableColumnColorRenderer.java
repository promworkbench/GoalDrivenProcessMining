package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

public class JTableColumnColorRenderer extends DefaultTableCellRenderer{
	
	public void setValue(Object value) {
    	if (value instanceof Color) {
    		setBackground((Color) value);
    	}
        
    }

}
