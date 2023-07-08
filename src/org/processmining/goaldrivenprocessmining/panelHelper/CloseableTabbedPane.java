package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CloseableTabbedPane extends JTabbedPane {

    public CloseableTabbedPane() {
        super();
    }

    @Override
    public void addTab(String title, final Component component) {
        JPanel tabHeader = new JPanel();
        tabHeader.setOpaque(false);
        tabHeader.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title);
        JButton closeButton = new JButton("x");
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(15, 15));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabIndex = indexOfComponent(component);
                if (tabIndex != -1) {
                    removeTabAt(tabIndex);
                }
            }
        });


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(closeButton, BorderLayout.LINE_END);

        tabHeader.add(titlePanel, BorderLayout.CENTER);

        super.addTab(null, component);
        setTabComponentAt(getTabCount() - 1, tabHeader);
    }
}
