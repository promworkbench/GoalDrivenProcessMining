package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
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

		GDPMLogSkeleton newGdpmLog = new GDPMLogSkeleton();
		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");

		Config config = CONFIG_Update.currentConfig;

		Map<EdgeObject, Map<EdgeObject, Map<Integer, List<Integer[]>>>> mapEdgeChildEdge = config.getMapEdgeChildEdge();

		// Find the edge match the source and target
		Map<EdgeObject, Map<Integer, List<Integer[]>>> children = new HashMap<>();
		for (Map.Entry<EdgeObject, Map<EdgeObject, Map<Integer, List<Integer[]>>>> entry : mapEdgeChildEdge
				.entrySet()) {
			EdgeObject key = entry.getKey();
			if (key.getNode1().equals(source) && key.getNode2().equals(target)) {
				children = entry.getValue();
				break;
			}
		}

		// add children to the edge table
		if (!children.isEmpty()) {
			for (EdgeObject edgeObject : children.keySet()) {
				this.addEdgeToEdgeHashTable(newGdpmLog, edgeObject, children, source, target);
			}
		}
		// check if in original edge hash table there is such edge
		for (EdgeObject edgeObject : Cl01GatherAttributes.originalEdgeHashTable.getEdgeTable().keySet()) {
			if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
				this.addEdgeToEdgeHashTable(newGdpmLog, edgeObject,
						Cl01GatherAttributes.originalEdgeHashTable.getEdgeTable(), source, target);
			}
		}
		// Create simple activity hash table
		ActivityHashTable activityHashTable = new ActivityHashTable();
		for (EdgeObject edgeObject : newGdpmLog.getEdgeHashTable().getEdgeTable().keySet()) {
			String act1 = edgeObject.getNode1();
			String act2 = edgeObject.getNode2();

			Map<Integer, List<Integer[]>> allTracePos = newGdpmLog.getEdgeHashTable().getEdgeTable().get(edgeObject);
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
		newGdpmLog.setActivityHashTable(activityHashTable);

		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, newGdpmLog);

	}

	private void addEdgeToEdgeHashTable(GDPMLogSkeleton gdpmLogSkeleton, EdgeObject edgeObject,
			Map<EdgeObject, Map<Integer, List<Integer[]>>> mapEdgePos, String source, String target) {
		Map<Integer, List<Integer[]>> allPos = mapEdgePos.get(edgeObject);
		String node1 = edgeObject.getNode1();
		String node2 = edgeObject.getNode2();
		if (node1.equals(source)) {
			if (!node1.equals("begin")) {
				for (Integer i : allPos.keySet()) {
					List<Integer[]> posInTrace = mapEdgePos.get(edgeObject).get(i);
					for (Integer[] pos : posInTrace) {
						EdgeObject newEdgeObject = new EdgeObject("begin", node1);
						gdpmLogSkeleton.getEdgeHashTable().addEdge(newEdgeObject, i, -1, pos[0]);
					}

				}
			}

		}
		if (node2.equals(target)) {
			if (!node2.equals("end")) {
				for (Integer i : allPos.keySet()) {
					List<Integer[]> posInTrace = mapEdgePos.get(edgeObject).get(i);
					for (Integer[] pos : posInTrace) {
						EdgeObject newEdgeObject = new EdgeObject(node2, "end");
						gdpmLogSkeleton.getEdgeHashTable().addEdge(newEdgeObject, i, pos[1], -2);
					}

				}
			}

		}
		gdpmLogSkeleton.getEdgeHashTable().addEdge(edgeObject, allPos);
	}

}
