package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.algorithms.StatUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;


public class LOW_MakeLowLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLogSkeleton currentLowLogSkeleton = null;

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
		return new IvMObject<?>[] { 
			GoalDrivenObject.full_log_skeleton,
			GoalDrivenObject.selected_source_target_node, 
			GoalDrivenObject.config 
			};
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { 
			GoalDrivenObject.low_level_log_skeleton
			};
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- LOW_Cl01MakeLowLevelLog");

		HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
		String source = (String) passValues.get("source");
		String target = (String) passValues.get("target");
		
		Config config = inputs.get(GoalDrivenObject.config);
		GDPMLogSkeleton fullLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
		GDPMLogSkeleton newLogSkeleton = (GDPMLogSkeleton) fullLogSkeleton.clone();

		// compute for updated combined low level log
		// restrict to source and target
		newLogSkeleton = LogSkeletonUtils.restrictLogFrom2Activities(newLogSkeleton, source, target);

		// apply filter

//		// apply group
//		List<GroupActObject> groups = config.getListGroupActObjects();
//		for (GroupActObject groupActObject : groups) {
//			newLogSkeleton = LogSkeletonUtils.replaceSetActivitiesInLog(newLogSkeleton, groupActObject.getListAct(),
//					groupActObject.getGroupName());
//		}
		// apply selected activities
		List<String> listUnselectedActivities = new ArrayList<String>();
		for (String act: config.getSelectedActs()) {
			if (!act.equals(source) && !act.equals(target))
			listUnselectedActivities.add(act);
		}
		GDPMLogSkeleton newLogObject = LogSkeletonUtils.removeActivitiesInLog(newLogSkeleton, listUnselectedActivities);
		// apply category

		// recalculate the stat of new log
		StatUtils.updateStat(newLogObject);
		// update to current global var
		currentLowLogSkeleton = newLogObject;
		return new IvMObjectValues().//
				s(GoalDrivenObject.low_level_log_skeleton, newLogObject);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		String[] unselectedValues = inputs.get(GoalDrivenObject.config).getUnselectedActs();
//
//		String selectedAttribute = log.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
//		XAttributeMap aMap = log.getAttributes();
//		XLogImpl newLog = new XLogImpl(aMap);
//		newLog.getClassifiers().addAll(log.getClassifiers());
//		XTraceImpl newTr = new XTraceImpl(aMap);
//		for (XTrace tr : log) {
//			Boolean sourceFound = source.replaceAll(" ", "").equals("") ? true : false;
//			for (XEvent ev : tr) {
//				String value = ev.getAttributes().get(selectedAttribute).toString();
//				if (!sourceFound) {
//					if (value.equals(source)) {
//						sourceFound = true;
//						newTr.add(ev);
//					}
//				} else {
//					if (value.equals(target)) {
//						newTr.add(ev);
//						newLog.add(newTr);
//						newTr = new XTraceImpl(aMap);
//						if (value.equals(source)) {
//							sourceFound = true;
//							newTr.add(ev);
//						} else {
//							sourceFound = false;
//						}
//					} else {
//						if (Arrays.asList(unselectedValues).contains(value)) {
//							newTr.add(ev);
//						}
//
//					}
//
//				}
//
//			}
//			if (!newTr.isEmpty() && target.replaceAll(" ", "").equals("")) {
//				newLog.add(newTr);
//				newTr = new XTraceImpl(aMap);
//			} else {
//				newTr = new XTraceImpl(aMap);
//			}
//
//		}
//		_GDPMLog gdpmLog = new _GDPMLog(newLog);
//		return new IvMObjectValues().//
//				s(GoalDrivenObject.low_level_log, gdpmLog);
//	}
	}
}
