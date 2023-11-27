package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConfigCards extends JPanel {
	private final ModePanel modePanel;
	private final ActDisplayPanel actDisplayPanel;
	private final ActConfigPanel actConfigPanel;
	private final GroupConfigPanel groupConfigPanel;
	private final AllActivityConfigPanel allActivityConfigPanel;
	private final LegendPanel legendPanel;
	private final CardLayout layoutCard;
	private final double sWidth;

	public ConfigCards() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		sWidth = screenSize.getWidth();
		setSize(new Dimension(800, 800));

		layoutCard = new CardLayout();
		setLayout(layoutCard);
		JLabel a = new JLabel("ASDFasdf");
		add(a);
		// Mode button panel
		modePanel = new ModePanel(this.getBounds().width);
		add(modePanel, "1");
		// Act config panel
		actConfigPanel = new ActConfigPanel(this.getBounds().width);
		add(actConfigPanel, "3");
		// Act button panel
		actDisplayPanel = new ActDisplayPanel(this.getBounds().width);
		add(actDisplayPanel, "4");
		// group config panel
		groupConfigPanel = new GroupConfigPanel();
		add(groupConfigPanel, "5");
		// legend panel
		legendPanel = new LegendPanel();
		add(legendPanel, "6");
		// all act config panel
		allActivityConfigPanel = new AllActivityConfigPanel(this.getBounds().width);
		add(allActivityConfigPanel, "7");

		setVisible(false);
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

	public ModePanel getModePanel() {
		return modePanel;
	}

	public GroupConfigPanel getGroupConfigPanel() {
		return groupConfigPanel;
	}

	public LegendPanel getLegendPanel() {
		return legendPanel;
	}

	public AllActivityConfigPanel getAllActivityConfigPanel() {
		return allActivityConfigPanel;
	}

	public double getsWidth() {
		return sWidth;
	}

}
