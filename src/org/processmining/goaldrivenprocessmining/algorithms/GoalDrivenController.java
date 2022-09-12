package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.IMTrace;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.visualisation.listeners.SelectionChangedListener;
import org.processmining.plugins.inductiveVisualMiner.Selection;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkGuiAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.UserStatus;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeController;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.view.IvMFilterTreeView;
import org.processmining.plugins.inductiveVisualMiner.mode.Mode;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePaths;
import org.processmining.plugins.inductiveVisualMiner.traceview.TraceViewEventColourMap;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.Miner;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotEdge;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotNode;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;

import com.google.gwt.dev.util.collect.HashMap;
import com.kitfox.svg.SVGDiagram;

import gnu.trove.set.hash.THashSet;

public class GoalDrivenController {
	private final GoalDrivenPanel panel;
	private final GoalDrivenConfiguration configuration;
	private final DataChain<GoalDrivenConfiguration> chain;
	private final PluginContext context;
	private final UserStatus userStatus;
	public GoalDrivenController(final PluginContext context,
			final GoalDrivenConfiguration configuration, final XLog log, final ProMCanceller canceller) {

		this.configuration = configuration;
		this.panel = configuration.getPanel();
		this.userStatus = new UserStatus();
		this.context = context;
		chain = configuration.getChain();

		//initialise gui handlers
		initGui(canceller, configuration);

		//set up the controller view
		chain.setOnChange(new Runnable() {
			public void run() {
				panel.getControllerView().pushCompleteChainLinks(chain);
			}
		});

		//set up exception handling
//		chain.setOnException(new OnException() {
//			public void onException(Exception e) {
//				setStatus("- error - aborted -", 0);
//			}
//		});
//
//		//set up status handling
//		chain.setOnStatus(new OnStatus<GoalDrivenConfiguration>() {
//			public void startComputation(final DataChainLinkComputation<GoalDrivenConfiguration> chainLink) {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						setStatus(chainLink.getStatusBusyMessage(), chainLink.hashCode());
//					}
//				});
//			}
//
//			public void endComputation(final DataChainLinkComputation<GoalDrivenConfiguration> chainLink) {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						setStatus(null, chainLink.hashCode());
//					}
//				});
//			}
//		});

		//start the chain
		chain.setFixedObject(IvMObject.input_log, log);
	}
	
	
	protected void initGui(final ProMCanceller canceller, final GoalDrivenConfiguration configuration) {

		initGuiClassifiers1();

		initGuiUniqueValue();

		initGuiMiner();

		GoalDrivenExportController.initialise(chain, configuration, panel);

		//graph direction changed
		panel.setOnGraphDirectionChanged(new Runnable() {
			public void run() {
				chain.setObject(IvMObject.selected_graph_user_settings, panel.getGraph().getUserSettings());
			}
		});
		panel.getGraph().getUserSettings().setDirection(GraphDirection.leftRight);
		setObject(IvMObject.selected_graph_user_settings, panel.getGraph().getUserSettings());

		//set pre-mining filters button
		initGuiPreMiningFilters(configuration.getDecorator());

		//set graph handlers
		initGuiGraph();
		initGuiGraph1();

	}

