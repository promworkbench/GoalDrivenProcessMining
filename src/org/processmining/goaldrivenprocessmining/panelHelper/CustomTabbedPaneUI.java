package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
    private Color selectedTabColor;
    private Color unselectedTabColor;

    public CustomTabbedPaneUI(Color selectedTabColor, Color unselectedTabColor) {
        this.selectedTabColor = selectedTabColor;
        this.unselectedTabColor = unselectedTabColor;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int width, int height, boolean isSelected) {
        if (isSelected) {
            g.setColor(selectedTabColor);
        } else {
            g.setColor(unselectedTabColor);
        }
        g.fillRect(x, y, width, height);
    }
}