package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Popup3Dots extends JPanel {
	private JButton threeDotsButton;
	private PopupContentPanel popupContent;

	public Popup3Dots() {
//		setLayout(new BorderLayout());
		threeDotsButton = new JButton("...");
		threeDotsButton.setBackground(Color.RED);
		threeDotsButton.setPreferredSize(new Dimension(40, 40));
		threeDotsButton.setFont(new Font("Arial", Font.PLAIN, 24));
		threeDotsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPopup();
			}
		});

		add(threeDotsButton);
		threeDotsButton.setVisible(true);

		// Create the popup content panel
		popupContent = new PopupContentPanel();


	}
	
	private void showPopup() {
        // Show the popup content panel in a JDialog
        JDialog popupDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this));
        popupDialog.add(popupContent);
        popupDialog.pack();
        popupDialog.setLocationRelativeTo(threeDotsButton);
        popupDialog.setVisible(true);
    }

    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                JFrame frame = new JFrame("Popup Panel Example");
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setSize(600, 300);
//
//                Popup3Dots popupPanel = new Popup3Dots();
//                frame.add(popupPanel);
//
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            }
//        });
//    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LayeredPane Overlay Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(400, 300)); // Set the preferred size of the layered pane

            JPanel parentPanel = new JPanel();
            parentPanel.setBackground(Color.LIGHT_GRAY);
            parentPanel.setSize(layeredPane.getPreferredSize());

            JPanel childPanel = new JPanel();
            childPanel.setBackground(Color.red);
            childPanel.setBounds(parentPanel.getWidth() - 100, 0, 20, 50); // Set the location and size of the child panel

            layeredPane.add(parentPanel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(childPanel, JLayeredPane.PALETTE_LAYER);

            frame.getContentPane().add(layeredPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}