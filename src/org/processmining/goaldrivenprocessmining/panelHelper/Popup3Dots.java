package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class Popup3Dots extends JPanel {
	private Popup3DotsButton threeDotsButton;
	private PopupContentPanel popupContent;
	private JPopupMenu popupMenu;

	public Popup3Dots(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
		
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(80, 80));
		threeDotsButton = new Popup3DotsButton();
		threeDotsButton.setBackground(Color.RED);
		threeDotsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPopup(e);
			}
		});

		add(threeDotsButton);
		threeDotsButton.setVisible(true);

		// Create the popup content panel
		popupContent = new PopupContentPanel(this.threeDotsButton);
		popupContent.setPopupMenu(this.popupMenu);

	}
	
	private void showPopup(ActionEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(popupContent);

        Component source = (Component) e.getSource();
        popupMenu.show(source, source.getWidth(), 0);
    }
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Custom Popup Panel Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPopupMenu popupMenu = new JPopupMenu() {
    			@Override
    			public void setVisible(boolean b) {
    				// Override setVisible to prevent the popup from closing
    				// when clicking inside the popupPanel
    				if (!b && getBounds().contains(getMousePosition())) {
    					return;
    				}
    				super.setVisible(b);
    			}
    		};
    		popupMenu.setBorderPainted(false);
            Popup3Dots popup3Dots = new Popup3Dots(popupMenu);
            popupMenu.add(popup3Dots);
            frame.add(popupMenu);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            System.out.println(popup3Dots.getWidth());
        });
    }
    
}