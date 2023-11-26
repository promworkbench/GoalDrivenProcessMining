package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;

import graph.GoalDrivenDFG;

public class GoalDrivenObject<C> extends IvMObject<C> {
	public static final IvMObject<HashMap> selected_source_target_node = c("selected source, target node",
			HashMap.class);

	public static final IvMObject<AttributeClassifier[]> classifier_for_gui1 = c("classifier for gui 1",
			AttributeClassifier[].class);
	public static final IvMObject<AttributeClassifier> selected_classifier1 = c("selected classifier1",
			AttributeClassifier.class);
	public static final IvMObject<AttributeClassifier[]> selected_unique_values = c("selected unique values",
			AttributeClassifier[].class);
	public static final IvMObject<AttributeClassifier[]> unselected_unique_values = c("unselected unique values",
			AttributeClassifier[].class);
	public static final IvMObject<String> selected_node = c("selected node", String.class);
	public static final IvMObject<ThroughputTimeObject> stat_selected_node = c("stat of the selected node",
			ThroughputTimeObject.class);
	// im log in high level
	public static final IvMObject<AttributeClassifier[]> all_unique_values = c("all unique values",
			AttributeClassifier[].class);

	// act config 
	public static final IvMObject<CategoryObject> new_category = c("New added category", CategoryObject.class);
	public static final IvMObject<MapActivityCategoryObject> map_activity_category = c(
			"Mapping of activities to new categories", MapActivityCategoryObject.class);

	public static final IvMObject<XLog> full_xlog = c("full log", XLog.class);
	
	public static final IvMObject<CategoryObject> selected_mode_category = c("selected mode of view for new category",
			CategoryObject.class);
	//Config
	public static final IvMObject<Config> config = c("current config of the app", Config.class);
	public static final IvMObject<UpdateConfig> update_config_object = c("current config of the app",
			UpdateConfig.class);
	//Log skeleton
	public static final IvMObject<GDPMLogSkeleton> full_log_skeleton = c("log skeleton of the full log",
			GDPMLogSkeleton.class);
	public static final IvMObject<GDPMLogSkeleton> high_level_log_skeleton = c("log skeleton of the high-level log",
			GDPMLogSkeleton.class);
	public static final IvMObject<GDPMLogSkeleton> low_level_log_skeleton = c("log skeleton of the low-level log",
			GDPMLogSkeleton.class);
	public static final IvMObject<GDPMLogSkeleton> selected_group_log_skeleton = c("log skeleton of a selected group",
			GDPMLogSkeleton.class);
	// DFG
	public static final IvMObject<GoalDrivenDFG> high_level_dfg = c("high-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<GoalDrivenDFG> low_level_dfg = c("low-level DFG", GoalDrivenDFG.class);
	public static final IvMObject<GoalDrivenDFG> selected_group_dfg = c("DFG of the selected group", GoalDrivenDFG.class);
	// selected group
	public static final IvMObject<String> selected_act = c("Selected act from UI", String.class);
	
	// selected mode 
	public static final IvMObject<String> selected_mode = c("Selected mode from UI", String.class);
	public static final IvMObject<String> selected_additional_mode = c("Selected additional mode from UI", String.class);
	public static final IvMObject<String[]> high_desire_acts = c("High desire act", String[].class);
	public static final IvMObject<String[]> low_desire_acts = c("Low desire act", String[].class);
	public static final IvMObject<String[]> high_priority_acts = c("High priority act", String[].class);
	public static final IvMObject<String[]> low_priority_acts = c("Low priority act", String[].class);
	
	
	
	public GoalDrivenObject(String name, Class<C> clazz) {
		super(name, clazz);
	}

}
