package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
					TraceSkeleton trace = fullLogSkeleton.getLog().get(entry.getKey());

					for (Integer[] pos : entry.getValue()) {
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
	}

}
