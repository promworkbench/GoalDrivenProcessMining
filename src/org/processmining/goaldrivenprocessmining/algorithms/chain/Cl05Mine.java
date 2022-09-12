package org.processmining.goaldrivenprocessmining.algorithms.chain;

import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.inductiveVisualMiner.Selection;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerParameters;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;

public class Cl05Mine<C> extends DataChainLinkComputationAbstract<C> {

	@Override
	public String getName() {
		return "mine";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Mining..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.imlog_activity_filtered, IvMObject.imlog_info_activity_filtered,
				IvMObject.selected_miner, IvMObject.selected_noise_threshold };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { IvMObject.model, IvMObject.selected_model_selection };
	}

	@Override
	public IvMObjectValues execute(C configuration, IvMObjectValues inputs, IvMCanceller canceller) throws Exception {
		IMLog log = inputs.get(IvMObject.imlog_activity_filtered);
		IMLogInfo logInfo = inputs.get(IvMObject.imlog_info_activity_filtered);
		VisualMinerWrapper miner = inputs.get(IvMObject.selected_miner);
		double noise_threshold = inputs.get(IvMObject.selected_noise_threshold);

		VisualMinerParameters minerParameters = new VisualMinerParameters(noise_threshold);

		IvMModel model = miner.mine(log, logInfo, minerParameters, canceller);
		Selection selection = new Selection();

		return new IvMObjectValues().//
				s(IvMObject.model, model).//
				s(IvMObject.selected_model_selection, selection);
	}
}
