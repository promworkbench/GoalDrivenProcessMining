package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupActObject;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapGroupLogObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapStatObject;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
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
	public static final IvMObject<GDPMLog> high_level_log = c("high-level event log", GDPMLog.class);
	public static final IvMObject<GoalDrivenDFG> high_level_dfg = c("high-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<GDPMLog> low_level_log = c("low-level log", GDPMLog.class);
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
	public static final IvMObject<GroupActObject> new_group = c("Batch selected nodes from GUI",
			GroupActObject.class);
	public static final IvMObject<GDPMLog> after_grouping_high_level_log = c("high-level log after grouping nodes",
			GDPMLog.class);
	public static final IvMObject<Boolean> is_in_group_mode = c("if users are choosing the grouping mode",
			Boolean.class);
	public static final IvMObject<MapGroupLogObject> map_group_log = c("mapping from group to log",
			MapGroupLogObject.class);
	public static final IvMObject<String> selected_group = c("selected group name from high-level dfg", String.class);
	//hash table
	public static final IvMObject<ActivityHashTable> act_hash_table = c("hash table of activities", ActivityHashTable.class);
	//Stat
	public static final IvMObject<MapStatObject> stat = c("stat of a log", MapStatObject.class);
	//Config
	public static final IvMObject<Config> config = c("current config of the app", Config.class);
	public static final IvMObject<UpdateConfig> update_config_object = c("current config of the app", UpdateConfig.class);
	
	public GoalDrivenObject(String name, Class<C> clazz) {
		super(name, clazz);
	}

}
