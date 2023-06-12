package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.goaldrivenprocessmining.algorithms.LogUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl01MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
	public static GDPMLog currentHighLog = null;
	public static GDPMLog originalHighLog = null;
	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return "Making high level event log...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Make high level log";
	}

	public IvMObject<?>[] createInputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { GoalDrivenObject.full_xlog, GoalDrivenObject.selected_unique_values };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { 
			GoalDrivenObject.high_level_log
			};
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl01MakeHighLevelLog");
		// compute for updated combined high level log
		XLog curLog = currentHighLog != null ? currentHighLog.getLog() : inputs.get(GoalDrivenObject.full_xlog);
		GDPMLog curLogObj = this.getCurrentHighLog(curLog, inputs.get(GoalDrivenObject.selected_unique_values));
		// compute for updated original high level log
		XLog oriLog = inputs.get(GoalDrivenObject.full_xlog);
		GDPMLog oriLogObj = this.getCurrentHighLog(oriLog, inputs.get(GoalDrivenObject.selected_unique_values));

		currentHighLog = curLogObj;
		originalHighLog = oriLogObj;
		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_log, curLogObj);
	}
	private GDPMLog getCurrentHighLog(XLog log, AttributeClassifier[] sValues) {
		String[] selectedValues = new String[sValues.length];
		for (int i = 0; i < sValues.length; i++) {
			selectedValues[i] = sValues[i].toString();
		}
		GDPMLog gdpmLog = LogUtils.projectLogOnSetActivities(log, selectedValues);
		LogUtils.setUpMapNodeType(gdpmLog, Arrays.asList(""));
		return gdpmLog;
		
	}
	
}
