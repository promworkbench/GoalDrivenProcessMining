package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.Arrays;
import java.util.Set;

import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.export.IvMExporter;

import gnu.trove.set.hash.THashSet;

public class GoalDrivenExportController {
	public static <C> void initialise(final DataChain<C> chain, final GoalDrivenConfiguration configuration,
			GoalDrivenPanel panel) {

		//compile a list of input objects
		final IvMObject<?>[] objects;
		{
			Set<IvMObject<?>> set = new THashSet<>();
			for (IvMExporter exporter : configuration.getExporters()) {
				set.addAll(Arrays.asList(exporter.getInputObjects()));
				set.addAll(Arrays.asList(exporter.getTriggerObjects()));
			}
			objects = new IvMObject<?>[set.size()];
			set.toArray(objects);
		}

		//add animation and statistics to export
//		panel.getGraph().setGetExporters(new GetExporters() {
//			public List<Exporter> getExporters(List<Exporter> exporters) {
//				//this method is called whenever the uses clicks on the export button
//
//				IvMObjectValues inputs = null;
//				try {
//					inputs = chain.getObjectValues(objects).get();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					return null;
//				}
//
//				//for each exporter, see whether its requirements are met
//				for (IvMExporter exporter : configuration.getExporters()) {
//					if (inputs.has(exporter.getInputObjects())) {
//						IvMObjectValues subInputs = inputs.getIfPresent(exporter.getInputObjects(),
//								exporter.getTriggerObjects());
//						exporter.setInputs(subInputs);
//						exporters.add(exporter);
//					}
//				}
//
//				return exporters;
//			}
//		});
	}
}
