package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityIndexMapper;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapGroupLogObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapStatObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.goaldrivenprocessmining.objectHelper._GDPMLog;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;

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
	public static final IvMObject<String> selected_node = c("selected node", String.class);
	public static final IvMObject<StatNodeObject> stat_selected_node = c("stat of the selected node",
			StatNodeObject.class);
	// im log in high level
	public static final IvMObject<AttributeClassifier[]> all_unique_values = c("all unique values",
			AttributeClassifier[].class);

	// act config 
	public static final IvMObject<CategoryObject> new_category = c("New added category", CategoryObject.class);
	public static final IvMObject<MapActivityCategoryObject> map_activity_category = c(
			"Mapping of activities to new categories", MapActivityCategoryObject.class);

	public static final IvMObject<XLog> full_xlog = c("full log", XLog.class);
	public static final IvMObject<_GDPMLog> high_level_log = c("high-level event log", _GDPMLog.class);
	public static final IvMObject<GoalDrivenDFG> high_level_dfg = c("high-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<_GDPMLog> low_level_log = c("low-level log", _GDPMLog.class);
	public static final IvMObject<GoalDrivenDFG> low_level_dfg = c("low-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<CategoryObject> selected_mode_category = c("selected mode of view for new category",
			CategoryObject.class);
	public static final IvMObject<IndirectedEdgeCarrierObject> indirected_edges = c(
			"indirected edges in the high-level dfg", IndirectedEdgeCarrierObject.class);
	public static final IvMObject<FrequencyEdgeObject> low_frequency_edge = c("Frequency of edges in low-level log",
			FrequencyEdgeObject.class);
	public static final IvMObject<FrequencyNodeObject> low_frequency_node = c("Frequency of nodes in low-level log",
			FrequencyNodeObject.class);
	public static final IvMObject<GroupActObject> new_group = c("Batch selected nodes from GUI",
			GroupActObject.class);
	public static final IvMObject<_GDPMLog> after_grouping_high_level_log = c("high-level log after grouping nodes",
			_GDPMLog.class);
	public static final IvMObject<MapGroupLogObject> map_group_log = c("mapping from group to log",
			MapGroupLogObject.class);
	public static final IvMObject<String> selected_group = c("selected group name from high-level dfg", String.class);
	//Stat
	public static final IvMObject<MapStatObject> stat = c("stat of a log", MapStatObject.class);
	//Config
	public static final IvMObject<Config> config = c("current config of the app", Config.class);
	public static final IvMObject<UpdateConfig> update_config_object = c("current config of the app", UpdateConfig.class);
	public static final IvMObject<ActivityIndexMapper> act_index_mapper = c("map from index to act name", ActivityIndexMapper.class);
	//Log skeleton
	public static final IvMObject<GDPMLogSkeleton> full_log_skeleton = c("log skeleton of the full log", GDPMLogSkeleton.class);
	public static final IvMObject<GDPMLogSkeleton> high_level_log_skeleton = c("log skeleton of the high-level log", GDPMLogSkeleton.class);
	public static final IvMObject<GDPMLogSkeleton> low_level_log_skeleton = c("log skeleton of the low-level log", GDPMLogSkeleton.class);
	
	
	
	
	
	
	public GoalDrivenObject(String name, Class<C> clazz) {
		super(name, clazz);
	}

}
