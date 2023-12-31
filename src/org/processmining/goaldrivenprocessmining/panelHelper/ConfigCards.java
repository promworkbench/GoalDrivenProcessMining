package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class ConfigCards extends JPanel {
	private final FilterConfigPanel filterConfigPanel;
	private final GroupConfigPanel groupConfigPanel;
	private final AllActivityConfigPanel allActivityConfigPanel;
	private final CaseConfigPanel caseConfigPanel;
	private final DisplaySettingPanel legendPanel;
	private final CardLayout layoutCard;
	private final double sWidth;

	public ConfigCards() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		sWidth = screenSize.getWidth();
		setSize(new Dimension(800, 800));

		layoutCard = new CardLayout();
		setLayout(layoutCard);
		// Filter button panel
		filterConfigPanel = new FilterConfigPanel(this.getBounds().width);
		add(filterConfigPanel, "2");
		// group config panel
		groupConfigPanel = new GroupConfigPanel();
		add(groupConfigPanel, "5");
		// legend panel
		legendPanel = new DisplaySettingPanel();
		add(legendPanel, "6");
		// all act config panel
		allActivityConfigPanel = new AllActivityConfigPanel();
		add(allActivityConfigPanel, "7");
		// all act config panel
		caseConfigPanel = new CaseConfigPanel(this.getBounds().width);
		add(caseConfigPanel, "8");

		setVisible(false);
	}

	public FilterConfigPanel getFilterConfigPanel() {
		return filterConfigPanel;
	}


	public CardLayout getLayoutCard() {
		return layoutCard;
	}

	public GroupConfigPanel getGroupConfigPanel() {
		return groupConfigPanel;
	}

	public DisplaySettingPanel getLegendPanel() {
		return legendPanel;
	}

	public AllActivityConfigPanel getAllActivityConfigPanel() {
		return allActivityConfigPanel;
	}

	public CaseConfigPanel getCaseConfigPanel() {
		return caseConfigPanel;
	}
	
	public double getsWidth() {
		return sWidth;
	}

}
