package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapEdgeStrokeWidth;
import org.processmining.goaldrivenprocessmining.objectHelper.MapNodeFillColor;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedNodeGroupObject;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;

import com.google.gwt.dev.util.collect.HashMap;

import graph.GoalDrivenDFG;

public class GoalDrivenObject<C> extends IvMObject<C> {
	public static final IvMObject<HashMap> selected_source_target_node = c("selected source, target node",
			HashMap.class);

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
	public static final IvMObject<AttributeClassifier[]> all_unique_values = c("all unique values",
			AttributeClassifier[].class);

	// act config 
	public static final IvMObject<CategoryObject> new_category = c("New added category", CategoryObject.class);
	public static final IvMObject<MapActivityCategoryObject> map_activity_category = c(
			"Mapping of activities to new categories", MapActivityCategoryObject.class);

	public static final IvMObject<XLog> full_xlog = c("full log", XLog.class);
	public static final IvMObject<XLog> high_level_xlog = c("high-level event log", XLog.class);
	public static final IvMObject<GoalDrivenDFG> high_level_dfg = c("high-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<XLog> low_level_xlog = c("low-level log", XLog.class);
	public static final IvMObject<GoalDrivenDFG> low_level_dfg = c("low-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<CategoryObject> selected_mode_category = c("selected mode of view for new category",
			CategoryObject.class);
	public static final IvMObject<IndirectedEdgeCarrierObject> indirected_edges = c(
			"indirected edges in the high-level dfg", IndirectedEdgeCarrierObject.class);
	public static final IvMObject<FrequencyEdgeObject> high_frequency_edge = c("Frequency of edges in high-level log",
			FrequencyEdgeObject.class);
	public static final IvMObject<FrequencyNodeObject> high_frequency_node = c("Frequency of nodes in high-level log",
			FrequencyNodeObject.class);
	public static final IvMObject<FrequencyEdgeObject> low_frequency_edge = c("Frequency of edges in low-level log",
			FrequencyEdgeObject.class);
	public static final IvMObject<FrequencyNodeObject> low_frequency_node = c("Frequency of nodes in low-level log",
			FrequencyNodeObject.class);
	public static final IvMObject<MapNodeFillColor> map_node_fill_color = c("Mapping for node fill color",
			MapNodeFillColor.class);
	public static final IvMObject<MapEdgeStrokeWidth> map_edge_stroke_width = c("Mapping for edge stroke width",
			MapEdgeStrokeWidth.class);
	public static final IvMObject<SelectedNodeGroupObject> batch_selected_nodes = c("Batch selected nodes from GUI", SelectedNodeGroupObject.class);
	public static final IvMObject<XLog> grouped_high_level_xlog = c("high-level log after grouping nodes", XLog.class);
//	public static final IvMObject<GoalDrivenDFG> grouped_high_level_dfg = c("high-level DFG after grouping nodes",
//			GoalDrivenDFG.class);

	public GoalDrivenObject(String name, Class<C> clazz) {
		super(name, clazz);
	}

}
