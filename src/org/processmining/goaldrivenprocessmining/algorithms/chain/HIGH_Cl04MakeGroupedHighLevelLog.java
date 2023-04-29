package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.List;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl04MakeGroupedHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {

	public String getStatusBusyMessage() {
		// TODO Auto-generated method stub
		return " log after select nodes from GUI...";
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "log after select nodes from GUI";
	}

	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { GoalDrivenObject.batch_selected_nodes, GoalDrivenObject.high_level_xlog,
				GoalDrivenObject.indirected_edges };
	}

	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] {GoalDrivenObject.grouped_high_level_xlog};
	}

	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		System.out.println("--- HIGH_Cl04MakeGroupedHighLevelLog ---");
		List<String> selectedNodes = inputs.get(GoalDrivenObject.batch_selected_nodes).getListNodeLabel();
		String groupName = inputs.get(GoalDrivenObject.batch_selected_nodes).getGroupName();
		XLog currentLog = inputs.get(GoalDrivenObject.high_level_xlog);
		List<EdgeObject> indirectedEdges = inputs.get(GoalDrivenObject.indirected_edges).getListIndirectedEdge();
		String classifier = currentLog.getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
		XAttributeMap aMap = currentLog.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		newLog.getClassifiers().addAll(currentLog.getClassifiers());
		XEvent groupNodeEvent = new XEventImpl();
		groupNodeEvent.getAttributes().put(classifier, new XAttributeLiteralImpl(classifier, groupName));
		for (XTrace tr : currentLog) {
			XTraceImpl newTr = new XTraceImpl(tr.getAttributes());
			for (int i = 0; i < tr.size(); i++) {
				XEvent ev = tr.get(i);
				String evValue = ev.getAttributes().get(classifier).toString();
				if (selectedNodes.contains(evValue)) {
					newTr.add(groupNodeEvent);
					if (i != 0) {
						XEvent evPrev = tr.get(i - 1);
						String valuePrev = evPrev.getAttributes().get(classifier).toString();
						if (indirectedEdges.contains(new EdgeObject(valuePrev, evValue))) {
							indirectedEdges.add(new EdgeObject(valuePrev, groupName));
						}
					}
				} else {
					newTr.add(ev);
				}
			}
			if (!newTr.isEmpty()) {
				newLog.add(newTr);
			}
		}
		for (XTrace tr: newLog) {
			for (XEvent ev: tr) {
				System.out.println(ev.getAttributes().get(classifier));
			}
		}

		return new IvMObjectValues().//
				s(GoalDrivenObject.grouped_high_level_xlog, newLog);
	}

}
