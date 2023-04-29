package org.processmining.goaldrivenprocessmining.graphHelper;

import org.processmining.plugins.graphviz.dot.Dot;

public class GoalDrivenDot extends Dot {
	
	public GoalDrivenDot() {
		setOption("rankdir", "TD");
		setOption("compound", "true");
		
		setNodeOption("fontname", "Helvetica,Arial");
		setNodeOption("style", "filled,setlinewidth(8),rounded");
		setNodeOption("margin", "0.28");
		
	}
	
}
