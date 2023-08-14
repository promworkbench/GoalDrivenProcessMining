package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenController;

public class PopupContentPanel extends JPanel {
	private JPanel mainPanel;
	private PopupGroupPanel groupPanel;
	private PopupCategoryPanel categoryPanel;
	private CardLayout cardLayout;
	private JButton groupButton;
	private JButton categoryButton;
	private JPopupMenu popupMenu;
	private JButton doneButton;
	private JButton cancelButton;

	public PopupContentPanel(JPopupMenu popupMenu) {
		setLayout(new BorderLayout());

		mainPanel = new JPanel(new FlowLayout());
		mainPanel.setPreferredSize(new Dimension(400, 50)); // Adjust height for visual purposes

		groupButton = new JButton("Group");
		groupButton.setSelected(true);
		categoryButton = new JButton("Category");

		mainPanel.add(groupButton);
		mainPanel.add(categoryButton);

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setPreferredSize(new Dimension(400, 200));
		separator.setForeground(Color.BLACK);
		mainPanel.add(separator, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.NORTH);

		cardLayout = new CardLayout();
		JPanel lowerPanel = new JPanel(cardLayout);

		groupPanel = new PopupGroupPanel(this.popupMenu);

		lowerPanel.add(groupPanel, "Group");

		categoryPanel = new PopupCategoryPanel();

		lowerPanel.add(categoryPanel, "Category");
		lowerPanel.add(new JPanel(), "Default");
		cardLayout.show(lowerPanel, "Group");
		add(lowerPanel, BorderLayout.CENTER);

		groupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(lowerPanel, "Group");
				groupButton.setBackground(Color.GREEN);
				categoryButton.setBackground(null);
			}
		});

		categoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(lowerPanel, "Category");
				categoryButton.setBackground(Color.GREEN);
				groupButton.setBackground(null);
			}
		});

		// Done and Cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		doneButton = new JButton("Done");
		cancelButton = new JButton("Cancel");

		buttonPanel.add(doneButton);
		buttonPanel.add(cancelButton);

		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Choose group
				if (groupButton.isSelected()) {
					if (groupPanel.getAddToGroupRadioButton().isSelected()) {

					}
					if (groupPanel.getCreateGroupRadioButton().isSelected()) {
						GoalDrivenController.addGroupConfigObject(groupPanel.getNewGroupNameTextField().getText());
						// Hide the PopupContentPanel
						PopupContentPanel.this.setVisible(false);
						popupMenu.setVisible(false);
					}
				}
				// Choose category
				if (categoryButton.isSelected()) {

				}

			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Hide the PopupContentPanel
				PopupContentPanel.this.setVisible(false);
				popupMenu.setVisible(false);
			}
		});
		groupButton.setBackground(Color.GREEN);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public PopupContentPanel(Popup3DotsButton threeDotsButton) {
		setLayout(new BorderLayout());

		mainPanel = new JPanel(new FlowLayout());
		mainPanel.setPreferredSize(new Dimension(400, 50)); // Adjust height for visual purposes

		groupButton = new JButton("Group");
		categoryButton = new JButton("Category");

		mainPanel.add(groupButton);
		mainPanel.add(categoryButton);
		add(mainPanel, BorderLayout.NORTH);

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		add(separator, BorderLayout.CENTER);

		cardLayout = new CardLayout();
		JPanel lowerPanel = new JPanel(cardLayout);

		groupPanel = new PopupGroupPanel(this.popupMenu);
		lowerPanel.add(groupPanel, "Group");

		categoryPanel = new PopupCategoryPanel();
		lowerPanel.add(categoryPanel, "Category");
		cardLayout.show(lowerPanel, "Group");
		add(lowerPanel, BorderLayout.CENTER);

		groupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(lowerPanel, "Group");
				groupButton.setBackground(Color.GREEN);
				categoryButton.setBackground(null);
			}
		});

		categoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(lowerPanel, "Category");
				categoryButton.setBackground(Color.GREEN);
				groupButton.setBackground(null);
			}
		});

		// Done and Cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton doneButton = new JButton("Done");
		JButton cancelButton = new JButton("Cancel");

		buttonPanel.add(doneButton);
		buttonPanel.add(cancelButton);

		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle "Done" action here
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Hide the PopupContentPanel
				PopupContentPanel.this.setVisible(false);
				// Hide the 3dots button (assuming it's a field in the parent component)
				threeDotsButton.setVisible(false);
				popupMenu.setVisible(false);
			}
		});
		groupButton.setBackground(Color.GREEN);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

}