	protected void initGuiMiner() {
		//miner
		setObject(IvMObject.selected_miner, new Miner());
		panel.getMinerSelection().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chain.setObject(IvMObject.selected_miner,
						(VisualMinerWrapper) panel.getMinerSelection().getSelectedItem());
			}
		});

		//noise threshold
		setObject(IvMObject.selected_noise_threshold, 1.0);
		chain.setObject(IvMObject.selected_noise_threshold, 1.0);
		//		panel.getPathsSlider().addChangeListener(new ChangeListener() {
		//			public void stateChanged(ChangeEvent e) {
		//				if (!panel.getPathsSlider().getSlider().getValueIsAdjusting()) {
		//					chain.setObject(IvMObject.selected_noise_threshold, panel.getPathsSlider().getValue());
		//				}
		//			}
		//		});

		//model-related buttons
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "enable model-related buttons";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.model };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				IvMModel model = inputs.get(IvMObject.model);

				//				panel.getSaveModelButton().setEnabled(true);
				//				panel.getEditModelView().setModel(model);
			}

			public void invalidate(GoalDrivenPanel panel) {
				//				panel.getSaveModelButton().setEnabled(false);
				//				panel.getEditModelView().setMessage("Mining tree...");
			}
		});
	}

	protected void initGuiGraph() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "model dot";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.graph_dot, IvMObject.graph_svg };
			}

			public IvMObject<?>[] createOptionalObjects() {
				return new IvMObject<?>[] { IvMObject.graph_dot_aligned, IvMObject.graph_svg_aligned };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				System.out.println("update graph gui");
				if (inputs.has(IvMObject.graph_svg_aligned) && inputs.has(IvMObject.graph_dot_aligned)) {
					Dot dot = inputs.get(IvMObject.graph_dot_aligned);
					SVGDiagram svg = inputs.get(IvMObject.graph_svg_aligned);
					panel.getGraph().changeDot(dot, svg, true);
				} else {
					Dot dot = inputs.get(IvMObject.graph_dot);
					SVGDiagram svg = inputs.get(IvMObject.graph_svg);
					panel.getGraph().changeDot(dot, svg, true);
				}
			}

			public void invalidate(GoalDrivenPanel panel) {
				//here, we could put the graph on blank, but that is annoying
				//				Dot dot = new Dot();
				//				DotNode dotNode = dot.addNode("...");
				//				dotNode.setOption("shape", "plaintext");
				//				panel.getGraph().changeDot(dot, true);
			}
		});

		//mode switch
		setObject(IvMObject.selected_visualisation_mode, new ModePaths());

		//register the requirements of the modes
		initGuiMode();

		//trace view event colour map & model node selection
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "trace view event colour map";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.graph_visualisation_info_aligned };
			}

			public IvMObject<?>[] createNonTriggerObjects() {
				return new IvMObject<?>[] { IvMObject.selected_model_selection };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				/**
				 * We don't want to be triggered by a change in selection, so we
				 * get it as a non-trigger. This is a bit risky, as we have to
				 * assume it is always available.
				 */
				if (inputs.has(IvMObject.selected_model_selection)) {
					Selection selection = inputs.get(IvMObject.selected_model_selection);
					ProcessTreeVisualisationInfo visualisationInfo = inputs
							.get(IvMObject.graph_visualisation_info_aligned);
					makeElementsSelectable(visualisationInfo, panel, selection);
				}
			}

			public void invalidate(GoalDrivenPanel panel) {
				// TODO no action taken?
			}
		});

		panel.getGraph().addSelectionChangedListener(new SelectionChangedListener<DotElement>() {
			public void selectionChanged(Set<DotElement> selectedElements) {
				//selection of nodes changed; keep track of them
				Selection selection = new Selection();
				for (DotElement dotElement : panel.getGraph().getSelectedElements()) {
					LocalDotEdge e = (LocalDotEdge) dotElement;
					String sourceNode = e.getSource().getLabel().split("&")[0];
					String targetNode = e.getTarget().getLabel().split("&")[0];
					selection.select(dotElement);
					System.out.println("graph listener");
					AttributeClassifier[] unique_values = panel.getUniqueValues().getSelectedClassifier();
					HashMap<String, Object> passValues = new HashMap<String, Object>();
					passValues.put("source", sourceNode);
					passValues.put("target", targetNode);
					passValues.put("unique_values", unique_values);					
					chain.setObject(GoalDrivenObject.selected_source_target_node, passValues);

				}

				//				if (onSelectionChanged != null) {
				//					try {
				//						onSelectionChanged.call(selection);
				//					} catch (Exception e) {
				//						e.printStackTrace();
				//					}
				//				}

				panel.getGraph().repaint();
			}
		});
	}

	protected void initGuiGraph1() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "edge dfg model dot";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.graph_dot_edge, GoalDrivenObject.graph_svg_edge };
			}

			public IvMObject<?>[] createOptionalObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.graph_dot_aligned_edge, GoalDrivenObject.graph_svg_aligned_edge };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				System.out.println("update graph gui edge");
				if (inputs.has(GoalDrivenObject.graph_svg_aligned_edge) && inputs.has(GoalDrivenObject.graph_dot_aligned_edge)) {
					System.out.println("yes");
					Dot dot = inputs.get(GoalDrivenObject.graph_dot_aligned_edge);
					SVGDiagram svg = inputs.get(GoalDrivenObject.graph_svg_aligned_edge);
					panel.getGraph2().changeDot(dot, svg, true);
				} else {
					System.out.println("no");
					Dot dot = inputs.get(GoalDrivenObject.graph_dot_edge);
					SVGDiagram svg = inputs.get(GoalDrivenObject.graph_svg_edge);
					panel.getGraph2().changeDot(dot, svg, true);

				}
			}

			public void invalidate(GoalDrivenPanel panel) {
				//here, we could put the graph on blank, but that is annoying
				//				Dot dot = new Dot();
				//				DotNode dotNode = dot.addNode("...");
				//				dotNode.setOption("shape", "plaintext");
				//				panel.getGraph().changeDot(dot, true);
			}
		});

		//mode switch

	}

	protected void initGuiClassifiers1() {
		//get the selected classifier to the gui
		System.out.println("init gui classifier");
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "classifier to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.classifier_for_gui1 };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] value = inputs.get(GoalDrivenObject.classifier_for_gui1);
				//				panel.getClassifiers1().getMultiComboBox().setSelectedItems(value);
				panel.getClassifiers1().getMultiComboBox().setSelectedItem(value[0]);

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

		//update data on classifiers
		panel.getClassifiers1().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("init gui classifier 1 - action listener");
				chain.setObject(GoalDrivenObject.selected_classifier1, panel.getClassifiers1().getSelectedClassifier()[0]);
			}
		});

		//update classifiers on data
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "set classifiers 1";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.classifiers1 };
			}

			public void invalidate(GoalDrivenPanel panel) {
				panel.getClassifiers1().setEnabled(false);
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] classifiers = inputs.get(GoalDrivenObject.classifiers1);
				panel.getClassifiers1().setEnabled(true);
				panel.getClassifiers1().replaceClassifiers(classifiers);
			}
		});
	}

	protected void initGuiUniqueValue() {
		//get the selected classifier to the gui
		System.out.println("init gui unique value");
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.unique_values_for_gui };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] selected_values = inputs.get(GoalDrivenObject.unique_values_for_gui);
				panel.getUniqueValues().getMultiComboBox().setSelectedItems(selected_values);

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

		//update data on classifiers
		panel.getUniqueValues().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("init gui unique value - action listener");
				chain.setObject(GoalDrivenObject.selected_unique_values, panel.getUniqueValues().getSelectedClassifier());
			}
		});

		//update classifiers on data
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "set unique values";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.unique_values };
			}

			public void invalidate(GoalDrivenPanel panel) {
				panel.getUniqueValues().setEnabled(false);
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] classifiers = inputs.get(GoalDrivenObject.unique_values);
				panel.getUniqueValues().setEnabled(true);
				panel.getUniqueValues().replaceClassifiers(classifiers);
			}
		});
	}

	protected void initGuiPreMiningFilters(IvMDecoratorI decorator) {
		setObject(IvMObject.selected_activities_threshold, 1.0);
		chain.setObject(IvMObject.selected_activities_threshold, 1.0);

		@SuppressWarnings("unchecked")
		IvMFilterTreeView<IMTrace> traceView = (IvMFilterTreeView<IMTrace>) panel.getPreMiningFilterTreeView()
				.getView(0);
		@SuppressWarnings("unchecked")
		IvMFilterTreeView<XEvent> eventView = (IvMFilterTreeView<XEvent>) panel.getPreMiningFilterTreeView().getView(1);
		final IvMFilterTreeController<IMTrace> preMiningFiltersTraceController = new IvMFilterTreeController<IMTrace>(
				"These filters alter the traces on which a model is discovered. "
						+ "Deviations, animation and performance are computed on the full (unfiltered) log.",
				IMTrace.class, traceView, configuration.getFilters(), decorator);
		final IvMFilterTreeController<XEvent> preMiningFiltersEventController = new IvMFilterTreeController<XEvent>(
				"These filters alter the events on which a model is discovered. "
						+ "Deviations, animation and performance are computed on the full (unfiltered) log.",
				XEvent.class, eventView, configuration.getFilters(), decorator);
		preMiningFiltersTraceController.setOnUpdate(new Runnable() {
			public void run() {
				setObject(IvMObject.pre_mining_filter_tree_trace, preMiningFiltersTraceController.getCurrentFilter());
			}
		});
		preMiningFiltersEventController.setOnUpdate(new Runnable() {
			public void run() {
				setObject(IvMObject.pre_mining_filter_tree_event, preMiningFiltersEventController.getCurrentFilter());
			}
		});
		setObject(IvMObject.pre_mining_filter_tree_trace, preMiningFiltersTraceController.getCurrentFilter());
		setObject(IvMObject.pre_mining_filter_tree_event, preMiningFiltersEventController.getCurrentFilter());

		panel.getPreMiningFiltersButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getPreMiningFilterTreeView().enableAndShow();
			}
		});

		//initialise filters
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "initialise pre-mining filters";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.attributes_info };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributesInfo attributesInfo = inputs.get(IvMObject.attributes_info);

				preMiningFiltersTraceController.setAttributesInfo(attributesInfo);
				preMiningFiltersEventController.setAttributesInfo(attributesInfo);
			}

			public void invalidate(GoalDrivenPanel panel) {
				preMiningFiltersTraceController.setAttributesInfo(null);
				preMiningFiltersEventController.setAttributesInfo(null);
			}
		});
	}

	public static void makeElementsSelectable(ProcessTreeVisualisationInfo info, GoalDrivenPanel panel,
			Selection selection) {
		for (LocalDotNode dotNode : info.getAllActivityNodes()) {
			panel.makeNodeSelectable(dotNode, selection.isSelected(dotNode));
		}
		for (LocalDotEdge logMoveEdge : info.getAllLogMoveEdges()) {
			panel.makeEdgeSelectable(logMoveEdge, selection.isSelected(logMoveEdge));
		}
		for (LocalDotEdge modelMoveEdge : info.getAllModelMoveEdges()) {
			panel.makeEdgeSelectable(modelMoveEdge, selection.isSelected(modelMoveEdge));
		}
		for (LocalDotEdge edge : info.getAllModelEdges()) {
			panel.makeEdgeSelectable(edge, selection.isSelected(edge));
		}
	}

	protected void initGuiMode() {
		//TODO: now if there is one mode with a certain trigger, then all modes will update with that trigger.

		//repaint on availability of visualisation data
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "repaint after visualisation data";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.visualisation_data };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				panel.getGraph().repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				panel.getGraph().repaint();
			}
		});

		//create model visualisation data
		chain.register(new DataChainLinkComputationAbstract<GoalDrivenConfiguration>() {
			public String getName() {
				return "visualisation data";
			}

			public String getStatusBusyMessage() {
				return "Visualising on model";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.selected_visualisation_mode, IvMObject.model,
						IvMObject.aligned_log_info_filtered };
			}

			public IvMObject<?>[] getOptionalObjects() {
				Set<IvMObject<?>> result = new THashSet<>();
				for (Mode mode : configuration.getModes()) {
					result.addAll(Arrays.asList(mode.getVisualisationDataOptionalObjects()));
				}

				IvMObject<?>[] arr = new IvMObject<?>[result.size()];
				return result.toArray(arr);
			}

			public IvMObject<?>[] createOutputObjects() {
				return new IvMObject<?>[] { IvMObject.visualisation_data };
			}

			public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
					IvMCanceller canceller) throws Exception {
				Mode mode = inputs.get(IvMObject.selected_visualisation_mode);

				IvMObjectValues subInputs = inputs.getIfPresent(mode.getVisualisationDataOptionalObjects());
				AlignedLogVisualisationData visualisationData = mode.getVisualisationData(subInputs);

				return new IvMObjectValues().//
						s(IvMObject.visualisation_data, visualisationData);
			}
		});

		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "set trace view colour map";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.model };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				IvMModel model = inputs.get(IvMObject.model);

				TraceViewEventColourMap traceViewEventColourMap = new TraceViewEventColourMap(model);
				//				panel.getTraceView().setEventColourMap(traceViewEventColourMap);
			}

			public void invalidate(GoalDrivenPanel panel) {
				//				panel.getTraceView().setEventColourMap(null);
			}
		});
	}

	/**
	 * Sets the status message of number. The status message stays in view until
	 * it is reset using NULL for that number.
	 * 
	 * @param message
	 * @param number
	 */
