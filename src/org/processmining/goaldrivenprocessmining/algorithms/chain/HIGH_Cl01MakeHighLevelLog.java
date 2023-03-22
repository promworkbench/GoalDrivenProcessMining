package org.processmining.goaldrivenprocessmining.algorithms.chain;

import java.util.Arrays;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.TupleNodeObject;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;

public class HIGH_Cl01MakeHighLevelLog<C> extends DataChainLinkComputationAbstract<C> {
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
		return new IvMObject<?>[] { GoalDrivenObject.selected_unique_values, GoalDrivenObject.full_xlog, };

	}

	public IvMObject<?>[] createOutputObjects() {
		// TODO Auto-generated method stub
		return new IvMObject<?>[] { 
			GoalDrivenObject.high_level_xlog,
			GoalDrivenObject.indirected_edges
			};
	}

	public IvMObjectValues execute(Object configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		System.out.println("--- HIGH_Cl01MakeHighLevelLog");
		XLog log = inputs.get(GoalDrivenObject.full_xlog);
		String selectedAttribute = log.getClassifiers().get(0).toString();
		/*********/
		AttributeClassifier[] sValues = inputs.get(GoalDrivenObject.selected_unique_values);
		String[] selectedValues = new String[sValues.length];
		for (int i = 0; i < sValues.length; i++) {
			selectedValues[i] = sValues[i].toString();
		}

		XAttributeMap aMap = log.getAttributes();
		XLogImpl newLog = new XLogImpl(aMap);
		newLog.getClassifiers().addAll(log.getClassifiers());
		IndirectedEdgeCarrierObject indirectedEdges = new IndirectedEdgeCarrierObject();
		for (XTrace tr : log) {
			XTraceImpl newTr = new XTraceImpl(aMap);
			for (int i = 0; i < tr.size(); i++) {
				XEvent ev = tr.get(i);
				String value = ev.getAttributes().get(selectedAttribute).toString();
				if (Arrays.asList(selectedValues).contains(value)) {
					if (i != 0) {
						XEvent evPrev = tr.get(i - 1);
						String valuePrev = evPrev.getAttributes().get(selectedAttribute).toString();
						if (!Arrays.asList(selectedValues).contains(valuePrev)) {
							if (newTr.size() == 0) {
								TupleNodeObject obj = new TupleNodeObject("begin", value);
								if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
									indirectedEdges.addEdge(obj);
								}
								
							} else {
								TupleNodeObject obj = new TupleNodeObject(
										newTr.get(newTr.size() - 1).getAttributes().get(selectedAttribute).toString(),
										value);
								if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
									indirectedEdges.addEdge(obj);
								}
							}
						}
					}

					newTr.add(ev);
				} else {
					if (i == tr.size() - 1) {
						TupleNodeObject obj = new TupleNodeObject(
								newTr.get(newTr.size() - 1).getAttributes().get(selectedAttribute).toString(), "end");
						if (!indirectedEdges.getListIndirectedEdge().contains(obj)) {
							indirectedEdges.addEdge(obj);
						}
					}
				}
			}
			if (!newTr.isEmpty()) {
				newLog.add(newTr);
			}

		}
		System.out.println(indirectedEdges);

		return new IvMObjectValues().//
				s(GoalDrivenObject.high_level_xlog, newLog). 
				s(GoalDrivenObject.indirected_edges, indirectedEdges);//
	}
}
