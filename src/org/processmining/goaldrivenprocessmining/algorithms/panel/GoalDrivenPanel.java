package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.plugins.InductiveMiner.BoundsPopupMenuListener;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.visualisation.listeners.DotElementSelectionListener;
import org.processmining.plugins.graphviz.visualisation.listeners.GraphChangedListener;
import org.processmining.plugins.graphviz.visualisation.listeners.SelectionChangedListener;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerAnimationPanel;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerSelectionColourer;
import org.processmining.plugins.inductiveVisualMiner.Selection;
import org.processmining.plugins.inductiveVisualMiner.animation.AnimationEnabledChangedListener;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.InputFunction;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMPanel;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeView;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeViews;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotEdge;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotNode;
import org.processmining.plugins.inductiveminer2.logs.IMEvent;
import org.processmining.plugins.inductiveminer2.logs.IMTrace;

import com.kitfox.svg.SVGDiagram;

import info.clearthought.layout.TableLayout;

public class GoalDrivenPanel extends IvMPanel {

	private static final long serialVersionUID = -1078786029763735572L;

	private static final int sidePanelWidth = 260;
	private static final int lineHeight = 20;

	private static final Insets margins = new Insets(2, 0, 0, 0);

	//gui elements
	private final InductiveVisualMinerAnimationPanel graphPanel;
	private final InductiveVisualMinerAnimationPanel graphPanel2;
	private final JLabel classifierLabel;
	private final JLabel valueLabel;
	private IvMUniqueValueChooser valuesCombobox;
	private IvMClassifierChooser classifiersCombobox1;
	private final JButton preMiningFiltersButton;
	private final IvMFilterTreeViews preMiningFilterTreeView;

	private final JLabel minerLabel;
	private JComboBox<?> minerCombobox;
	private final ControllerView<DataState> controllerView;

	private InputFunction<Selection> onSelectionChanged = null;
	private Runnable onGraphDirectionChanged = null;
	private AnimationEnabledChangedListener onAnimationEnabledChanged = null;


	public static final String title = "visual Miner";

