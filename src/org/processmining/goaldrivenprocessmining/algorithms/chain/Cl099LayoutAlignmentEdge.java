package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.algorithms.DfmVisualisation;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.MapValueGroupObject;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.DotPanelUserSettings;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationDataImplFrequencies;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.mode.Mode;
import org.processmining.plugins.inductiveVisualMiner.traceview.TraceViewEventColourMap;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisation;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

import com.kitfox.svg.SVGDiagram;

/**
 * This class is to be instantiated once for every mode.
 * 
 * @author sander
 *
 */
public class Cl099LayoutAlignmentEdge extends DataChainLinkComputationAbstract<GoalDrivenConfiguration> {

	private final GoalDrivenConfiguration configuration;

	public Cl099LayoutAlignmentEdge(GoalDrivenConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getName() {
		return "layout alignment";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Layouting aligned model..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.model_edge, GoalDrivenObject.aligned_log_info_edge, IvMObject.selected_visualisation_mode,
				IvMObject.selected_graph_user_settings, GoalDrivenObject.map_value_group };
	}

//	@Override
//	public IvMObject<?>[] createNonTriggerObjects() {
//		return Mode.gatherInputsRequested(configuration);
//	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.graph_dot_aligned_edge, GoalDrivenObject.graph_svg_aligned_edge,
				GoalDrivenObject.graph_visualisation_info_aligned_edge };
	}

	@Override
	public IvMObjectValues execute(GoalDrivenConfiguration configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
		IvMModel model = inputs.get(GoalDrivenObject.model_edge);
		MapValueGroupObject mapValueGroup = inputs.get(GoalDrivenObject.map_value_group);
		IvMLogInfo logInfo = inputs.get(GoalDrivenObject.aligned_log_info_edge);
		Mode mode = inputs.get(IvMObject.selected_visualisation_mode);
		DotPanelUserSettings settings = inputs.get(IvMObject.selected_graph_user_settings);

		IvMObjectValues modeInputs = inputs.getIfPresent(mode.getOptionalObjects());
		ProcessTreeVisualisationParameters visualisationParameters = mode
				.getVisualisationParametersWithAlignments(modeInputs);

		//compute dot
		AlignedLogVisualisationData data = new AlignedLogVisualisationDataImplFrequencies(model, logInfo);
		Triple<Dot, ProcessTreeVisualisationInfo, TraceViewEventColourMap> p;
		if (model.isTree()) {
			ProcessTreeVisualisation visualiser = new ProcessTreeVisualisation();
			p = visualiser.fancy(model, data, visualisationParameters);
		} else {
			DfmVisualisation visualiser = new DfmVisualisation();
			p = visualiser.fancy(model, data, visualisationParameters, mapValueGroup);
		}

		//keep the user settings of the dot panel
//		settings.applyToDot(p.getA());

		//compute svg from dot
		SVGDiagram diagram = DotPanel.dot2svg(p.getA());

		//		//update the selection
		//		if (!mode.isShowDeviations()) {
		//			selection = new Selection(selection.getSelectedActivities(), new THashSet<LogMovePosition>(),
		//					new TIntHashSet(10, 0.5f, -1), selection.getSelectedTaus());
		//		}

		return new IvMObjectValues().//
				s(GoalDrivenObject.graph_dot_aligned_edge, p.getA()).//
				s(GoalDrivenObject.graph_svg_aligned_edge, diagram).//
				s(GoalDrivenObject.graph_visualisation_info_aligned_edge, p.getB());
//		return new IvMObjectValues().//
//				s(IvMObject.graph_dot_aligned_edge, p.getA()).//
//				s(IvMObject.graph_svg_aligned_edge, diagram).//
//				s(IvMObject.graph_visualisation_info_aligned_edge, p.getB());
	}
}