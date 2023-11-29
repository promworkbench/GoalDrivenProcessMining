package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ControlBar extends JPanel {
	private final JPanel configPanel;
	//	private final JButton modeButton;
	private final JButton filterButton;
	private final JButton allActivityButton;
	private final JButton groupButton;
	private final JPanel expandPanel;
	private final JButton expandButton;
	private final JButton legendButton;

	public ControlBar() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();
		double controllerBarSize[][] = { { 0.9 * sWidth, 0.1 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(controllerBarSize));

		//config panel: filter, activity, case buttons
		configPanel = new JPanel();
		configPanel.setBackground(GoalDrivenConstants.CONTROL_BAR_BACKGROUND_COLOR);
		{
			add(configPanel, "0,0");
			configPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			//			modeButton = this.drawButton("Mode");
			filterButton = this.drawButton("Filter edges");
			allActivityButton = this.drawButton("Activities");
			groupButton = this.drawButton("Groups");
			legendButton = this.drawButton("Legend");

			//			configPanel.add(modeButton);
			configPanel.add(filterButton);
			configPanel.add(allActivityButton);
			configPanel.add(groupButton);
			configPanel.add(legendButton);

		}
		expandPanel = new JPanel();
		expandPanel.setBackground(GoalDrivenConstants.CONTROL_BAR_BACKGROUND_COLOR);
		{
			add(expandPanel, "1, 0");
			expandButton = this.drawButton("Expand stat window");
			expandPanel.add(expandButton);
		}
	}

	public JButton drawButton(String name) {
		JButton button = new JButton(name);
		button.setBackground(GoalDrivenConstants.BUTTON_BACKGROUND_COLOR);
		button.setForeground(GoalDrivenConstants.BUTTON_FOREGROUND_COLOR);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(GoalDrivenConstants.BUTTON_HOVER_BACKGROUND_COLOR); // Change background color when mouse enters the button
				button.setForeground(GoalDrivenConstants.BUTTON_HOVER_FOREGROUND_COLOR); // Change text color when mouse enters the button
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(GoalDrivenConstants.BUTTON_BACKGROUND_COLOR); // Restore original background color when mouse exits the button
				button.setForeground(GoalDrivenConstants.BUTTON_FOREGROUND_COLOR); // Restore original text color when mouse exits the button
			}
		});
		return button;
	}

	public JPanel getConfigPanel() {
		return configPanel;
	}

	public JButton getFilterButton() {
		return filterButton;
	}

	public JButton getGroupButton() {
		return groupButton;
	}

	public JPanel getExpandPanel() {
		return expandPanel;
	}

	public JButton getExpandButton() {
		return expandButton;
	}

	public JButton getAllActivityButton() {
		return allActivityButton;
	}

	public JButton getLegendButton() {
		return legendButton;
	}

}
