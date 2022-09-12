package org.processmining.goaldrivenprocessmining.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import javax.swing.JOptionPane;

import org.deckfour.xes.model.XEvent;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl012UniqueValues;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl01GatherAttributes;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl02SortEvents;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl03MakeLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl04FilterLogOnActivities;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl055MineEdges;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl05Mine;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl066LayoutModelEdges;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl06LayoutModel;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl077AlignEdge;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl07Align;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl099LayoutAlignmentEdge;
import org.processmining.goaldrivenprocessmining.algorithms.chain.Cl09LayoutAlignment;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.mining.logs.IMTrace;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerPanel;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputer;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputerImpl;
import org.processmining.plugins.inductiveVisualMiner.attributes.IvMVirtualAttributeFactory;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceDistinctEventAttribute;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceDuration;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceFitness;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceHasDeviations;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceHasSynchronousMoves;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceLength;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfCompleteEvents;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfLogMoves;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceNumberOfModelMoves;
import org.processmining.plugins.inductiveVisualMiner.attributes.VirtualAttributeTraceSumEventAttribute;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainImplNonBlocking;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.configuration.InductiveVisualMinerConfiguration;
import org.processmining.plugins.inductiveVisualMiner.cost.CostModelFactory;
import org.processmining.plugins.inductiveVisualMiner.cost.CostModelFactoryImplModelDeviationsLP;
import org.processmining.plugins.inductiveVisualMiner.cost.CostModelFactoryImplModelDeviationsServiceLP;
import org.processmining.plugins.inductiveVisualMiner.cost.CostModelFactoryImplModelDeviationsTimeLP;
import org.processmining.plugins.inductiveVisualMiner.cost.CostModelFactoryImplModelLP;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataAnalysisTab;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRowBlock;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRowBlockComputer;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.associations.DataAnalysisTabAssociations;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.associations.DataRowBlockAssociations;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.associations.DataRowBlockAssociationsProcess;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.causal.DataAnalysisTabCausal;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.causal.DataRowBlockCausal;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.cohorts.DataAnalysisTabCohorts;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.cohorts.DataRowBlockCohorts;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.cost.DataAnalysisTabCosts;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.cost.DataRowBlockCosts;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataAnalysisTabEventData;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventData;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventDataHistogram;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventDataHistogramVirtual;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventDataType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventDataTypeVirtual;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.eventattributes.DataRowBlockEventDataVirtual;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.logattributes.DataAnalysisTabLog;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.logattributes.DataRowBlockLogAttributes;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.logattributes.DataRowBlockLogAttributesHighlighted;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.logattributes.DataRowBlockLogEMSC;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.modeltime.DataAnalysisTabModelTime;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.modeltime.RowBlockModelHistogram;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.modeltime.RowBlockModelLogNormal;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.modeltime.RowBlockModelPerformance;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.modeltime.RowBlockModelWeibull;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataAnalysisTabTrace;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTrace;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTraceBlockVirtual;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTraceHistogram;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTraceHistogramVirtual;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTraceType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.DataRowBlockTraceTypeVirtual;
import org.processmining.plugins.inductiveVisualMiner.export.IvMExporter;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorDefault;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.IvMFilterBuilder;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.IvMFilterBuilderFactory;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceAnd;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceAny;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceEndsWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceFollows;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceOr;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceStartsWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceWithEventTwice;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceWithoutAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIMTraceWithoutEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMMoveAnd;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMMoveAny;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMMoveAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMMoveOr;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceAnd;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceAny;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceCohort;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceEndsWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceFollows;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceOr;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceStartsWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceWithEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceWithEventTwice;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceWithoutAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceWithoutEvent;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterXEventAnd;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterXEventAny;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterXEventAttribute;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterXEventOr;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMMove;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMTrace;
import org.processmining.plugins.inductiveVisualMiner.mode.Mode;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeCost;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePaths;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePathsDeviations;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePathsQueueLengths;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePathsService;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePathsSojourn;
import org.processmining.plugins.inductiveVisualMiner.mode.ModePathsWaiting;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeRelativePaths;
import org.processmining.plugins.inductiveVisualMiner.popup.PopupItemActivity;
import org.processmining.plugins.inductiveVisualMiner.popup.PopupItemLog;
import org.processmining.plugins.inductiveVisualMiner.popup.PopupItemLogMove;
import org.processmining.plugins.inductiveVisualMiner.popup.PopupItemModelMove;
import org.processmining.plugins.inductiveVisualMiner.popup.PopupItemStartEnd;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivityCost;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivityName;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivityOccurrences;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivityOccurrencesPerTrace;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivityPerformance;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemActivitySpacer;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogMoveActivities;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogMoveSpacer;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogMoveTitle;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogName;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogSpacer;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemLogTitle;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemModelMoveOccurrences;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemStartEndName;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemStartEndNumberOfTraces;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemStartEndPerformance;
import org.processmining.plugins.inductiveVisualMiner.popup.items.PopupItemStartEndSpacer;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.AllOperatorsMiner;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.LifeCycleMiner;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.Miner;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeVirtual;

