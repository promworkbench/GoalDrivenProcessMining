package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ColorRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Color) {
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.createGraphics();
			g.setColor(((Color) value));
			g.fillRect(0, 0, 15, 25);
			g.dispose();
			setIcon(new ImageIcon(img));
			setText("");
		}
		return this;
	}
}