package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Popup3DotsButton extends JButton {
	private static final int BUTTON_WIDTH = 20;
	private static final int BUTTON_HEIGHT = 50;

	public Popup3DotsButton() {
		setLayout(new FlowLayout());
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(true);
		setFocusPainted(true);
		setToolTipText("Click for more");
		setPreferredSize(new Dimension(BUTTON_WIDTH + 30, BUTTON_HEIGHT));
		setBackground(Color.GREEN);
	}

	@Override
    protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }

        int dotRadius = 2;
        int dotSpacing = 6;
        int startY = (getHeight() - (3 * dotRadius + 2 * dotSpacing)) / 2;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxDistance = Math.max(dotSpacing, dotRadius * 2);
        int circleDiameter = maxDistance * 2 + dotRadius * 2 + 15; // Adjusted for 10 pixels gap

        int circleRadius = circleDiameter / 2;
        int circleCenterX = getWidth() / 2;
        int circleCenterY = getHeight() / 2;

        // Draw a light gray shadow circle slightly larger on all sides
        Ellipse2D shadowCircle = new Ellipse2D.Double(
                circleCenterX - circleRadius - 3, circleCenterY - circleRadius ,
                circleDiameter + 4, circleDiameter + 4);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(shadowCircle);

        // Draw the main circle
        Ellipse2D mainCircle = new Ellipse2D.Double(circleCenterX - circleRadius - 1, circleCenterY - circleRadius + 2,
                circleDiameter, circleDiameter);
        g2d.setColor(getBackground());
        g2d.fill(mainCircle);

        // Draw the three dots
        for (int i = 0; i < 3; i++) {
            int dotY = startY + (i * (dotRadius * 2 + dotSpacing));
            Ellipse2D dot = new Ellipse2D.Double((getWidth() - dotRadius * 2) / 2, dotY, dotRadius * 2, dotRadius * 2);
            g2d.setColor(Color.WHITE);
            g2d.fill(dot);
        }

        g2d.dispose();
    }

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(BUTTON_WIDTH + 30, BUTTON_HEIGHT);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Vertical Dots Button Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Popup3DotsButton dotsButton = new Popup3DotsButton();
			

			JPanel contentPanel = new JPanel();
			contentPanel.add(dotsButton);

			frame.add(contentPanel);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			System.out.println(dotsButton.getSize().getWidth());
		});
	}

}
