package org.processmining.goaldrivenprocessmining.algorithms.chain;

public class HIGH_GROUP_Cl04MakeGroupedLog<C>  {
//	private MapGroupLogObject mapGroupLogObject = null;
//	private XLog currentLog = null;
//
//	public String getName() {
//		return "Make the log for the selected nodes";
//	}
//
//	@Override
//	public String getStatusBusyMessage() {
//		return "Making the log for the selected nodes";
//	}
//
//	@Override
//	public IvMObject<?>[] createInputObjects() {
//		return new IvMObject<?>[] { GoalDrivenObject.config, GoalDrivenObject.new_group,
//				GoalDrivenObject.high_level_log_skeleton };
//	}
//
//	@Override
//	public IvMObject<?>[] createOutputObjects() {
//		return new IvMObject<?>[] { GoalDrivenObject.map_group_log };
//	}
//
//	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
//		System.out.println("--- HIGH_GROUP_Cl04MakeGroupedLog");
//		GroupActObject groupActObject = inputs.get(GoalDrivenObject.new_group);
//		GDPMLogSkeleton highLogSkeleton = (GDPMLogSkeleton) inputs.get(GoalDrivenObject.high_level_log_skeleton).clone();
//		List<String> toRemoveAct = new ArrayList<String>();
//		for (String act: highLogSkeleton.getActivityHashTable().getActivityTable().keySet()) {
//			if (!groupActObject.getListAct().contains(act)) {
//				toRemoveAct.add(act);
//			}
//		}
//		GDPMLogSkeleton newGroupLogSkeleton = LogSkeletonUtils.removeActivitiesInLog(highLogSkeleton, toRemoveAct);
//		
//		StatUtils.updateStat(newGroupLogSkeleton);
//		
//		
//		GoalDrivenDFG dfg = new GoalDrivenDFG(newGroupLogSkeleton);
//		GraphObjectClickControl edgeClickControl = new GraphObjectClickControl(
//				((GoalDrivenConfiguration) configuration).getChain());
//		dfg.setEdgeClickControl(edgeClickControl);
//		dfg.addControlListener(edgeClickControl);
//		GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
//				((GoalDrivenConfiguration) configuration).getChain());
//		dfg.setGroupNodeControl(groupNodeControl);
//		dfg.addControlListener(groupNodeControl);
//		if (this.mapGroupLogObject == null) {
//			this.mapGroupLogObject = new MapGroupLogObject();
//		}
//		this.mapGroupLogObject.getMapGroupLog().put(groupActObject.getGroupName(), newGroupLogSkeleton);
//		this.mapGroupLogObject.getMapGroupDfg().put(inputs.get(GoalDrivenObject.new_group).getGroupName(), dfg);
//		XLog log = inputs.get(GoalDrivenObject.high_level_log).getLog();
//		GroupActObject groupActObject = inputs.get(GoalDrivenObject.new_group);
//		ActivityHashTable actHashTable = inputs.get(GoalDrivenObject.act_hash_table);
//		
//		_GDPMLog gdpmLog = LogSkeletonUtils.removeActivitiesInLog(log, actHashTable, groupActObject.getListAct());
//		LogSkeletonUtils.setUpMapNodeType(gdpmLog, Arrays.asList(""));
//		
//		XLog newLog = gdpmLog.getLog();
//		FrequencyEdgeObject frequencyEdge = LogSkeletonUtils.getFrequencyEdges(newLog,
//				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
//		FrequencyNodeObject frequencyNode = LogSkeletonUtils.getFrequencyNodeObject(newLog,
//				newLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString());
//		GoalDrivenDFG dfg = new GoalDrivenDFG(gdpmLog, frequencyEdge, frequencyNode);
//		GraphObjectClickControl edgeClickControl = new GraphObjectClickControl(
//				((GoalDrivenConfiguration) configuration).getChain());
//		dfg.setEdgeClickControl(edgeClickControl);
//		dfg.addControlListener(edgeClickControl);
//		GroupNodeControl groupNodeControl = new GroupNodeControl(dfg.getGraph().getNodeTable(),
//				((GoalDrivenConfiguration) configuration).getChain());
//		dfg.setGroupNodeControl(groupNodeControl);
//		dfg.addControlListener(groupNodeControl);
//		if (this.mapGroupLogObject == null) {
//			this.mapGroupLogObject = new MapGroupLogObject();
//		}
//		this.mapGroupLogObject.getMapGroupLog().put(groupActObject.getGroupName(), gdpmLog);
//		this.mapGroupLogObject.getMapGroupDfg().put(groupActObject.getGroupName(), dfg);

//		return new IvMObjectValues().//
//				s(GoalDrivenObject.map_group_log, this.mapGroupLogObject);
//	}
}
