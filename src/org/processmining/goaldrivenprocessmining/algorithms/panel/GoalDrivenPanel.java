package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.panelHelper.ConfigCards;
import org.processmining.goaldrivenprocessmining.panelHelper.ControlBar;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.Selection;
import org.processmining.plugins.inductiveVisualMiner.animation.AnimationEnabledChangedListener;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.InputFunction;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMPanel;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeView;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeViews;
import org.processmining.plugins.inductiveminer2.logs.IMEvent;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;

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
	private final JPanel sidePanel;
	//config elemnets
	private final ControlBar controlBar;
	private final ConfigCards configCards;
	private final JPanel contentPanel;
	private final JLayeredPane layeredPanel;

	private final JLabel classifierLabel;
	private IvMClassifierChooser classifiersCombobox1;
	private final JButton preMiningFiltersButton;
	private final IvMFilterTreeViews preMiningFilterTreeView;

	private final ControllerView<DataState> controllerView;

	private InputFunction<Selection> onSelectionChanged = null;
	private Runnable onGraphDirectionChanged = null;
	public static final String title = "visual Miner";

	public GoalDrivenPanel(GoalDrivenConfiguration configuration, ProMCanceller canceller) {
		super(configuration.getDecorator());
		IvMDecoratorI decorator = configuration.getDecorator();

		int gridy = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();

		double size[][] = { { 0.37 * sWidth, 0.37 * sWidth, 0.26 * sWidth },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		double controllerBarSize[][] = { { 0.9 * sWidth, 0.1 * sWidth },
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

		contentPanel.setLayout(new TableLayout(size));

		layeredPanel.add(contentPanel, new Integer(0));
		contentPanel.setBounds(0, 0, 2000, 1000);
		layeredPanel.add(configCards, new Integer(1), 0);

		add(layeredPanel, "0, 1, 2, 1");
		//controls the margin on the left side of the settings panel
		Border leftBorder = new EmptyBorder(0, 2, 0, 0);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		sidePanel = new JPanel();
		sidePanel.setBorder(blackline);

		contentPanel.add(sidePanel, "2, 1");

		//other settings
		{
			JPanel otherSettingsPanel = new JPanel();
			sidePanel.add(otherSettingsPanel, BorderLayout.PAGE_START);
			otherSettingsPanel.setOpaque(false);
			otherSettingsPanel.setLayout(new GridBagLayout());
			// classifier
			{
				classifierLabel = new JLabel("Classifier");
				classifierLabel.setVisible(false);
				decorator.decorate(getClassifierLabel());
				classifierLabel.setBorder(leftBorder);
				GridBagConstraints cClassifierLabel = new GridBagConstraints();
				cClassifierLabel.gridx = 0;
				cClassifierLabel.gridy = gridy;
				cClassifierLabel.gridwidth = 1;
				cClassifierLabel.anchor = GridBagConstraints.WEST;
				otherSettingsPanel.add(classifierLabel, cClassifierLabel);

				classifiersCombobox1 = new IvMClassifierChooser(null, null, false);
				classifiersCombobox1.setVisible(false);
				//				decorator.decorate(classifiersCombobox1.getMultiComboBox());
				//				classifiersCombobox1.setEnabled(false);
				GridBagConstraints cClassifiers = new GridBagConstraints();
				cClassifiers.gridx = 1;
				cClassifiers.gridy = gridy++;
				cClassifiers.gridwidth = 1;
				cClassifiers.insets = margins;
				cClassifiers.fill = GridBagConstraints.HORIZONTAL;
				otherSettingsPanel.add(classifiersCombobox1, cClassifiers);
			}

			// unique value

			//pre-mining filters
			{
				IvMFilterTreeView<IMTrace> preMiningTraceFilterView = new IvMFilterTreeView<IMTrace>("trace filters",
						decorator);
				IvMFilterTreeView<IMEvent> preMiningEventFilterView = new IvMFilterTreeView<IMEvent>("event filters",
						decorator);
				preMiningFilterTreeView = new IvMFilterTreeViews(this, "Pre-mining filter", preMiningTraceFilterView,
						preMiningEventFilterView);

				preMiningFiltersButton = new JButton("pre-mining filters");
				decorator.decorate(preMiningFiltersButton);
				GridBagConstraints cTraceViewButton = new GridBagConstraints();
				cTraceViewButton.gridx = 1;
				cTraceViewButton.gridy = gridy++;
				cTraceViewButton.gridwidth = 1;
				cTraceViewButton.insets = margins;
				cTraceViewButton.fill = GridBagConstraints.HORIZONTAL;
				//				otherSettingsPanel.add(preMiningFiltersButton, cTraceViewButton);
			}

			//miner

		}

		//graph panel
		{
			XLog log = null;
			graphPanel = new GoalDrivenDFG(log);

			graphPanel.setBorder(blackline);
			contentPanel.add(graphPanel, "0, 1");

		}

		{
			XLog log = null;
			graphPanel2 = new GoalDrivenDFG(log);
			
			graphPanel2.setBorder(blackline);
			contentPanel.add(graphPanel2, "1, 1");

		}

		//controller view
		{
			controllerView = new ControllerView<>(this);
		}

	}

//	public void makeNodeSelectable(final LocalDotNode dotNode, boolean select) {
//		dotNode.addSelectionListener(new DotElementSelectionListener() {
//			public void selected(DotElement element, SVGDiagram image) {
//				InductiveVisualMinerSelectionColourer.colourSelectedNode(image, dotNode, true);
//			}
//
//			public void deselected(DotElement element, SVGDiagram image) {
//				InductiveVisualMinerSelectionColourer.colourSelectedNode(image, dotNode, false);
//			}
//		});
//		if (select) {
//			graphPanel.select(dotNode);
//		}
//	}

//	public void makeEdgeSelectable(final LocalDotEdge dotEdge, boolean select) {
//		dotEdge.addSelectionListener(new DotElementSelectionListener() {
//			public void selected(DotElement element, SVGDiagram image) {
//				InductiveVisualMinerSelectionColourer.colourSelectedEdge(graphPanel.getSVG(), dotEdge, true);
//				graphPanel.repaint();
//
//			}
//
//			public void deselected(DotElement element, SVGDiagram image) {
//				InductiveVisualMinerSelectionColourer.colourSelectedEdge(graphPanel.getSVG(), dotEdge, false);
//				graphPanel.repaint();
//			}
//		});
//		if (select) {
//			graphPanel.select(dotEdge);
//		}
//	}

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

	public JPanel getSidePanel() {
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

	public IvMClassifierChooser getClassifiers1() {
		return classifiersCombobox1;
	}

	public JButton getPreMiningFiltersButton() {
		return preMiningFiltersButton;
	}

	public IvMFilterTreeViews getPreMiningFilterTreeView() {
		return preMiningFilterTreeView;
	}

	public void setOnSelectionChanged(InputFunction<Selection> onSelectionChanged) {
		this.onSelectionChanged = onSelectionChanged;
	}

	public void setOnGraphDirectionChanged(Runnable onGraphDirectionChanged) {
		this.onGraphDirectionChanged = onGraphDirectionChanged;
	}

	public void setOnAnimationEnabledChanged(AnimationEnabledChangedListener onAnimationEnabledChanged) {
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}

	public JLabel getClassifierLabel() {
		return classifierLabel;
	}

}