import gnu.trove.map.hash.THashMap;

public class GoalDrivenConfigurationDefault extends GoalDrivenConfigurationAbstract {

	protected Cl02SortEvents<GoalDrivenConfiguration> sortEvents;

	public GoalDrivenConfigurationDefault(ProMCanceller canceller, Executor executor) {
		super(canceller, executor);
	}

	@Override
	public IvMFilterBuilderFactory getFilters() {
		return new IvMFilterBuilderFactory() {
			@SuppressWarnings("unchecked")
			public <X> List<IvMFilterBuilder<X, ?, ?>> get(Class<X> clazz) {
				if (clazz == IvMTrace.class) {
					List<IvMFilterBuilder<X, ?, ?>> filterBuilders = new ArrayList<>();
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceCohort());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceStartsWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceEndsWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceWithEventTwice());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceFollows());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceWithoutEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceWithoutAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceAnd());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceOr());
					Collections.sort(filterBuilders);
					filterBuilders.add(0, (IvMFilterBuilder<X, ?, ?>) new FilterIvMTraceAny());
					return filterBuilders;
				} else if (clazz == IvMMove.class) {
					List<IvMFilterBuilder<X, ?, ?>> filterBuilders = new ArrayList<>();
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMMoveAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMMoveAnd());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIvMMoveOr());
					Collections.sort(filterBuilders);
					filterBuilders.add(0, (IvMFilterBuilder<X, ?, ?>) new FilterIvMMoveAny());
					return filterBuilders;
				} else if (clazz == IMTrace.class) {
					List<IvMFilterBuilder<X, ?, ?>> filterBuilders = new ArrayList<>();
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceStartsWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceEndsWithEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceWithEventTwice());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceFollows());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceWithoutEvent());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceWithoutAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceAnd());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterIMTraceOr());
					Collections.sort(filterBuilders);
					filterBuilders.add(0, (IvMFilterBuilder<X, ?, ?>) new FilterIMTraceAny());
					return filterBuilders;
				} else if (clazz == XEvent.class) {
					List<IvMFilterBuilder<X, ?, ?>> filterBuilders = new ArrayList<>();
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterXEventAttribute());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterXEventAnd());
					filterBuilders.add((IvMFilterBuilder<X, ?, ?>) new FilterXEventOr());
					Collections.sort(filterBuilders);
					filterBuilders.add(0, (IvMFilterBuilder<X, ?, ?>) new FilterXEventAny());
					return filterBuilders;
				}
				return null;
			}
		};
	}

	@Override
	protected List<VisualMinerWrapper> createDiscoveryTechniques() {
		return new ArrayList<>(Arrays.asList(new VisualMinerWrapper[] { //
				new Miner(), // 
				new DfgMiner(), //
				new LifeCycleMiner(), //
				new AllOperatorsMiner(), //
		}));
	}

	@Override
	protected List<Mode> createModes() {
		return new ArrayList<>(Arrays.asList(new Mode[] { //
				new ModePaths(), //
				new ModePathsDeviations(), //
				new ModePathsQueueLengths(), //
				new ModePathsSojourn(), //
				new ModePathsWaiting(), //
				new ModePathsService(), //
				new ModeRelativePaths(), //
				new ModeCost() }));
	}

	@Override
	protected List<PopupItemActivity> createPopupItemsActivity() {
		return new ArrayList<>(Arrays.asList(new PopupItemActivity[] { //
				new PopupItemActivityName(), //
				new PopupItemActivitySpacer(), //
				new PopupItemActivityOccurrences(), //
				new PopupItemActivityOccurrencesPerTrace(), //
				new PopupItemActivitySpacer(), //
				new PopupItemActivityPerformance(), //
				new PopupItemActivitySpacer(), //
				new PopupItemActivityCost(), //
		}));
	}

	@Override
	protected List<PopupItemStartEnd> createPopupItemsStartEnd() {
		return new ArrayList<>(Arrays.asList(new PopupItemStartEnd[] { //
				new PopupItemStartEndName(), //
				new PopupItemStartEndSpacer(), //
				new PopupItemStartEndNumberOfTraces(), //
				new PopupItemStartEndSpacer(), //
				new PopupItemStartEndPerformance(), //
		}));
	}

	@Override
	protected List<PopupItemLogMove> createPopupItemsLogMove() {
		return new ArrayList<>(Arrays.asList(new PopupItemLogMove[] { //
				new PopupItemLogMoveTitle(), //
				new PopupItemLogMoveSpacer(), //
				new PopupItemLogMoveActivities(), //
		}));
	}

	@Override
	protected List<PopupItemModelMove> createPopupItemsModelMove() {
		return new ArrayList<>(Arrays.asList(new PopupItemModelMove[] { //
				new PopupItemModelMoveOccurrences(), //
		}));
	}

	@Override
	protected List<PopupItemLog> createPopupItemsLog() {
		return new ArrayList<>(Arrays.asList(new PopupItemLog[] { //
				new PopupItemLogTitle(), //
				new PopupItemLogSpacer(), //
				new PopupItemLogName() //
		}));
	}

	@Override
	protected List<DataAnalysisTab<?, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> createDataAnalysisTables() {
		List<DataAnalysisTab<?, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> result = new ArrayList<>();

		result.add(new DataAnalysisTabLog<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(//
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockLogAttributes<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockLogAttributesHighlighted<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockLogEMSC<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}));

		result.add(new DataAnalysisTabTrace<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockTrace<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockTraceBlockVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockTraceType<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockTraceTypeVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockTraceHistogram<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockTraceHistogramVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}));

		result.add(new DataAnalysisTabEventData<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockEventData<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockEventDataVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockEventDataType<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockEventDataTypeVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockEventDataHistogram<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new DataRowBlockEventDataHistogramVirtual<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}));

		result.add(new DataAnalysisTabModelTime<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new RowBlockModelPerformance<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new RowBlockModelHistogram<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new RowBlockModelWeibull<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						r.add(new RowBlockModelLogNormal<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}));

		result.add(new DataAnalysisTabCohorts<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(
				new Callable<List<DataRowBlock<Cohort, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Cohort, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlock<Cohort, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockCohorts<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}, //
				new Callable<List<DataRowBlockComputer<Cohort, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Cohort, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}));

		result.add(new DataAnalysisTabCosts<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockCosts<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}));

		result.add(new DataAnalysisTabAssociations<>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockAssociationsProcess<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockAssociations<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;
					}
				}));

		result.add(new DataAnalysisTabCausal<>(
				new Callable<List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						List<DataRowBlock<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> r = new ArrayList<>();
						r.add(new DataRowBlockCausal<InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>());
						return r;

					}
				}, //
				new Callable<List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>>>() {
					public List<DataRowBlockComputer<Object, InductiveVisualMinerConfiguration, InductiveVisualMinerPanel>> call()
							throws Exception {
						return new ArrayList<>();
					}
				}));

		return result;
	}

	@Override
	protected List<CostModelFactory> createCostModelFactories() {
		return new ArrayList<>(Arrays.asList(new CostModelFactory[] { //
				new CostModelFactoryImplModelLP(), //
				new CostModelFactoryImplModelDeviationsLP(), //
				new CostModelFactoryImplModelDeviationsServiceLP(), //
				new CostModelFactoryImplModelDeviationsTimeLP(), //
		}));
	}

	@Override
	protected IvMVirtualAttributeFactory createVirtualAttributes() {
		return new IvMVirtualAttributeFactory() {
			public Iterable<AttributeVirtual> createVirtualTraceAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						new VirtualAttributeTraceDuration(), //
						new VirtualAttributeTraceLength(), //
				}));
			}

			public Iterable<AttributeVirtual> createVirtualEventAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						//
				}));
			}

			public Iterable<AttributeVirtual> createVirtualIvMTraceAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				ArrayList<AttributeVirtual> result = new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						new VirtualAttributeTraceNumberOfCompleteEvents(), //
						new VirtualAttributeTraceHasDeviations(), //
						new VirtualAttributeTraceHasSynchronousMoves(), //
						new VirtualAttributeTraceFitness(), //
						new VirtualAttributeTraceNumberOfModelMoves(), //
						new VirtualAttributeTraceNumberOfLogMoves(), //
				}));
				for (AttributeImpl eventAttribute : eventAttributesReal.values()) {
					result.add(new VirtualAttributeTraceDistinctEventAttribute(eventAttribute));
					if (eventAttribute.isNumeric()) {
						result.add(new VirtualAttributeTraceSumEventAttribute(eventAttribute));
					}
				}
				return result;
			}

			public Iterable<AttributeVirtual> createVirtualIvMEventAttributes(
					THashMap<String, AttributeImpl> traceAttributesReal,
					THashMap<String, AttributeImpl> eventAttributesReal) {
				return new ArrayList<>(Arrays.asList(new AttributeVirtual[] { //
						//
				}));
			}
		};
	}

	@Override
	protected List<IvMExporter> createExporters() {
		return new ArrayList<>(Arrays.asList(new IvMExporter[] { //
				
		}));
	}

	@Override
	protected GoalDrivenPanel createPanel(ProMCanceller canceller) {
		return new GoalDrivenPanel(this, canceller);
	}

	@Override
	public DataChain<GoalDrivenConfiguration> createChain(final GoalDrivenPanel panel,
			final ProMCanceller canceller, final Executor executor) {
		//set up the state
		DataState state = new DataState();

		//set up the chain
		final DataChainImplNonBlocking<GoalDrivenConfiguration, GoalDrivenPanel> chain = new DataChainImplNonBlocking<GoalDrivenConfiguration, GoalDrivenPanel>(
				state, canceller, executor, this, panel);

		chain.register(new Cl01GatherAttributes());
		chain.register(new Cl012UniqueValues());

		sortEvents = new Cl02SortEvents<>();
		chain.register(sortEvents);
		sortEvents.setOnIllogicalTimeStamps(new Function<Object, Boolean>() {
			public Boolean call(Object input) throws Exception {
				String[] options = new String[] { "Continue with neither animation nor performance", "Reorder events" };
				int n = JOptionPane.showOptionDialog(panel,
						"The event log contains illogical time stamps,\n i.e. some time stamps contradict the order of events.\n\nInductive visual Miner can reorder the events and discover a new model.\nWould you like to do that?", //message
						"Illogical Time Stamps", //title
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, //do not use a custom Icon
						options, //the titles of buttons
						options[0]); //default button title
				if (n == 1) {
					//the user requested to reorder the events
					return true;
				}
				return false;
			}
		});

		chain.register(new Cl03MakeLog<GoalDrivenConfiguration>());
		chain.register(new Cl04FilterLogOnActivities());
		chain.register(new Cl05Mine<GoalDrivenConfiguration>());
		chain.register(new Cl06LayoutModel<GoalDrivenConfiguration>());
		chain.register(new Cl07Align());
		chain.register(new Cl09LayoutAlignment(this));
		chain.register(new Cl055MineEdges<GoalDrivenConfiguration>());
		chain.register(new Cl066LayoutModelEdges<GoalDrivenConfiguration>());
		chain.register(new Cl077AlignEdge());
		chain.register(new Cl099LayoutAlignmentEdge(this));
		return chain;
	}

	protected AlignmentComputer createAlignmentComputer() {
		return new AlignmentComputerImpl();
	}

	protected IvMDecoratorI createDecorator() {
		return new IvMDecoratorDefault();
	}

	
}