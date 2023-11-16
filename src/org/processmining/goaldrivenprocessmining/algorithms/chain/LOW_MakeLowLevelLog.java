package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class LOW_MakeLowLevelLog<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "Make low level log";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining low level log..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.selected_source_target_node };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.low_level_log_skeleton };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_MakeLowLevelLog");
		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");

		/*--------------------------*/

		/*--------------------------*/
		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		Config config = CONFIG_Update.currentConfig;
		List<GroupSkeleton> groups = config.getListGroupSkeletons();
		Map<EdgeObject, Map<EdgeObject, Map<Integer, List<Integer[]>>>> mapEdgeChildEdge = config.getMapEdgeChildEdge();

		List<String> listSources = new ArrayList<String>();
		List<String> listTargets = new ArrayList<String>();
		// check if source is a group
		for (GroupSkeleton groupSkeleton : groups) {
			if (groupSkeleton.getGroupName().equals(source)) {
				listSources.addAll(this.getAllActsInGroup(groupSkeleton));
			}
		}
		// otherwise
		if (listSources.isEmpty()) {
			listSources.add(source);
		}
		// check if target is a group
		for (GroupSkeleton groupSkeleton : groups) {
			if (groupSkeleton.getGroupName().equals(target)) {
				listTargets.addAll(this.getAllActsInGroup(groupSkeleton));
			}
		}
		// otherwise
		if (listTargets.isEmpty()) {
			listTargets.add(target);
		}
		for (String s : listSources) {
			for (String t : listTargets) {
				// create log based on the source and target
				this.createLog(newGdpmLog, inputs, s, t);
			}
		}

		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, newGdpmLog);

	}

	private List<String> getAllActsInGroup(GroupSkeleton groupSkeleton) {
		List<String> res = new ArrayList<>();
		res.addAll(groupSkeleton.getListAct());
		for (GroupSkeleton groupSkeleton2 : groupSkeleton.getListGroup()) {
			res.addAll(this.getAllActsInGroup(groupSkeleton2));
		}
		return res;
	}

	private void createLog(GDPMLogSkeleton newGdpmLog, IvMObjectValues inputs, String source, String target) {

		/*------------------*/
		GDPMLogSkeleton fullLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
		EdgeHashTable highLevelEdgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;
		EdgeHashTable newEdgeHashTable = new EdgeHashTable();
		Map<EdgeObject, Map<Integer, List<Integer[]>>> edgeTable = highLevelEdgeHashTable.getEdgeTable();
		for (EdgeObject edgeObject : edgeTable.keySet()) {
			if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
				Map<Integer, List<Integer[]>> mapCasePos = edgeTable.get(edgeObject);
				for (Map.Entry<Integer, List<Integer[]>> entry : mapCasePos.entrySet()) {
					for (Integer[] pos : entry.getValue()) {
						TraceSkeleton trace = fullLogSkeleton.getLog().get(entry.getKey());

						if (pos[0] == -1) {
							String sourceAct = "begin";
							String targetAct = trace.getTrace().get(0).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), -1, 0);
						} else {
							String sourceAct = "begin";
							String targetAct = trace.getTrace().get(pos[0]).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), -1, pos[0]);
						}
						if (pos[1] == -2) {
							String sourceAct = trace.getTrace().get(trace.getTrace().size() - 1).getActivity();
							String targetAct = "end";
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), trace.getTrace().size() - 1, -2);
						} else {
							String sourceAct = trace.getTrace().get(pos[1]).getActivity();
							String targetAct = "end";
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), pos[1], -2);
						}
						int startIndex = pos[0] == -1 ? 0 : pos[0];
						int targetIndex = pos[1] == -2 ? trace.getTrace().size() - 1 : pos[1];

						for (int index = startIndex; index < targetIndex; index++) {
							String sourceAct = trace.getTrace().get(index).getActivity();
							String targetAct = trace.getTrace().get(index + 1).getActivity();
							EdgeObject newEdgeObject = new EdgeObject(sourceAct, targetAct);
							newEdgeHashTable.addEdge(newEdgeObject, entry.getKey(), index, index + 1);
						}

					}
				}

				break;
			}
		}
		/*------------------*/
		newGdpmLog.setEdgeHashTable(newEdgeHashTable);
		// Create simple activity hash table
		ActivityHashTable activityHashTable = new ActivityHashTable();
		this.addActToActivityHashTable(newGdpmLog, activityHashTable);
		newGdpmLog.setActivityHashTable(activityHashTable);
	}


	private void addActToActivityHashTable(GDPMLogSkeleton gdpmLogSkeleton, ActivityHashTable activityHashTable) {
		for (EdgeObject edgeObject : gdpmLogSkeleton.getEdgeHashTable().getEdgeTable().keySet()) {
			String act1 = edgeObject.getNode1();
			String act2 = edgeObject.getNode2();

			Map<Integer, List<Integer[]>> allTracePos = gdpmLogSkeleton.getEdgeHashTable().getEdgeTable()
					.get(edgeObject);
			for (Map.Entry<Integer, List<Integer[]>> entry : allTracePos.entrySet()) {
				int trace = entry.getKey();
				List<Integer[]> listEdgePos = entry.getValue();
				List<Integer> sourceIndexes = new ArrayList<>();
				List<Integer> targetIndexes = new ArrayList<>();
				for (Integer[] edge : listEdgePos) {
					sourceIndexes.add(edge[0]);
					targetIndexes.add(edge[1]);
				}
				if (!act1.equals("begin")) {
					if (activityHashTable.getActivityTable().containsKey(act1)) {

						if (activityHashTable.getActivityTable().get(act1).containsKey(trace)) {
							List<Integer> actPos = activityHashTable.getActivityTable().get(act1).get(trace);
							for (Integer pos : sourceIndexes) {
								if (!actPos.contains(pos)) {
									actPos.add(pos);
								}
							}
							activityHashTable.getActivityTable().get(act1).replace(trace, actPos);
						} else {
							List<Integer> actPos = new ArrayList<>();
							actPos.addAll(sourceIndexes);
							activityHashTable.getActivityTable().get(act1).put(trace, actPos);
						}
					}

					else {
						Map<Integer, List<Integer>> mapPosTrace = new HashMap<>();
						mapPosTrace.put(trace, sourceIndexes);
						activityHashTable.getActivityTable().put(act1, mapPosTrace);
					}
				}
				if (!act2.equals("end")) {
					if (activityHashTable.getActivityTable().containsKey(act2)) {
						if (activityHashTable.getActivityTable().get(act2).containsKey(trace)) {
							List<Integer> actPos = activityHashTable.getActivityTable().get(act2).get(trace);
							for (Integer pos : targetIndexes) {
								if (!actPos.contains(pos)) {
									actPos.add(pos);
								}
							}
							activityHashTable.getActivityTable().get(act2).replace(trace, actPos);
						} else {
							List<Integer> actPos = new ArrayList<>();
							actPos.addAll(targetIndexes);
							activityHashTable.getActivityTable().get(act2).put(trace, actPos);
						}
					} else {
						Map<Integer, List<Integer>> mapPosTrace = new HashMap<>();
						mapPosTrace.put(trace, targetIndexes);
						activityHashTable.getActivityTable().put(act2, mapPosTrace);
					}
				}
			}
		}
	}
}
