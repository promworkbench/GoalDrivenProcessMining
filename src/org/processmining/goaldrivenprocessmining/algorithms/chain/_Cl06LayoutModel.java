package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class _Cl06LayoutModel<C> extends DataChainLinkComputationAbstract<C> {

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
		return new IvMObject<?>[] { IvMObject.model, IvMObject.selected_visualisation_mode,
				IvMObject.selected_graph_user_settings, GoalDrivenObject.map_activity_category };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.graph_dot, IvMObject.graph_svg };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs,
			IvMCanceller canceller) throws Exception {
//		System.out.println("--- Cl06");
//		MapValueGroupObject mapValueGroup = inputs.get(GoalDrivenObject.map_value_group);
//		IvMModel model = inputs.get(IvMObject.model);
//		Mode mode = inputs.get(IvMObject.selected_visualisation_mode);
//		DotPanelUserSettings graphSettings = inputs.get(IvMObject.selected_graph_user_settings);
//
//		//compute dot
//		AlignedLogVisualisationData data = new AlignedLogVisualisationDataImplEmpty();
//		ProcessTreeVisualisationParameters parameters = mode.getParametersWithoutAlignments();
//
//		Triple<Dot, ProcessTreeVisualisationInfo, TraceViewEventColourMap> p;
//		if (model.isTree()) {
//			ProcessTreeVisualisation visualiser = new ProcessTreeVisualisation();
//			p = visualiser.fancy(model, data, parameters);
//		} else {
////			DfmVisualisation visualiser = new DfmVisualisation();
////			p = visualiser.fancy(model, data, parameters, mapValueGroup);
//		}
//		File t = new File("C:\\D\\data\\abc2");
//		p.getA().exportToFile(t);
//		//set the graph direction
//		graphSettings.applyToDot(p.getA());
//
//		//compute svg from dot
//		SVGDiagram diagram = DotPanel.dot2svg(p.getA());

		return new IvMObjectValues().//
				s(IvMObject.graph_dot, null).//
				s(IvMObject.graph_svg, null);
		//s(IvMObject.graph_visualisation_info, p.getB()); //disabled as to avoid confusion (not used)
	}

}
