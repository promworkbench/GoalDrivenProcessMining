package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PopupContentPanel extends JPanel {
	private JPanel mainPanel;
	private PopupGroupPanel groupPanel;
	private PopupCategoryPanel categoryPanel;

	public PopupContentPanel() {
		setLayout(new BorderLayout());

		// Create the main panel with "Group" and "Category" buttons
		mainPanel = new JPanel(new FlowLayout());
		JButton groupButton = new JButton("Group");
		JButton categoryButton = new JButton("Category");

		groupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showGroupPanel();
			}
		});

		categoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCategoryPanel();
			}
		});

		mainPanel.add(groupButton);
		mainPanel.add(categoryButton);
		add(mainPanel, BorderLayout.CENTER);

		// Create the Group panel with "Back" button
		groupPanel = new PopupGroupPanel(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMainPanel();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle "Done" action in GroupPanel
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle "Cancel" action in GroupPanel
			}
		});

		groupPanel.setVisible(false);
//		add(groupPanel, BorderLayout.CENTER);

		// Create the Category panel with "Back" button
		categoryPanel = new PopupCategoryPanel(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMainPanel();
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle "Done" action in CategoryPanel
			}
		}, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle "Cancel" action in CategoryPanel
			}
		});
		categoryPanel.setVisible(false);
//		add(categoryPanel, BorderLayout.CENTER);
		
	}

	private void showMainPanel() {
		// Show the main panel with "Group" and "Category" buttons
		removeAll();
		mainPanel.setVisible(true);
		add(mainPanel, BorderLayout.CENTER);
		groupPanel.setVisible(false);
		categoryPanel.setVisible(false);
		revalidate();
		repaint();
	}

	private void showGroupPanel() {
		// Show the Group panel and hide other panels
		removeAll();
		groupPanel.setVisible(true);
		add(groupPanel, BorderLayout.CENTER);
		categoryPanel.setVisible(false);
		mainPanel.setVisible(false);
		revalidate();
		repaint();
	}

	private void showCategoryPanel() {
		// Show the Category panel and hide other panels
		removeAll();
		groupPanel.setVisible(false);
		add(categoryPanel, BorderLayout.CENTER);
		categoryPanel.setVisible(true);
		mainPanel.setVisible(false);
		revalidate();
		repaint();
	}

	// Add any additional methods or functionality specific to your popup content panel
	// For example, custom event handling, data manipulation, etc.
}