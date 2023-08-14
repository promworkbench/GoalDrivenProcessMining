package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;

public class PopupGroupPanel extends JPanel {
	private JPanel lowerPanel;
	private JPanel createGroupPanel;
	private JPanel addToGroupPanel;
	private JPopupMenu popupMenu;
	// create new group panel
	private JLabel newGroupNameLabel;
	private JTextField newGroupNameTextField;
	// add to group panel
	private JList<GroupActObject> groupList;
	// group button
	private JRadioButton addToGroupRadioButton;
	private JRadioButton createGroupRadioButton;
	// group data
	

	public PopupGroupPanel(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
		setLayout(new BorderLayout());
		// Create the upper panel with checkboxes
		JPanel upperPanel = new JPanel();
		createGroupRadioButton = new JRadioButton("Create new group");
		createGroupRadioButton.setSelected(true);
		addToGroupRadioButton = new JRadioButton("Add to group");
		// Group the radio buttons so that only one can be selected at a time
		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(createGroupRadioButton);
		radioButtonGroup.add(addToGroupRadioButton);

		upperPanel.add(createGroupRadioButton);
		upperPanel.add(addToGroupRadioButton);
		add(upperPanel, BorderLayout.NORTH);

		// Create the lower panel
		lowerPanel = new JPanel(new CardLayout());

		// Create the "Create new group" panel
		{
			createGroupPanel = new JPanel();
			newGroupNameLabel = new JLabel("Group name:");
			newGroupNameTextField = new JTextField(20);

			createGroupPanel.setLayout(new FlowLayout());
			createGroupPanel.add(newGroupNameLabel);
			createGroupPanel.add(newGroupNameTextField);
			lowerPanel.add(createGroupPanel, "CreateGroup");
		}

		// Create the "Add to group" panel
		{
			addToGroupPanel = new JPanel();
			addToGroupPanel.setLayout(new FlowLayout());

			JLabel groupLabel = new JLabel("Group:");
			addToGroupPanel.add(groupLabel);

			// Simulating the list of groups from the backend

			DefaultListModel<GroupActObject> listModel = new DefaultListModel<>();
			for (GroupActObject group : PopupPanel.groupActObjects) {
				listModel.addElement(group);
			}

			groupList = new JList<>(listModel);

			// Wrap the list in a scroll pane to make it scrollable
			JScrollPane scrollPane = new JScrollPane(groupList);
			scrollPane.setPreferredSize(new Dimension(150, 100));
			addToGroupPanel.add(scrollPane);
			lowerPanel.add(addToGroupPanel, "AddToGroup");
		}

		add(lowerPanel, BorderLayout.CENTER);

		createGroupRadioButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) lowerPanel.getLayout();
			cardLayout.show(lowerPanel, "CreateGroup");
		});

		addToGroupRadioButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) lowerPanel.getLayout();
			cardLayout.show(lowerPanel, "AddToGroup");
		});

	}

	public JRadioButton getAddToGroupRadioButton() {
		return addToGroupRadioButton;
	}

	public void setAddToGroupRadioButton(JRadioButton addToGroupRadioButton) {
		this.addToGroupRadioButton = addToGroupRadioButton;
	}

	public JRadioButton getCreateGroupRadioButton() {
		return createGroupRadioButton;
	}

	public void setCreateGroupRadioButton(JRadioButton createGroupRadioButton) {
		this.createGroupRadioButton = createGroupRadioButton;
	}

	public JLabel getNewGroupNameLabel() {
		return newGroupNameLabel;
	}

	public void setNewGroupNameLabel(JLabel newGroupNameLabel) {
		this.newGroupNameLabel = newGroupNameLabel;
	}

	public JTextField getNewGroupNameTextField() {
		return newGroupNameTextField;
	}

	public void setNewGroupNameTextField(JTextField newGroupNameTextField) {
		this.newGroupNameTextField = newGroupNameTextField;
	}



	
}
