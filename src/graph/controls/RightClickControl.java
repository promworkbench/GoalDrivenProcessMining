package graph.controls;

import java.awt.event.MouseEvent;

import org.processmining.goaldrivenprocessmining.panelHelper.PopupPanel;

import graph.GraphConstants;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

public class RightClickControl extends ControlAdapter {
	
	private Display display;
	
	public RightClickControl(Display display) {
		this.display = display;
		
	}
	@Override
    public void itemClicked(VisualItem item, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && item != null) {
            boolean isSelected = item.getTable().getBoolean(item.getRow(), GraphConstants.SELECT_FIELD);
            if (isSelected) {
                PopupPanel.showPopupPanel(this.display, e.getPoint());
            }
        }
    }
}
