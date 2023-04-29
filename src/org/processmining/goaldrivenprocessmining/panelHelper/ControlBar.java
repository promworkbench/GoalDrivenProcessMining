package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.algorithms.panel.PanelConstants;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class ControlBar extends JPanel{
	private final JPanel configPanel;
	private final JButton modeButton;
	private final JButton filterButton;
	private final JButton actButton;
	private final JButton actConfigButton;
	private final JButton caseButton;
	private final JPanel expandPanel;
	private final JButton expandButton;
	
	
	public ControlBar() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();
		double controllerBarSize[][] = { { 0.9 * sWidth, 0.1 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(controllerBarSize));
		setBackground(Color.BLACK);
		
		//config panel: filter, activity, case buttons
		configPanel = new JPanel();
		configPanel.setBackground(new Color(18, 18, 18));
		{
			add(configPanel, "0,0");
			configPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			modeButton = this.drawButton("Mode");
			filterButton = this.drawButton("Filter");
			actButton = this.drawButton("Activity Display");
			actConfigButton = this.drawButton("Activity Configuration");
			caseButton = this.drawButton("Case");
			
			configPanel.add(modeButton);
			configPanel.add(filterButton);
			configPanel.add(actButton);
			configPanel.add(actConfigButton);
			configPanel.add(caseButton);

		}
		expandPanel = new JPanel();
		expandPanel.setBackground(new Color(18, 18, 18));
		{
			add(expandPanel, "1, 0");
			expandButton = this.drawButton("Expand stat window");
			expandPanel.add(expandButton);
		}
	}
	
	public JButton drawButton(String name) {
		JButton button = new JButton(name);
		button.setBackground(PanelConstants.BUTTON_BACKGROUND_COLOR);
		button.setForeground(PanelConstants.BUTTON_FOREGROUND_COLOR);
		return button;
	}

	public JPanel getConfigPanel() {
		return configPanel;
	}

	public JButton getFilterButton() {
		return filterButton;
	}

	public JButton getActButton() {
		return actButton;
	}

	public JButton getCaseButton() {
		return caseButton;
	}

	public JPanel getExpandPanel() {
		return expandPanel;
	}

	public JButton getExpandButton() {
		return expandButton;
	}

	public JButton getActConfigButton() {
		return actConfigButton;
	}

	public JButton getModeButton() {
		return modeButton;
	}
	
}
