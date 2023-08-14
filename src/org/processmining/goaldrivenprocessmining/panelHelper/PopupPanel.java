package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;

public class PopupPanel {

	public static List<GroupActObject> groupActObjects = new ArrayList<>();

	//	public static void main(String[] args) {
	//		SwingUtilities.invokeLater(() -> {
	//			JFrame frame = new JFrame("Custom Popup Panel Example");
	//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	//			JPanel mainPanel = new JPanel();
	//			mainPanel.setPreferredSize(new Dimension(800, 600));
	//
	//			// The object that triggers the popup
	//			mainPanel.addMouseListener(new MouseAdapter() {
	//				@Override
	//				public void mouseClicked(MouseEvent e) {
	//					Point mousePosition = e.getPoint();
	//
	//					showPopupPanel(mainPanel, mousePosition);
	//				}
	//			});
	//			frame.add(mainPanel);
	//			frame.pack();
	//			frame.setLocationRelativeTo(null);
	//			frame.setVisible(true);
	//		});
	//	}

	public static void showPopupPanel(Component parent, Point mousePosition) {
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setBorderPainted(true);
		popupMenu.setForeground(Color.BLACK);

		PopupContentPanel popupContent = new PopupContentPanel(popupMenu);

		// Create a temporary container to hold the popupPanel
		JPanel container = new JPanel();
		container.add(popupContent);

		// Add the container to a dialog to force layout and sizing
		JDialog dialog = new JDialog();
		dialog.add(container);
		dialog.pack();
		dialog.setVisible(false);

		// Now you can query the size of the popupPanel
		int popupWidth = popupContent.getWidth();
		int popupHeight = popupContent.getHeight();

		dialog.dispose(); // Close the dialog

		int parentWidth = parent.getWidth();
		int parentHeight = parent.getHeight();

		int popupX = mousePosition.x + 10;
		int popupY = mousePosition.y;

		if (popupX + popupWidth > parentWidth) {
			popupX = mousePosition.x - popupWidth - 10;
		}

		if (popupY + popupHeight > parentHeight) {
			popupY = parentHeight - popupHeight;
		}

		popupMenu.add(popupContent);

		popupMenu.show(parent, popupX, popupY);
	}

}
