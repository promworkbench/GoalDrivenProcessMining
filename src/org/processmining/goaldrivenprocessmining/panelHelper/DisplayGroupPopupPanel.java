package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenController;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;

public class DisplayGroupPopupPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DisplayGroupPopupPanel(JPopupMenu popupMenu, Point mousePosition, GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JButton showButton = new JButton("Show");
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GoalDrivenController.displaySelectedGroup(groupSkeleton, isHighLevel);
			}
		});
		JButton ungroupButton = new JButton("Ungroup");
		ungroupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GoalDrivenController.ungroupGroupConfigObject(groupSkeleton.getGroupName());
				DisplayGroupPopupPanel.this.setVisible(false);
				popupMenu.setVisible(false);
				revalidate();
				repaint();
			}
		});
		add(showButton);
		add(ungroupButton);

	}

}
