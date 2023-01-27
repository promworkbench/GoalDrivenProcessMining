package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ConfigCards extends JPanel {
	private final ActDisplayPanel actDisplayPanel;
	private final FilterConfigPanel filterConfigPanel;
	private final ActConfigPanel actConfigPanel;
	private final CardLayout layoutCard;
	
	public ConfigCards() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();
		setBounds(0, 0, (int) (0.37 * sWidth), 200);
		
		layoutCard = new CardLayout();
		setLayout(layoutCard);
		// Act button panel
		actDisplayPanel = new ActDisplayPanel(this.getBounds().width);
		add(actDisplayPanel, "3");
		// Filter button panel
		filterConfigPanel = new FilterConfigPanel(this.getBounds().width);
		add(filterConfigPanel, "1");
		// Act config panel
		actConfigPanel = new ActConfigPanel(this.getBounds().width);
		add(actConfigPanel, "2");
		
		setBorder(BorderFactory.createLineBorder(Color.blue));
		setVisible(false);
	}
	
	public FilterConfigPanel getFilterConfigPanel() {
		return filterConfigPanel;
	}

	public ActDisplayPanel getActDisplayPanel() {
		return actDisplayPanel;
	}

	public CardLayout getLayoutCard() {
		return layoutCard;
	}

	public ActConfigPanel getActConfigPanel() {
		return actConfigPanel;
	}
	
	
}
