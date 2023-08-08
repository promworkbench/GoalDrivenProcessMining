package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopupGroupPanel extends JPanel {
    public PopupGroupPanel(ActionListener backListener, ActionListener doneListener, ActionListener cancelListener) {
        setLayout(new BorderLayout());
        add(new JLabel("Group Panel Content"), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(backListener);
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(doneListener);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelListener);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.EAST;
        buttonPanel.add(backButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.EAST;
        buttonPanel.add(doneButton, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.EAST;
        buttonPanel.add(cancelButton, constraints);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