	public GoalDrivenPanel(GoalDrivenConfiguration configuration, ProMCanceller canceller) {
		super(configuration.getDecorator());
		System.out.println("Go to panel");
		IvMDecoratorI decorator = configuration.getDecorator();

		int gridy = 0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();
		double sHeight = screenSize.getHeight();
		double size[][] = { { 0.16 * sWidth, 0.42 * sWidth, 0.42 * sWidth }, { sHeight } };
		setLayout(new TableLayout(size));

		setOpaque(false);

		//controls the margin on the left side of the settings panel
		Border leftBorder = new EmptyBorder(0, 2, 0, 0);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		JPanel sidePanel = new JPanel();
		sidePanel.setBorder(blackline);
		add(sidePanel, "0, 0");

		//other settings
		{
			JPanel otherSettingsPanel = new JPanel();
			sidePanel.add(otherSettingsPanel, BorderLayout.PAGE_START);
			otherSettingsPanel.setOpaque(false);
			otherSettingsPanel.setLayout(new GridBagLayout());
			// classifier
			{
				classifierLabel = new JLabel("Classifier");
				decorator.decorate(getClassifierLabel());
				classifierLabel.setBorder(leftBorder);
				GridBagConstraints cClassifierLabel = new GridBagConstraints();
				cClassifierLabel.gridx = 0;
				cClassifierLabel.gridy = gridy;
				cClassifierLabel.gridwidth = 1;
				cClassifierLabel.anchor = GridBagConstraints.WEST;
				otherSettingsPanel.add(classifierLabel, cClassifierLabel);

				classifiersCombobox1 = new IvMClassifierChooser(null, null, false);
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
			{
				valueLabel = new JLabel("Values");
				decorator.decorate(getClassifierLabel());
				valueLabel.setBorder(leftBorder);
				GridBagConstraints cClassifierLabel = new GridBagConstraints();
				cClassifierLabel.gridx = 0;
				cClassifierLabel.gridy = gridy;
				cClassifierLabel.gridwidth = 1;
				cClassifierLabel.anchor = GridBagConstraints.WEST;
				otherSettingsPanel.add(valueLabel, cClassifierLabel);

				valuesCombobox = new IvMUniqueValueChooser(null, "");
				decorator.decorate(valuesCombobox.getMultiComboBox());
				valuesCombobox.setEnabled(false);
				GridBagConstraints cClassifiers = new GridBagConstraints();
				cClassifiers.gridx = 1;
				cClassifiers.gridy = gridy++;
				cClassifiers.gridwidth = 1;
				cClassifiers.insets = margins;
				cClassifiers.fill = GridBagConstraints.HORIZONTAL;
				otherSettingsPanel.add(valuesCombobox, cClassifiers);
			}


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
			{
				minerLabel = new JLabel("Miner");
				decorator.decorate(getMinerLabel());
				getMinerLabel().setBorder(leftBorder);
				GridBagConstraints cMinerLabel = new GridBagConstraints();
				cMinerLabel.gridx = 0;
				cMinerLabel.gridy = gridy;
				cMinerLabel.gridwidth = 1;
				cMinerLabel.anchor = GridBagConstraints.WEST;
				otherSettingsPanel.add(getMinerLabel(), cMinerLabel);

				minerCombobox = new JComboBox<>(configuration.getDiscoveryTechniquesArray());
				decorator.decorate(minerCombobox);
				minerCombobox.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
				minerCombobox.setFocusable(false);
				GridBagConstraints cMiners = new GridBagConstraints();
				cMiners.gridx = 1;
				cMiners.gridy = gridy++;
				cMiners.gridwidth = 1;
				cMiners.insets = margins;
				cMiners.fill = GridBagConstraints.HORIZONTAL;
				otherSettingsPanel.add(minerCombobox, cMiners);
			}

		}

		//graph panel
		{
			graphPanel = new InductiveVisualMinerAnimationPanel(canceller, decorator);
			//			graphPanel.setFocusable(true);

			//set the graph changed listener
			//if we are initialised, the dotPanel should not update the layout, as we have to recompute the animation
			graphPanel.addGraphChangedListener(new GraphChangedListener() {

				public void graphChanged(GraphChangedReason reason, Object newState) {
					System.out.println("graph change listener");
					onGraphDirectionChanged.run();
				}
			});

			//set the node selection change listener
//			graphPanel.addSelectionChangedListener(new SelectionChangedListener<DotElement>() {
//				public void selectionChanged(Set<DotElement> selectedElements) {
//					//selection of nodes changed; keep track of them
//					Selection selection = new Selection();
//					for (DotElement dotElement : graphPanel.getSelectedElements()) {
//						LocalDotEdge e = (LocalDotEdge) dotElement;
//						System.out.println(e.getSource().getLabel());
//						System.out.println(e.getTarget().getLabel());
//						selection.select(dotElement);
//					}
//
//					if (onSelectionChanged != null) {
//						try {
//							onSelectionChanged.call(selection);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//
//					graphPanel.repaint();
//				}
//			});
			graphPanel.setBorder(blackline);
			add(graphPanel, "1, 0");

		}
		{
			graphPanel2 = new InductiveVisualMinerAnimationPanel(canceller, decorator);
			graphPanel2.setFocusable(true);

			//set the graph changed listener
			//if we are initialised, the dotPanel should not update the layout, as we have to recompute the animation
			graphPanel2.addGraphChangedListener(new GraphChangedListener() {

				public void graphChanged(GraphChangedReason reason, Object newState) {
					onGraphDirectionChanged.run();
				}
			});

			//set the node selection change listener
			graphPanel2.addSelectionChangedListener(new SelectionChangedListener<DotElement>() {
				public void selectionChanged(Set<DotElement> selectedElements) {
					//selection of nodes changed; keep track of them

					Selection selection = new Selection();
					for (DotElement dotElement : graphPanel2.getSelectedElements()) {
						
						selection.select(dotElement);
					}

					if (onSelectionChanged != null) {
						try {
							onSelectionChanged.call(selection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					graphPanel2.repaint();
				}
			});
			graphPanel2.setBorder(blackline);
			add(graphPanel2, "2, 0");

		}

		//controller view
		{
			controllerView = new ControllerView<>(this);
			graphPanel.getHelperControlsShortcuts().add("ctrl o");
			graphPanel.getHelperControlsExplanations().add("show controller");
		}

		//cost model
		{
			graphPanel.getHelperControlsShortcuts().add("ctrl c");
			graphPanel.getHelperControlsExplanations().add("change cost model");
		}
	}

	public void removeNotify() {
		super.removeNotify();
		//		for (SideWindow sideWindow : getSideWindows()) {
		//			sideWindow.setVisible(false);
		//		}
		graphPanel.pause();
	}

	public void makeNodeSelectable(final LocalDotNode dotNode, boolean select) {
		dotNode.addSelectionListener(new DotElementSelectionListener() {
			public void selected(DotElement element, SVGDiagram image) {
				InductiveVisualMinerSelectionColourer.colourSelectedNode(image, dotNode, true);
			}

			public void deselected(DotElement element, SVGDiagram image) {
				InductiveVisualMinerSelectionColourer.colourSelectedNode(image, dotNode, false);
			}
		});
		if (select) {
			graphPanel.select(dotNode);
		}
	}

	public void makeEdgeSelectable(final LocalDotEdge dotEdge, boolean select) {
		dotEdge.addSelectionListener(new DotElementSelectionListener() {
			public void selected(DotElement element, SVGDiagram image) {
				InductiveVisualMinerSelectionColourer.colourSelectedEdge(graphPanel.getSVG(), dotEdge, true);
				graphPanel.repaint();
				

			}

			public void deselected(DotElement element, SVGDiagram image) {
				InductiveVisualMinerSelectionColourer.colourSelectedEdge(graphPanel.getSVG(), dotEdge, false);
				graphPanel.repaint();
			}
		});
		if (select) {
			graphPanel.select(dotEdge);
		}
	}

	public InductiveVisualMinerAnimationPanel getGraph() {
		return graphPanel;
	}
	
	public InductiveVisualMinerAnimationPanel getGraph2() {
		return graphPanel2;
	}

	public JComboBox<?> getMinerSelection() {
		return minerCombobox;
	}

	
	public IvMClassifierChooser getClassifiers1() {
		return classifiersCombobox1;
	}
	
	public IvMUniqueValueChooser getUniqueValues() {
		return valuesCombobox;
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
		this.onAnimationEnabledChanged = onAnimationEnabledChanged;
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}

	public JLabel getMinerLabel() {
		return minerLabel;
	}

	public JLabel getClassifierLabel() {
		return classifierLabel;
	}

}