//	public void setStatus(String message, int number) {
//		userStatus.setStatus(message, number);
//		panel.getStatusLabel().setText(userStatus.getText());
//		panel.getStatusLabel().repaint();
//	}
	//

	private <C> void updateObjectInGui(final IvMObject<C> object, final C value, final boolean fixed) {
		if (object.equals(IvMObject.selected_miner)) {
			panel.getMinerSelection().setSelectedItem(value);
		} else if (object.equals(IvMObject.model) && fixed) {
			//			panel.getActivitiesSlider().setVisible(false);
			//			panel.getPathsSlider().setVisible(false);
			panel.getPreMiningFiltersButton().setVisible(false);
			panel.getMinerLabel().setVisible(false);
			panel.getMinerSelection().setVisible(false);
		} else if (object.equals(GoalDrivenObject.selected_classifier1) && fixed) {
			//			panel.getEditModelButton().setVisible(false);
			panel.getClassifierLabel().setVisible(false);
			panel.getClassifiers1().setVisible(false);
		} else if (object.equals(GoalDrivenObject.selected_classifier1) || object.equals(GoalDrivenObject.classifier_for_gui1)) {
			panel.getClassifiers1().getMultiComboBox().setSelectedItem(((AttributeClassifier[]) value)[0]);
		} else if (object.equals(IvMObject.selected_noise_threshold)) {
			//			panel.getPathsSlider().setValue((Double) value);
		} else if (object.equals(IvMObject.selected_activities_threshold)) {
			//			panel.getActivitiesSlider().setValue((Double) value);
		} else if (object.equals(IvMObject.selected_visualisation_mode)) {
			//			panel.getVisualisationModeSelector().setSelectedItem(value);
		} else if (object.equals(IvMObject.selected_animation_enabled)) {
			panel.getGraph().setAnimationEnabled((Boolean) value);
		}
	}

	public static void debug(Object s) {
		System.out.println(s);
	}

	public GoalDrivenPanel getPanel() {
		return panel;
	}

	public <C> void setObject(IvMObject<C> object, C value) {
		updateObjectInGui(object, value, false);
		chain.setObject(object, value);
	}

	public <C> void setFixedObject(IvMObject<C> object, C value) {
		System.out.println("set fix object " + value.toString());
		updateObjectInGui(object, value, true);
		chain.setFixedObject(object, value);
	}
}
