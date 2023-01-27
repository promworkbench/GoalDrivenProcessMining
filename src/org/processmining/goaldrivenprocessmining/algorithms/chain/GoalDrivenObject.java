package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapValueGroupObject;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogNotFiltered;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;

import com.google.gwt.dev.util.collect.HashMap;
import com.kitfox.svg.SVGDiagram;

public class GoalDrivenObject<C> extends IvMObject<C> {
	public static final IvMObject<HashMap> selected_source_target_node = c("selected source, target node", 
			HashMap.class);
	public static final IvMObject<IvMModel> model_edge = c("model after selected edge", IvMModel.class);
	public static final IvMObject<Dot> graph_dot_edge = c("graph dot edge", Dot.class);
	public static final IvMObject<SVGDiagram> graph_svg_edge = c("graph svg edge", SVGDiagram.class);
	public static final IvMObject<IvMLogNotFiltered> aligned_log_edge = c("aligned log edge", IvMLogNotFiltered.class);
	public static final IvMObject<IvMLogInfo> aligned_log_info_edge = c("aligned log info edge", IvMLogInfo.class);
	public static final IvMObject<ProcessTreeVisualisationInfo> graph_visualisation_info_aligned_edge = c(
			"graph visualisation info aligned", ProcessTreeVisualisationInfo.class);
	public static final IvMObject<Dot> graph_dot_aligned_edge = c("graph dot aligned edge", Dot.class);
	public static final IvMObject<SVGDiagram> graph_svg_aligned_edge = c("graph svg aligned edge", SVGDiagram.class);
	public static final IvMObject<XLogInfo> xlog_info_edge = c("xlog info edge", XLogInfo.class);
	public static final IvMObject<XLogInfo> xlog_info_performance_edge = c("xlog info performance edge", XLogInfo.class);
	public static final IvMObject<XLog> log_edge = c("log edge", XLog.class);
	public static final IvMObject<AttributeClassifier[]> classifiers1 = c("classifiers", AttributeClassifier[].class);
	
	
	public static final IvMObject<AttributeClassifier[]> unique_values = c("unique values",
			AttributeClassifier[].class);
	public static final IvMObject<AttributeClassifier[]> classifier_for_gui1 = c("classifier for gui 1",
			AttributeClassifier[].class);
	public static final IvMObject<AttributeClassifier> selected_classifier1 = c("selected classifier1",
			AttributeClassifier.class);
	public static final IvMObject<AttributeClassifier[]> selected_unique_values = c("selected unique values",
			AttributeClassifier[].class);
	public static final IvMObject<AttributeClassifier[]> unselected_unique_values = c("unselected unique values",
			AttributeClassifier[].class);
	
	// im log in high level
	public static final IvMObject<IMLog> im_log_high_level = c("IM log for high level", IMLog.class);
	public static final IvMObject<IMLog> im_log_info_high_level = c("IM log info for high level", IMLog.class);
	public static final IvMObject<AttributeClassifier[]> all_unique_values = c("all unique values", AttributeClassifier[].class);
	
	// act config 
	public static final IvMObject<GroupActObject[]> all_group_act = c("IM log for high level", GroupActObject[].class);
	public static final IvMObject<MapValueGroupObject> map_value_group = c("IM log for high level", MapValueGroupObject.class);
	
	
	public GoalDrivenObject(String name, Class<C> clazz) {
		super(name, clazz);
	}

}
