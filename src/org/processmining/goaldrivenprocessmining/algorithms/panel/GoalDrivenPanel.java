package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.panelHelper.ConfigCards;
import org.processmining.goaldrivenprocessmining.panelHelper.ControlBar;
import org.processmining.goaldrivenprocessmining.panelHelper.SidePanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.animation.AnimationEnabledChangedListener;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMPanel;

import graph.GoalDrivenDFG;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class GoalDrivenPanel extends IvMPanel {

	private static final long serialVersionUID = -1078786029763735572L;
	private static final Insets margins = new Insets(2, 0, 0, 0);

	//gui elements
	private final JPanel contentLeftPanel;
	private final JPanel contentRightPanel;
	private GoalDrivenDFG highDfgPanel;
	private GoalDrivenDFG lowDfgPanel;
	private JLabel lowDfgTitle;
	//stat sidebar
	private final SidePanel sidePanel;
	//config elemnets
	private final ControlBar controlBar;
	private final ConfigCards configCards;
	private final JPanel contentPanel;
	private final JLayeredPane layeredPanel;

	private final ControllerView<DataState> controllerView;

	public GoalDrivenPanel(GoalDrivenConfiguration configuration, ProMCanceller canceller) {
		super(configuration.getDecorator());
		setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();

		double size[][] = { { 0.37 * sWidth, 0.37 * sWidth, 0.26 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		double contentPanelSize[][] = { { 0.5 * sWidth, 0.5 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		setOpaque(false);

		controlBar = new ControlBar();
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
//		sidePanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
		contentPanel.add(sidePanel, "2, 1");

		//graph panel
		{
			contentLeftPanel = new JPanel();
			contentLeftPanel.setLayout(new BorderLayout());
			contentLeftPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			contentLeftPanel.setBorder(BorderFactory.createEmptyBorder(10,5,0,5));
			JLabel hightitle = new JLabel("High-level DFG:");
			hightitle.setForeground(Color.WHITE);
			hightitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 20));
			contentLeftPanel.add(hightitle, BorderLayout.NORTH);
			GDPMLog log = null;
			highDfgPanel = new GoalDrivenDFG(log);
			highDfgPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
			highDfgPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
			contentLeftPanel.add(highDfgPanel, BorderLayout.CENTER);
			
			contentPanel.add(contentLeftPanel, "0, 1");
			
		}

		{
			contentRightPanel = new JPanel();
			contentRightPanel.setLayout(new BorderLayout());
			contentRightPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			contentRightPanel.setBorder(BorderFactory.createEmptyBorder(10,5,0,5));
			JLabel lowtitle = new JLabel("Low-level DFG: ");
			lowtitle.setForeground(Color.WHITE);
			lowtitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 20));
			lowDfgTitle = new JLabel("");
			lowDfgTitle.setForeground(Color.WHITE);
			lowDfgTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 20));
			contentRightPanel.add(lowtitle, BorderLayout.NORTH);
			GDPMLog log = null;
			lowDfgPanel = new GoalDrivenDFG(log);
			lowDfgPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
			lowDfgPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
			contentRightPanel.add(lowDfgPanel, BorderLayout.CENTER);
			contentPanel.add(contentRightPanel, "1, 1");

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

	public GoalDrivenDFG getHighDfgPanel() {
		return highDfgPanel;
	}

	public void setHighDfgPanel(GoalDrivenDFG dfg) {
		this.highDfgPanel = dfg;
	}

	public GoalDrivenDFG getLowDfgPanel() {
		return lowDfgPanel;
	}

	public void setLowDfgPanel(GoalDrivenDFG dfg) {
		this.lowDfgPanel = dfg;
	}

	public JPanel getContentLeftPanel() {
		return contentLeftPanel;
	}

	public JPanel getContentRightPanel() {
		return contentRightPanel;
	}

	public void setOnAnimationEnabledChanged(AnimationEnabledChangedListener onAnimationEnabledChanged) {
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}

}