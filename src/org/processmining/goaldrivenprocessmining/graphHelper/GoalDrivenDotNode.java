package org.processmining.goaldrivenprocessmining.graphHelper;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.inductiveVisualMiner.visualisation.LocalDotNode;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;

public class GoalDrivenDotNode extends LocalDotNode {
	
	

	public GoalDrivenDotNode(Dot dot, ProcessTreeVisualisationInfo info, NodeType type, String label, int unode,
			LocalDotNode correspondingSplit) {
		super(dot, info, type, label, unode, correspondingSplit);

		switch (type) {
			case activity :
				setOption("shape", "box");
				setOption("style", "rounded,filled,setlinewidth(8)");
				setOption("fontsize", "12");
				break;
			case logMoveActivity :
				setOption("shape", "box");
				setOption("style", "rounded,filled");
				setOption("fontsize", "9");
				setOption("fillcolor", "red");
				break;
			case concurrentSplit :
			case concurrentJoin :
			case orSplit :
			case orJoin :
			case interleavedSplit :
			case interleavedJoin :
				setOption("shape", "diamond");
				setOption("fixedsize", "true");
				setOption("height", "0.25");
				setOption("width", "0.27");
				break;
			case sink :
				setOption("width", "0.2");
				setOption("shape", "circle");
				setOption("style", "filled");
				setOption("fillcolor", "#E40000");
				break;
			case source :
				setOption("width", "0.2");
				setOption("shape", "circle");
				setOption("style", "filled");
				setOption("fillcolor", "#80ff00");
				break;
			case xor :
				setOption("width", "0.05");
				setOption("shape", "circle");
				break;
		}

		dot.addNode(this);
		if (info != null) {
			info.addNode(unode, this, correspondingSplit);
		}
	}

}
