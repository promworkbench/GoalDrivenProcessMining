package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.panelHelper.ConfigCards;
import org.processmining.goaldrivenprocessmining.panelHelper.ControlBar;
import org.processmining.goaldrivenprocessmining.panelHelper.SidePanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.animation.AnimationEnabledChangedListener;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMPanel;

import graph.GoalDrivenDFG;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class GoalDrivenPanel extends IvMPanel {

	private static final long serialVersionUID = -1078786029763735572L;
	private static final Insets margins = new Insets(2, 0, 0, 0);

	//gui elements
	private GoalDrivenDFG graphPanel;
	private GoalDrivenDFG graphPanel2;
	//stat sidebar
	private final SidePanel sidePanel;
	//config elemnets
	private final ControlBar controlBar;
	private final ConfigCards configCards;
	private final JPanel contentPanel;
	private final JLayeredPane layeredPanel;

	private final ControllerView<DataState> controllerView;

	public static final String title = "visual Miner";

	public GoalDrivenPanel(GoalDrivenConfiguration configuration, ProMCanceller canceller) {
		super(configuration.getDecorator());
		IvMDecoratorI decorator = configuration.getDecorator();
		setBackground(PanelConstants.BACKGROUND_COLOR);
		int gridy = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();

		double size[][] = { { 0.37 * sWidth, 0.37 * sWidth, 0.26 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		double contentPanelSize[][] = { { 0.5 * sWidth, 0.5 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		setOpaque(false);

		controlBar = new ControlBar();
		controlBar.setBackground(new Color(18, 18, 18));
		configCards = new ConfigCards();

		add(controlBar, "0, 0, 2, 0");
		layeredPanel = new JLayeredPane();
		layeredPanel.setBounds(0, 0, 800, 400);
		layeredPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		contentPanel = new JPanel();

		contentPanel.setLayout(new TableLayout(contentPanelSize));

		layeredPanel.add(contentPanel, new Integer(0));
		contentPanel.setBounds(0, 0, 2000, 1000);
		layeredPanel.add(configCards, new Integer(1), 0);

		add(layeredPanel, "0, 1, 2, 1");
		//controls the margin on the left side of the settings panel
		sidePanel = new SidePanel();
		sidePanel.setBorder(PanelConstants.BETWEEN_PANEL_BORDER);
		contentPanel.add(sidePanel, "2, 1");

		//graph panel
		{
			XLog log = null;
			graphPanel = new GoalDrivenDFG(log);
			graphPanel.setBorder(PanelConstants.BETWEEN_PANEL_BORDER);
			contentPanel.add(graphPanel, "0, 1");
			graphPanel.setBackground(PanelConstants.CONTENT_CARD_COLOR);

		}

		{
			XLog log = null;
			graphPanel2 = new GoalDrivenDFG(log);
			graphPanel2.setBorder(PanelConstants.BETWEEN_PANEL_BORDER);
			contentPanel.add(graphPanel2, "1, 1");
			graphPanel2.setBackground(PanelConstants.CONTENT_CARD_COLOR);

		}

		//controller view
		{
			controllerView = new ControllerView<>(this);
		}

	}

	public JButton drawButton(String name) {
		return new JButton(name);
	}

	public JTable drawConfigTable(AttributeClassifier[] acts, String type) {
		String[] columnNames = { "Activities", "" };
		if (acts.length == 0) {
			return new JTable(new DefaultTableModel(null, columnNames));
		}
		Object[][] data = new Object[acts.length][2];
		for (int i = 0; i < acts.length; i++) {
			data[i][0] = acts[i];
			data[i][1] = type;
		}
		return new JTable(new DefaultTableModel(data, columnNames));
	}

	public ControlBar getControlBar() {
		return controlBar;
	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public JLayeredPane getLayeredPanel() {
		return layeredPanel;
	}

	public ConfigCards getConfigCards() {
		return configCards;
	}

	public GoalDrivenDFG getGraph() {
		return graphPanel;
	}
	
	public void setGraph(GoalDrivenDFG dfg) {
		this.graphPanel = dfg;
	}

	public GoalDrivenDFG getGraph2() {
		return graphPanel2;
	}
	
	public void setGraph2(GoalDrivenDFG dfg) {
		this.graphPanel2 = dfg;
	}


	public void setOnAnimationEnabledChanged(AnimationEnabledChangedListener onAnimationEnabledChanged) {
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}


}