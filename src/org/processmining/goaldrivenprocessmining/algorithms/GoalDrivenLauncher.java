package org.processmining.goaldrivenprocessmining.algorithms;

import java.lang.ref.SoftReference;

import org.deckfour.xes.model.XLog;

public class GoalDrivenLauncher {
	public final SoftReference<XLog> xLog;

	private GoalDrivenLauncher(SoftReference<XLog> xLog) {
		this.xLog = xLog;
	}

	public static GoalDrivenLauncher launcher(XLog xLog) {
		return new GoalDrivenLauncher(new SoftReference<>(xLog));
	}

}
