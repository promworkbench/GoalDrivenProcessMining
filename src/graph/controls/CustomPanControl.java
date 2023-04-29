package graph.controls;

import java.awt.event.MouseEvent;

import prefuse.controls.PanControl;
import prefuse.util.ui.UILib;

public class CustomPanControl extends PanControl {
	public void mousePressed(MouseEvent e) {
        if ( UILib.isButtonPressed(e, MouseEvent.BUTTON1_MASK) && !e.isShiftDown() ) {
            super.mousePressed(e);
        }
    }
    
    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if ( UILib.isButtonPressed(e, MouseEvent.BUTTON1_MASK) && !e.isShiftDown() ) {
        	super.mouseDragged(e);
        }
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        if ( UILib.isButtonPressed(e, MouseEvent.BUTTON1_MASK) && !e.isShiftDown() ) {
            super.mouseReleased(e);
        }
    }
}
