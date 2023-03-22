package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanelUserSettings;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationDataImplEmpty;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.mode.Mode;
import org.processmining.plugins.inductiveVisualMiner.traceview.TraceViewEventColourMap;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

public class _Cl066LayoutModelEdges<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "layout model";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Layouting model..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] {  IvMObject.selected_visualisation_mode,
				IvMObject.selected_graph_user_settings, GoalDrivenObject.map_activity_category };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] {  };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
//		IvMModel model = inputs.get(GoalDrivenObject.model_edge);
		MapActivityCategoryObject mapValueGroup = inputs.get(GoalDrivenObject.map_activity_category);
		Mode mode = inputs.get(IvMObject.selected_visualisation_mode);
		DotPanelUserSettings graphSettings = inputs.get(IvMObject.selected_graph_user_settings);

		//compute dot
		AlignedLogVisualisationData data = new AlignedLogVisualisationDataImplEmpty();
		ProcessTreeVisualisationParameters parameters = mode.getParametersWithoutAlignments();

		Triple<Dot, ProcessTreeVisualisationInfo, TraceViewEventColourMap> p;
//		if (model.isTree()) {
//			ProcessTreeVisualisation visualiser = new ProcessTreeVisualisation();
//			p = visualiser.fancy(model, data, parameters);
//		} else {
//			DfmVisualisation visualiser = new DfmVisualisation();
//			p = visualiser.fancy(model, data, parameters, mapValueGroup);
//		}
//
//		//set the graph direction
//		graphSettings.applyToDot(p.getA());
//
//		//compute svg from dot
//		SVGDiagram diagram = DotPanel.dot2svg(p.getA());

		return new IvMObjectValues();//
//				s(GoalDrivenObject.graph_dot_edge, p.getA()).//
//				s(GoalDrivenObject.graph_svg_edge, diagram);
		//s(IvMObject.graph_visualisation_info, p.getB()); //disabled as to avoid confusion (not used)
	}

}
