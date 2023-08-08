package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopupCategoryPanel extends JPanel {
	public PopupCategoryPanel(ActionListener backListener, ActionListener doneListener, ActionListener cancelListener) {
        setLayout(new BorderLayout());
        add(new JLabel("Category Panel Content"), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(backListener);
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(doneListener);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelListener);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
