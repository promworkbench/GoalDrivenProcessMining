package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class NotificationConfigPanel extends JPanel {

	private JLabel notiLabel;
	private JButton notiOpenButton;
	private JButton notiCloseButton;

	public NotificationConfigPanel() {
		setLayout(new BorderLayout());
		notiLabel = new JLabel();
		notiLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		notiCloseButton = new JButton("Close");
		notiOpenButton = new JButton("Open");

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		buttonPanel.add(notiCloseButton);
		buttonPanel.add(notiOpenButton);

		add(notiLabel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
	}
	
	public void updateNotificationLabel(String source, String target) {
		notiLabel.setText("See cases containing the path " + source + " \u2192 " + target);
	}

	public JButton getNotiOpenButton() {
		return notiOpenButton;
	}

	public void setNotiOpenButton(JButton notiOpenButton) {
		this.notiOpenButton = notiOpenButton;
	}

	public JButton getNotiCloseButton() {
		return notiCloseButton;
	}

	public void setNotiCloseButton(JButton notiCloseButton) {
		this.notiCloseButton = notiCloseButton;
	}
	
}
