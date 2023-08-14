package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PopupCategoryPanel extends JPanel {
	private JPanel lowerPanel;
    private JPanel createCategoryJPanel;
    private JPanel addToCategoryJPanel;
	public PopupCategoryPanel() {
		setLayout(new BorderLayout());
		 // Create the upper panel with checkboxes
       JPanel upperPanel = new JPanel();
       JRadioButton createGroupRadioButton = new JRadioButton("Create new category");
       createGroupRadioButton.setSelected(true);
       JRadioButton addToGroupRadioButton = new JRadioButton("Add to category");
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
       createCategoryJPanel = new JPanel();
       createCategoryJPanel.add(new JLabel("Create new category panel content"));
       lowerPanel.add(createCategoryJPanel, "CreateCategory");

       // Create the "Add to group" panel
       addToCategoryJPanel = new JPanel();
       addToCategoryJPanel.add(new JLabel("Add to category panel content"));
       lowerPanel.add(addToCategoryJPanel, "AddToCategory");

       add(lowerPanel, BorderLayout.CENTER);

       createGroupRadioButton.addActionListener(e -> {
           CardLayout cardLayout = (CardLayout) lowerPanel.getLayout();
           cardLayout.show(lowerPanel, "CreateCategory");
       });

       addToGroupRadioButton.addActionListener(e -> {
           CardLayout cardLayout = (CardLayout) lowerPanel.getLayout();
           cardLayout.show(lowerPanel, "AddToCategory");
       });
		

    }
}
