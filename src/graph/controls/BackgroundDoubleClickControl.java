package graph.controls;

import java.awt.event.MouseEvent;

import graph.GoalDrivenDFG;
import graph.GoalDrivenDFGUtils;
import prefuse.controls.ControlAdapter;

public class BackgroundDoubleClickControl extends ControlAdapter {
	private GoalDrivenDFG goalDrivenDFG;

	public BackgroundDoubleClickControl(GoalDrivenDFG goalDrivenDFG) {
		this.goalDrivenDFG = goalDrivenDFG;
	}

	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 2 && e.isConsumed() == false) {
			// reset all nodes
			GoalDrivenDFGUtils.resetColorAndStroke(this.goalDrivenDFG);
		}
	}
}
