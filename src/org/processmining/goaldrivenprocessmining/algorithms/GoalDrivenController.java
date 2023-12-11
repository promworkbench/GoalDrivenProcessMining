package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.CONFIG_Update;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.goaldrivenprocessmining.algorithms.chain.HIGH_MakeHighLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.chain.LOW_MakeLowLevelLog;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EventSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.FilterEdgeConfig;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedObject;
import org.processmining.goaldrivenprocessmining.objectHelper.TraceSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig;
import org.processmining.goaldrivenprocessmining.objectHelper.UpdateConfig.UpdateType;
import org.processmining.goaldrivenprocessmining.objectHelper.ValueCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.goaldrivenprocessmining.panelHelper.GroupActConfig;
import org.processmining.goaldrivenprocessmining.panelHelper.NewCategoryPanel;
import org.processmining.goaldrivenprocessmining.panelHelper.PopupPanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkGuiAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.Miner;

import graph.GoalDrivenDFG;
import graph.GoalDrivenDFGUtils;
import graph.GraphConstants;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.util.TableIterator;
import prefuse.visual.VisualItem;

public class GoalDrivenController {
	private static GoalDrivenPanel panel;
	private static DataChain<GoalDrivenConfiguration> chain;

	// filter edge
	private static List<FilterEdgeConfig> filterEdgeConfigs;
	public static EdgeObject currentSelectedHighLevelEdge = null;

	public GoalDrivenController(final PluginContext context, final GoalDrivenConfiguration configuration,
			final XLog log, final ProMCanceller canceller) {

		this.panel = configuration.getPanel();
		this.chain = configuration.getChain();
		this.filterEdgeConfigs = new ArrayList<>();

		//initialise gui handlers
		initGui(canceller, configuration);

		//set up the controller view
		this.chain.setOnChange(new Runnable() {
			public void run() {
				panel.getControllerView().pushCompleteChainLinks(chain);
			}
		});
		//start the chain
		this.chain.setFixedObject(IvMObject.input_log, log);
	}

	/****************************************************************************************************************/

	public static void loadConfigForFilterEdges(EdgeObject edgeObject) {
		FilterEdgeConfig filterEdgeConfig = null;
		for (FilterEdgeConfig fEC : filterEdgeConfigs) {
			if (fEC.getEdgeObject().equals(edgeObject)) {
				filterEdgeConfig = fEC;
				break;
			}
		}
		// apply the config 
		if (filterEdgeConfig != null) {
			// apply the config for low 
			// apply the threshold
			double lower = filterEdgeConfig.getThreshold()[0] / 100f;
			double upper = filterEdgeConfig.getThreshold()[1] / 100f;
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getEdgeSlider().getRangeSlider()
					.setValue(filterEdgeConfig.getThreshold()[0]);
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getEdgeSlider().getRangeSlider()
					.setUpperValue(filterEdgeConfig.getThreshold()[1]);
			chain.setObject(GoalDrivenObject.low_edge_threshold, new Double[] { lower, upper });
			// apply the hide isolate acts option
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getHideIsolateActivity()
					.setSelected(filterEdgeConfig.getIsHideIsolateActs());
			chain.setObject(GoalDrivenObject.is_low_edge_hide_isolate, filterEdgeConfig.getIsHideIsolateActs());
			// apply the list persistent paths
			List<Object[]> data = filterEdgeConfig.getPersistentPaths();
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().updatePersistentTable(data);
		} else {
			// reset the filter edge panel for low 
			// apply the threshold
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getEdgeSlider().getRangeSlider()
					.setValue(0);
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getEdgeSlider().getRangeSlider()
					.setUpperValue(100);
			chain.setObject(GoalDrivenObject.low_edge_threshold, new Double[] { 0d, 1d });
			// apply the hide isolate acts option
			panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getHideIsolateActivity()
					.setSelected(false);
			chain.setObject(GoalDrivenObject.is_low_edge_hide_isolate, false);
			// apply the list persistent paths
			((DefaultTableModel) panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
					.getPersistentPathsTable().getModel()).setRowCount(0);

		}

	}

	/****************************************************************************************************************/

	public static void expandSelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		if (isHighLevel) {
			GoalDrivenDFGUtils.setGroupStateExpanded(panel.getHighDfgPanel(), groupSkeleton);
		}
		GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
	}

	public static void collapseSelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		if (isHighLevel) {
			GoalDrivenDFGUtils.setGroupStateCollapsed(panel.getHighDfgPanel(), groupSkeleton);
		}
		GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
	}

	public static void removeGroupFromGraph(String groupName) {
		Config currentConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		GroupSkeleton selectedGroup = null;
		GroupSkeleton parentGroup = null;
		List<GroupSkeleton> newGroupSkeletons = new ArrayList<GroupSkeleton>();
		for (GroupSkeleton groupSkeleton : currentConfig.getListGroupSkeletons()) {
			if (!groupSkeleton.getGroupName().equals(groupName)) {
				newGroupSkeletons.add(groupSkeleton);
			} else {
				selectedGroup = groupSkeleton;
			}
		}
		if (selectedGroup != null) {
			for (GroupSkeleton groupSkeleton : currentConfig.getListGroupSkeletons()) {
				if (groupSkeleton.getListGroup().contains(selectedGroup)) {
					parentGroup = groupSkeleton;
					break;
				}
			}
			GoalDrivenDFGUtils.removeGroupState(panel.getHighDfgPanel(), selectedGroup, parentGroup);
			GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
		}

		CONFIG_Update.currentConfig.setListGroupSkeletons(newGroupSkeletons);
		chain.setObject(GoalDrivenObject.config, CONFIG_Update.currentConfig);
	}

	public static void removeActInGroupConfigObject(String groupName, String act) {
		Config currentConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		GroupSkeleton selectedGroup = null;
		GroupSkeleton changedGroup = null;
		for (GroupSkeleton groupSkeleton : currentConfig.getListGroupSkeletons()) {
			if (groupSkeleton.getGroupName().equals(act)) {
				changedGroup = groupSkeleton;
			}
			if (groupSkeleton.getGroupName().equals(groupName)) {
				selectedGroup = groupSkeleton;
			}
		}

		if (changedGroup == null) {
			// change the selected group
			List<String> newChildAct = new ArrayList<String>();
			for (String childAct : selectedGroup.getListAct()) {
				if (!childAct.equals(act)) {
					newChildAct.add(childAct);
				}
			}
			selectedGroup.setListAct(newChildAct);
		} else {
			List<GroupSkeleton> newChildGroups = new ArrayList<GroupSkeleton>();
			for (GroupSkeleton groupSkeleton : selectedGroup.getListGroup()) {
				if (!groupSkeleton.getGroupName().equals(act)) {
					newChildGroups.add(groupSkeleton);
				}
			}
			selectedGroup.setListGroup(newChildGroups);
			// reset group in graph
			GoalDrivenDFGUtils.removeGroupStateFromGroup(panel.getHighDfgPanel(), changedGroup, selectedGroup);
			//			GoalDrivenDFGUtils.addGroupState(changedGroup);
		}
		// update dfg
		GoalDrivenDFGUtils.editGroupState(panel.getHighDfgPanel(), selectedGroup);
		GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
		// for config 
		List<GroupSkeleton> newGroupSkeletons = new ArrayList<GroupSkeleton>();
		for (GroupSkeleton groupSkeleton : currentConfig.getListGroupSkeletons()) {
			if (groupSkeleton.getGroupName().equals(groupName)) {
				newGroupSkeletons.add(selectedGroup);
			} else {
				newGroupSkeletons.add(groupSkeleton);
			}
		}
		CONFIG_Update.currentConfig.setListGroupSkeletons(newGroupSkeletons);
		chain.setObject(GoalDrivenObject.config, CONFIG_Update.currentConfig);
	}

	public static void addGroupConfigObject(String groupName, Boolean isHighLevel) {
		List<String> selectedAct = new ArrayList<>();
		List<GroupSkeleton> selectedGroup = new ArrayList<>();
		Visualization visHigh = panel.getHighDfgPanel().getVisualization();
		Visualization visLow = panel.getLowDfgPanel().getVisualization();

		Visualization vis = isHighLevel ? visHigh : visLow;

		List<VisualItem> allVisualItemsHigh = GoalDrivenDFGUtils.getAllNodes(vis);
		for (VisualItem item : allVisualItemsHigh) {
			if (item.getBoolean(GraphConstants.IS_SELECTED)) {
				if (item.get(GraphConstants.NODE_TYPE_FIELD) == NodeType.ACT_NODE) {
					selectedAct.add(item.getString(GraphConstants.LABEL_FIELD));
				} else {
					if (CONFIG_Update.currentConfig != null) {
						Config updateConfig = CONFIG_Update.currentConfig;
						String itemName = item.getString(GraphConstants.LABEL_FIELD);
						for (GroupSkeleton groupSkeleton : updateConfig.getListGroupSkeletons()) {
							if (groupSkeleton.getGroupName().equals(itemName)) {
								selectedGroup.add(groupSkeleton);
								break;
							}
						}
					}
				}

			}
		}
		GroupSkeleton newGroupSkeleton = new GroupSkeleton(groupName, selectedAct, selectedGroup);
		GoalDrivenDFGUtils.addGroupState(newGroupSkeleton);
		GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
		// update config
		Config updatedConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		updatedConfig.addGroup(newGroupSkeleton);
		CONFIG_Update.currentConfig = updatedConfig;
		chain.setObject(GoalDrivenObject.config, updatedConfig);

	}

	protected void initGui(final ProMCanceller canceller, final GoalDrivenConfiguration configuration) {
		initGuiControlBar();
		initGuiMiner();
		initHighLevelGraph();
		initLowLevelGraph();
		initStatPanel();
	}

	protected void initGuiMiner() {
		//miner
		setObject(IvMObject.selected_miner, new Miner());
		chain.setObject(IvMObject.selected_miner, (VisualMinerWrapper) new DfgMiner());
		//noise threshold
		setObject(IvMObject.selected_noise_threshold, 1.0);
		chain.setObject(IvMObject.selected_noise_threshold, 1.0);

	}

	protected void initHighLevelGraph() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "high-level dfg update to panel";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				if (inputs.has(GoalDrivenObject.high_level_dfg)) {
					GoalDrivenDFG dfg = inputs.get(GoalDrivenObject.high_level_dfg);
					panel.getContentLeftPanel().remove(panel.getHighDfgPanel());
					panel.setHighDfgPanel(dfg);
					panel.getHighDfgPanel().setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
					panel.getHighDfgPanel().setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
					panel.getContentLeftPanel().add(panel.getHighDfgPanel(), BorderLayout.CENTER);
					panel.revalidate();
					panel.repaint();
				}
			}

			public void invalidate(GoalDrivenPanel panel) {
			}
		});

	}

	protected void initLowLevelGraph() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "low level graph update to panel";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg,
						GoalDrivenObject.selected_source_target_node };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				Boolean isLowClear = false;
				if (inputs.has(GoalDrivenObject.low_level_dfg)) {
					GoalDrivenDFG dfg = inputs.get(GoalDrivenObject.low_level_dfg);
					if (!dfg.getLog().getEdgeHashTable().getEdgeTable().isEmpty()) {
						panel.getContentRightPanel().remove(panel.getLowDfgPanel());
						panel.setLowDfgPanel(dfg);
						panel.getLowDfgPanel().setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
						panel.getLowDfgPanel().setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
						panel.getContentRightPanel().add(panel.getLowDfgPanel(), BorderLayout.CENTER);
						isLowClear = false;

					} else {
						panel.getContentRightPanel().remove(panel.getLowDfgPanel());
						GDPMLogSkeleton log = null;
						panel.setLowDfgPanel(new GoalDrivenDFG(log, false));
						panel.getLowDfgPanel().setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
						panel.getLowDfgPanel().setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
						panel.getContentRightPanel().add(panel.getLowDfgPanel(), BorderLayout.CENTER);
						panel.getLowDfgTitle().setText("Low-level DFG");
						isLowClear = true;
					}

				}
				if (!isLowClear) {
					if (inputs.has(GoalDrivenObject.selected_source_target_node)) {
						HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
						String source = (String) passValues.get("source");
						String target = (String) passValues.get("target");
						panel.getLowDfgTitle().setText("Low-level DFG - " + source + " \u2192 " + target);
					}
				}

				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
			}
		});

		//mode switch

	}

	protected void initGuiControlBar() {

		/*--------filter edge config panel---------*/
		// filter button
		panel.getControlBar().getFilterButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.5 * panel.getConfigCards().getsWidth()), 400);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "2");

			}
		});
		panel.getConfigCards().getFilterConfigPanel().getFilterCloseButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}

		});
		// high level
		// action for hovering over tables
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getRemovingPathsTable()
				.addMouseMotionListener(new MouseInputAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getRemovingPathsTable();
						int row = table.rowAtPoint(e.getPoint());
						if (row >= 0) {
							table.clearSelection(); // Clear previous selections
							table.addRowSelectionInterval(row, row);
							table.setCursor(new Cursor(Cursor.HAND_CURSOR));
						} else {
							table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getPersistentPathsTable()
				.addMouseMotionListener(new MouseInputAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getPersistentPathsTable();
						int row = table.rowAtPoint(e.getPoint());
						if (row >= 0) {
							table.clearSelection(); // Clear previous selections
							table.addRowSelectionInterval(row, row);
							table.setCursor(new Cursor(Cursor.HAND_CURSOR));
						} else {
							table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getRemovingPathsTable().getModel()
				.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						TableModel model = (TableModel) e.getSource();
						List<String> labels = new ArrayList<String>();

						for (int i = 0; i < model.getRowCount(); i++) {
							labels.add((String) model.getValueAt(i, 0));
						}
						chain.setObject(GoalDrivenObject.high_removing_path_table, labels);
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getPersistentPathsTable().getModel()
				.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						TableModel model = (TableModel) e.getSource();
						List<String> labels = new ArrayList<String>();

						for (int i = 0; i < model.getRowCount(); i++) {
							labels.add((String) model.getValueAt(i, 0));
						}
						chain.setObject(GoalDrivenObject.high_keeping_path_table, labels);
					}
				});
		// action for clicking source, target columns
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getRemovingPathsTable()
				.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent evt) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getRemovingPathsTable();
						int row = table.rowAtPoint(evt.getPoint());
						int col = table.columnAtPoint(evt.getPoint());
						if ((row >= 0 && col == 1) || (row >= 0 && col == 2)) {
							String act = table.getValueAt(row, col).toString();
							GoalDrivenDFGUtils.isInSelectActMode = true;
							SelectedObject selectedObject = new SelectedObject(act, null);
							chain.setObject(GoalDrivenObject.selected_object, selectedObject);
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getPersistentPathsTable()
				.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent evt) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getPersistentPathsTable();
						int row = table.rowAtPoint(evt.getPoint());
						int col = table.columnAtPoint(evt.getPoint());
						if ((row >= 0 && col == 1) || (row >= 0 && col == 2)) {
							String act = table.getValueAt(row, col).toString();
							GoalDrivenDFGUtils.isInSelectActMode = true;
							SelectedObject selectedObject = new SelectedObject(act, null);
							chain.setObject(GoalDrivenObject.selected_object, selectedObject);
						}
					}
				});
		// chain for tables
		// high level chain
		// set default not hide isolate activities
		chain.setObject(GoalDrivenObject.is_high_edge_hide_isolate, false);
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.is_high_edge_hide_isolate,
						GoalDrivenObject.high_edge_threshold, GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG highLevelDfg = inputs.get(GoalDrivenObject.high_level_dfg);
				Double[] edgeThreshold = inputs.get(GoalDrivenObject.high_edge_threshold);
				Boolean isHideIsolate = inputs.get(GoalDrivenObject.is_high_edge_hide_isolate);
				int maxFreq = Collections.max(highLevelDfg.getCurrentFrequencyEdge().values());
				int lowerThreshold = (int) Math.ceil(maxFreq * edgeThreshold[0]);
				int upperThreshold = (int) Math.round(maxFreq * edgeThreshold[1]);

				// find the edges that need to be filtered and kept. That is has the freq below the threshold
				List<EdgeObject> filteredEdges = new ArrayList<EdgeObject>();
				List<EdgeObject> keptEdges = new ArrayList<EdgeObject>();

				for (Map.Entry<EdgeObject, Integer> entry : highLevelDfg.getCurrentFrequencyEdge().entrySet()) {
					if (entry.getValue() < lowerThreshold || entry.getValue() > upperThreshold) {
						filteredEdges.add(entry.getKey());
					} else {
						keptEdges.add(entry.getKey());
					}
				}
				// find these edges in the graph
				List<Integer> filteredEdgeRows = new ArrayList<>();
				List<Object[]> filteredEdgeData = new ArrayList<>();
				List<Integer> keptEdgeRows = new ArrayList<>();
				Table nodeTable = highLevelDfg.getGraph().getNodeTable();
				Table edgeTable = highLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						int source = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
						int target = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
						String sourceString = nodeTable.getTuple(source).getString(GraphConstants.LABEL_FIELD);
						String targetString = nodeTable.getTuple(target).getString(GraphConstants.LABEL_FIELD);
						sourceString = sourceString.equals("**BEGIN**") ? "begin" : sourceString;
						targetString = targetString.equals("**END**") ? "end" : targetString;
						EdgeObject checkEdge = new EdgeObject(sourceString, targetString);
						if (filteredEdges.contains(checkEdge)) {
							// check if this edge persistent kept
							if (!panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
									.doesTableContainValue(
											panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
													.getPersistentPathsTable(),
											edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
								filteredEdgeRows.add(row);
								// add to filtered edges data 
								Object[] data = new Object[] { edgeTable.getString(row, GraphConstants.LABEL_FIELD),
										sourceString, targetString,
										highLevelDfg.getCurrentFrequencyEdge().get(checkEdge) };
								filteredEdgeData.add(data);
							} else {
								keptEdgeRows.add(row);
							}
						}
						if (keptEdges.contains(checkEdge)) {
							keptEdgeRows.add(row);
						}
					}

				}

				// first, make all nodes display
				List<Integer> nodeRows = new ArrayList<>();
				TableIterator nodes = nodeTable.iterator();
				while (nodes.hasNext()) {
					int row = nodes.nextInt();
					if (nodeTable.isValidRow(row)) {
						nodeRows.add(row);
					}
				}
				for (Integer i : nodeRows) {
					nodeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}

				// make these filtered edges non display
				for (Integer i : filteredEdgeRows) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
				}
				// update the removing paths table
				panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
						.updateRemovingTable(filteredEdgeData);
				// make these kept edges display
				for (Integer i : keptEdgeRows) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}

				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
						.updateCellColor(highLevelDfg.getGraph());

				// if hide isolate activities is checked => hide all these nodes, otherwise display these
				if (isHideIsolate) {
					// find all isolate activities
					List<String> isolateActs = new ArrayList<String>(panel.getConfigCards().getFilterConfigPanel()
							.getHighLevelEdgePanel().getDisconnectedBeginActs());
					isolateActs.retainAll(panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
							.getDisconnectedEndActs());
					// hide nodes
					if (!isolateActs.isEmpty()) {
						List<Integer> isolateRows = new ArrayList<>();
						nodes = nodeTable.iterator();
						while (nodes.hasNext()) {
							int row = nodes.nextInt();
							if (nodeTable.isValidRow(row)) {
								if (isolateActs.contains(nodeTable.getString(row, GraphConstants.LABEL_FIELD))) {
									isolateRows.add(row);
								}
							}
						}
						for (Integer i : isolateRows) {
							nodeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
						}
						// its corresponding edges
						List<Integer> hideEdgeRows = new ArrayList<>();
						edges = edgeTable.iterator();
						while (edges.hasNext()) {
							int row = edges.nextInt();
							if (edgeTable.isValidRow(row)) {
								int source = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
								int target = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
								if (isolateRows.contains(source) || isolateRows.contains(target)) {
									hideEdgeRows.add(row);
								}
							}

						}
						for (Integer i : hideEdgeRows) {
							edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
						}

					}
				}

				highLevelDfg.revalidate();
				highLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_removing_path_table,
						GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG highLevelDfg = inputs.get(GoalDrivenObject.high_level_dfg);

				List<String> labels = inputs.get(GoalDrivenObject.high_removing_path_table);
				List<Integer> removingEdgeRow = new ArrayList<>();
				Table edgeTable = highLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						if (labels.contains(edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
							removingEdgeRow.add(row);
						}
					}
				}
				for (Integer i : removingEdgeRow) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
				}
				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
						.updateCellColor(highLevelDfg.getGraph());

				highLevelDfg.revalidate();
				highLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_keeping_path_table, GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG highLevelDfg = inputs.get(GoalDrivenObject.high_level_dfg);

				List<String> labels = inputs.get(GoalDrivenObject.high_keeping_path_table);
				List<Integer> removingEdgeRow = new ArrayList<>();
				Table edgeTable = highLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						if (labels.contains(edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
							removingEdgeRow.add(row);
						}
					}
				}
				for (Integer i : removingEdgeRow) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}
				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
						.updateCellColor(highLevelDfg.getGraph());

				highLevelDfg.revalidate();
				highLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// action for sliders 
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getEdgeSlider().getRangeSlider()
				.addChangeListener(new ChangeListener() {

					public void stateChanged(ChangeEvent e) {
						double upper = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getEdgeSlider().getRangeSlider().getUpperValue() / 100f;
						double lower = panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel()
								.getEdgeSlider().getRangeSlider().getValue() / 100f;
						chain.setObject(GoalDrivenObject.high_edge_threshold, new Double[] { lower, upper });

					}

				});

		// low level 
		// action for hovering over tables
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getRemovingPathsTable()
				.addMouseMotionListener(new MouseInputAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getRemovingPathsTable();
						int row = table.rowAtPoint(e.getPoint());
						if (row >= 0) {
							table.clearSelection(); // Clear previous selections
							table.addRowSelectionInterval(row, row);
							table.setCursor(new Cursor(Cursor.HAND_CURSOR));
						} else {
							table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getPersistentPathsTable()
				.addMouseMotionListener(new MouseInputAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getPersistentPathsTable();
						int row = table.rowAtPoint(e.getPoint());
						if (row >= 0) {
							table.clearSelection(); // Clear previous selections
							table.addRowSelectionInterval(row, row);
							table.setCursor(new Cursor(Cursor.HAND_CURSOR));
						} else {
							table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getRemovingPathsTable().getModel()
				.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						TableModel model = (TableModel) e.getSource();
						List<String> labels = new ArrayList<String>();

						for (int i = 0; i < model.getRowCount(); i++) {
							labels.add((String) model.getValueAt(i, 0));
						}
						chain.setObject(GoalDrivenObject.low_removing_path_table, labels);
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getPersistentPathsTable().getModel()
				.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						TableModel model = (TableModel) e.getSource();
						List<String> labels = new ArrayList<String>();

						for (int i = 0; i < model.getRowCount(); i++) {
							labels.add((String) model.getValueAt(i, 0));
						}
						chain.setObject(GoalDrivenObject.low_keeping_path_table, labels);
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getSaveFilterEdgeConfigurationButton()
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						EdgeObject edgeObject = GoalDrivenController.currentSelectedHighLevelEdge;
						if (edgeObject != null) {
							// get the threshold
							int lower = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
									.getEdgeSlider().getRangeSlider().getValue();
							int upper = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
									.getEdgeSlider().getRangeSlider().getUpperValue();
							// get hide isolate activity
							Boolean isHideIsolateActivity = panel.getConfigCards().getFilterConfigPanel()
									.getLowLevelEdgePanel().getHideIsolateActivity().isSelected();
							// get persistent paths
							List<Object[]> data = new ArrayList<>();
							DefaultTableModel tableModel = (DefaultTableModel) panel.getConfigCards()
									.getFilterConfigPanel().getLowLevelEdgePanel().getPersistentPathsTable().getModel();

							for (int i = 0; i < tableModel.getRowCount(); i++) {
								Object[] row = new Object[tableModel.getColumnCount()];
								for (int j = 0; j < tableModel.getColumnCount(); j++) {
									row[j] = tableModel.getValueAt(i, j);
								}
								data.add(row);
							}
							FilterEdgeConfig filterEdgeConfig = new FilterEdgeConfig(edgeObject,
									new Integer[] { lower, upper }, isHideIsolateActivity, data);
							// add to the filter edge configs list
							Boolean isInList = false;
							for (FilterEdgeConfig fEC : filterEdgeConfigs) {
								if (fEC.getEdgeObject().equals(edgeObject)) {
									// Replace if edgeObject already exists
									filterEdgeConfigs.set(filterEdgeConfigs.indexOf(fEC), filterEdgeConfig);
									isInList = true;
								}
							}
							if (!isInList) {
								filterEdgeConfigs.add(filterEdgeConfig);
							}

						}
					}
				});

		// action for clicking source, target columns
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getRemovingPathsTable()
				.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent evt) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getRemovingPathsTable();
						int row = table.rowAtPoint(evt.getPoint());
						int col = table.columnAtPoint(evt.getPoint());
						if ((row >= 0 && col == 1) || (row >= 0 && col == 2)) {
							String act = table.getValueAt(row, col).toString();
							GoalDrivenDFGUtils.isInSelectActMode = true;
							SelectedObject selectedObject = new SelectedObject(act, null);
							chain.setObject(GoalDrivenObject.selected_object, selectedObject);
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getPersistentPathsTable()
				.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent evt) {
						JTable table = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getPersistentPathsTable();
						int row = table.rowAtPoint(evt.getPoint());
						int col = table.columnAtPoint(evt.getPoint());
						if ((row >= 0 && col == 1) || (row >= 0 && col == 2)) {
							String act = table.getValueAt(row, col).toString();
							GoalDrivenDFGUtils.isInSelectActMode = true;
							SelectedObject selectedObject = new SelectedObject(act, null);
							chain.setObject(GoalDrivenObject.selected_object, selectedObject);
						}
					}
				});
		// chain for tables
		// low level chain
		chain.setObject(GoalDrivenObject.is_low_edge_hide_isolate, false);
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.is_low_edge_hide_isolate,
						GoalDrivenObject.low_edge_threshold, GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG lowLevelDfg = inputs.get(GoalDrivenObject.low_level_dfg);
				Double[] edgeThreshold = inputs.get(GoalDrivenObject.low_edge_threshold);
				int maxFreq = Collections.max(lowLevelDfg.getCurrentFrequencyEdge().values());
				int lowerThreshold = (int) Math.ceil(maxFreq * edgeThreshold[0]);
				int upperThreshold = (int) Math.round(maxFreq * edgeThreshold[1]);
				Boolean isHideIsolate = inputs.get(GoalDrivenObject.is_low_edge_hide_isolate);

				// find the edges that need to be filtered and kept. That is has the freq below the threshold
				List<EdgeObject> filteredEdges = new ArrayList<EdgeObject>();
				List<EdgeObject> keptEdges = new ArrayList<EdgeObject>();

				for (Map.Entry<EdgeObject, Integer> entry : lowLevelDfg.getCurrentFrequencyEdge().entrySet()) {
					if (entry.getValue() < lowerThreshold || entry.getValue() > upperThreshold) {
						filteredEdges.add(entry.getKey());
					} else {
						keptEdges.add(entry.getKey());
					}
				}
				// find these edges in the graph
				List<Integer> filteredEdgeRows = new ArrayList<>();
				List<Object[]> filteredEdgeData = new ArrayList<>();
				List<Integer> keptEdgeRows = new ArrayList<>();
				Table nodeTable = lowLevelDfg.getGraph().getNodeTable();
				Table edgeTable = lowLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						int source = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
						int target = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
						String sourceString = nodeTable.getTuple(source).getString(GraphConstants.LABEL_FIELD);
						String targetString = nodeTable.getTuple(target).getString(GraphConstants.LABEL_FIELD);
						sourceString = sourceString.equals("**BEGIN**") ? "begin" : sourceString;
						targetString = targetString.equals("**END**") ? "end" : targetString;
						EdgeObject checkEdge = new EdgeObject(sourceString, targetString);
						if (filteredEdges.contains(checkEdge)) {
							// check if this edge persistent kept
							if (!panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
									.doesTableContainValue(
											panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
													.getPersistentPathsTable(),
											edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
								filteredEdgeRows.add(row);
								// add to filtered edges data 
								Object[] data = new Object[] { edgeTable.getString(row, GraphConstants.LABEL_FIELD),
										sourceString, targetString,
										lowLevelDfg.getCurrentFrequencyEdge().get(checkEdge) };
								filteredEdgeData.add(data);
							} else {
								keptEdgeRows.add(row);
							}
						}
						if (keptEdges.contains(checkEdge)) {
							keptEdgeRows.add(row);
						}
					}

				}
				// first, make all nodes display
				List<Integer> nodeRows = new ArrayList<>();
				TableIterator nodes = nodeTable.iterator();
				while (nodes.hasNext()) {
					int row = nodes.nextInt();
					if (nodeTable.isValidRow(row)) {
						nodeRows.add(row);
					}
				}
				for (Integer i : nodeRows) {
					nodeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}
				// make these filtered edges non display
				for (Integer i : filteredEdgeRows) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
				}
				// update the removing paths table
				panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
						.updateRemovingTable(filteredEdgeData);
				// make these kept edges display
				for (Integer i : keptEdgeRows) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}
				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
						.updateCellColor(lowLevelDfg.getGraph());
				// if hide isolate activities is checked => hide all these nodes, otherwise display these
				if (isHideIsolate) {
					// find all isolate activities
					List<String> isolateActs = new ArrayList<String>(panel.getConfigCards().getFilterConfigPanel()
							.getLowLevelEdgePanel().getDisconnectedBeginActs());
					isolateActs.retainAll(panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
							.getDisconnectedEndActs());
					// hide nodes
					if (!isolateActs.isEmpty()) {
						List<Integer> isolateRows = new ArrayList<>();
						nodes = nodeTable.iterator();
						while (nodes.hasNext()) {
							int row = nodes.nextInt();
							if (nodeTable.isValidRow(row)) {
								if (isolateActs.contains(nodeTable.getString(row, GraphConstants.LABEL_FIELD))) {
									isolateRows.add(row);
								}
							}
						}
						for (Integer i : isolateRows) {
							nodeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
						}
						// its corresponding edges
						List<Integer> hideEdgeRows = new ArrayList<>();
						edges = edgeTable.iterator();
						while (edges.hasNext()) {
							int row = edges.nextInt();
							if (edgeTable.isValidRow(row)) {
								int source = edgeTable.getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
								int target = edgeTable.getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
								if (isolateRows.contains(source) || isolateRows.contains(target)) {
									hideEdgeRows.add(row);
								}
							}

						}
						for (Integer i : hideEdgeRows) {
							edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
						}

					}
				}

				lowLevelDfg.revalidate();
				lowLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.low_removing_path_table, GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG lowLevelDfg = inputs.get(GoalDrivenObject.low_level_dfg);

				List<String> labels = inputs.get(GoalDrivenObject.low_removing_path_table);
				List<Integer> removingEdgeRow = new ArrayList<>();
				Table edgeTable = lowLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						if (labels.contains(edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
							removingEdgeRow.add(row);
						}
					}
				}
				for (Integer i : removingEdgeRow) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, false);
				}
				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
						.updateCellColor(lowLevelDfg.getGraph());

				lowLevelDfg.revalidate();
				lowLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.low_keeping_path_table, GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG lowLevelDfg = inputs.get(GoalDrivenObject.low_level_dfg);

				List<String> labels = inputs.get(GoalDrivenObject.low_keeping_path_table);
				List<Integer> removingEdgeRow = new ArrayList<>();
				Table edgeTable = lowLevelDfg.getGraph().getEdgeTable();
				TableIterator edges = edgeTable.iterator();
				while (edges.hasNext()) {
					int row = edges.nextInt();
					if (edgeTable.isValidRow(row)) {
						if (labels.contains(edgeTable.getString(row, GraphConstants.LABEL_FIELD))) {
							removingEdgeRow.add(row);
						}
					}
				}
				for (Integer i : removingEdgeRow) {
					edgeTable.setBoolean(i, GraphConstants.IS_DISPLAY, true);
				}
				// update color cell
				panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
						.updateCellColor(lowLevelDfg.getGraph());

				lowLevelDfg.revalidate();
				lowLevelDfg.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

		// action for slider
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getEdgeSlider().getRangeSlider()
				.addChangeListener(new ChangeListener() {

					public void stateChanged(ChangeEvent e) {
						double lower = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getEdgeSlider().getRangeSlider().getValue() / 100f;
						double upper = panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel()
								.getEdgeSlider().getRangeSlider().getUpperValue() / 100f;
						chain.setObject(GoalDrivenObject.low_edge_threshold, new Double[] { lower, upper });

					}

				});

		// checkbox for hide isolate activity
		panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().getHideIsolateActivity()
				.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							chain.setObject(GoalDrivenObject.is_high_edge_hide_isolate, true);
						} else {
							chain.setObject(GoalDrivenObject.is_high_edge_hide_isolate, false);
						}
					}
				});
		panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().getHideIsolateActivity()
				.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							chain.setObject(GoalDrivenObject.is_low_edge_hide_isolate, true);
						} else {
							chain.setObject(GoalDrivenObject.is_low_edge_hide_isolate, false);
						}
					}
				});
		/*------------------------------------------*/

		// expand button
		panel.getControlBar().getExpandButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String label = panel.getControlBar().getExpandButton().getText().split(" ")[0];
				panel.setLayout(new BorderLayout());
				panel.add(panel.getControlBar(), BorderLayout.NORTH);
				panel.add(panel.getLayeredPanel(), BorderLayout.CENTER);
				if (label.equals("Collapse")) {
					double contentPanelSize[][] = { { 0.5, 0.5 }, { TableLayoutConstants.FILL } };
					panel.getContentPanel().setLayout(new TableLayout(contentPanelSize));
					panel.getContentLeftPanel().add(panel.getHighDfgPanel());
					panel.getContentPanel().add(panel.getContentLeftPanel(), "0,0");
					panel.getContentRightPanel().add(panel.getLowDfgPanel());
					panel.getContentPanel().add(panel.getContentRightPanel(), "1,0");
					panel.getControlBar().getExpandButton().setText("Expand stat window");
				} else {
					double contentPanelSize[][] = { { 0.4, 0.4, 0.2 }, { TableLayoutConstants.FILL } };
					panel.getContentPanel().setLayout(new TableLayout(contentPanelSize));
					panel.getContentLeftPanel().add(panel.getHighDfgPanel());
					panel.getContentPanel().add(panel.getContentLeftPanel(), "0,0");
					panel.getContentRightPanel().add(panel.getLowDfgPanel());
					panel.getContentPanel().add(panel.getContentRightPanel(), "1,0");
					panel.getContentPanel().add(panel.getSidePanel(), "2,0");
					panel.getControlBar().getExpandButton().setText("Collapse stat window");
				}

				panel.revalidate();
				panel.repaint();

			}

		});

		/*--------All act config panel---------*/
		// all act config button
		panel.getControlBar().getAllActivityButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.37 * panel.getConfigCards().getsWidth()), 400);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "7");

			}

		});
		// all act table row listener
		panel.getConfigCards().getAllActivityConfigPanel().getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				JTable table = (JTable) evt.getSource();
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col == 0) {
					String act = table.getValueAt(row, col).toString();
					GoalDrivenDFGUtils.isInSelectActMode = true;
					SelectedObject selectedObject = new SelectedObject(act, null);
					chain.setObject(GoalDrivenObject.selected_object, selectedObject);
				}
			}
		});
		panel.getConfigCards().getAllActivityConfigPanel().getTable().addMouseMotionListener(new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable table = panel.getConfigCards().getAllActivityConfigPanel().getTable();
				int row = table.rowAtPoint(e.getPoint());
				if (table.getSelectedRows().length <= 1) {
					if (row >= 0) {
						table.clearSelection(); // Clear previous selections
						table.addRowSelectionInterval(row, row);
						table.setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				} else {
					// Rows are selected, disable hover effect
					table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});
		// inti first all act object
		List<String> highDesireActs = new ArrayList<String>();
		List<String> lowDesireActs = new ArrayList<String>();
		List<String> highPriorityActs = new ArrayList<String>();
		List<String> lowPriorityActs = new ArrayList<String>();
		// get data from table
		JTable table = panel.getConfigCards().getAllActivityConfigPanel().getTable();
		for (int row = 0; row < table.getModel().getRowCount(); row++) {
			String act = (String) table.getModel().getValueAt(row, 0);
			String priority = (String) table.getModel().getValueAt(row, 2);
			String desire = (String) table.getModel().getValueAt(row, 3);
			// priority
			if (priority.equals("High")) {
				highPriorityActs.add(act);
			} else if (priority.equals("Low")) {
				lowPriorityActs.add(act);
			}
			// desire
			if (desire.equals("High")) {
				highDesireActs.add(act);
			} else if (desire.equals("Low")) {
				lowDesireActs.add(act);
			}
		}
		chain.setObject(GoalDrivenObject.high_desire_acts, highDesireActs.toArray(new String[0]));
		chain.setObject(GoalDrivenObject.low_desire_acts, lowDesireActs.toArray(new String[0]));
		chain.setObject(GoalDrivenObject.high_priority_acts, highPriorityActs.toArray(new String[0]));
		chain.setObject(GoalDrivenObject.low_priority_acts, lowPriorityActs.toArray(new String[0]));
		chain.setObject(GoalDrivenObject.selected_additional_mode, "None");
		// all act config done button
		panel.getConfigCards().getAllActivityConfigPanel().getAllActConfigDoneButton()
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						List<String> highLevelActs = new ArrayList<String>();
						List<String> lowLevelActs = new ArrayList<String>();
						List<String> highDesireActs = new ArrayList<String>();
						List<String> neutralDesireActs = new ArrayList<String>();
						List<String> lowDesireActs = new ArrayList<String>();
						List<String> highPriorityActs = new ArrayList<String>();
						List<String> neutralPriorityActs = new ArrayList<String>();
						List<String> lowPriorityActs = new ArrayList<String>();
						// get data from table
						JTable table = panel.getConfigCards().getAllActivityConfigPanel().getTable();
						for (int row = 0; row < table.getModel().getRowCount(); row++) {
							String act = (String) table.getModel().getValueAt(row, 0);
							String hierarchy = (String) table.getModel().getValueAt(row, 2);
							String priority = (String) table.getModel().getValueAt(row, 3);
							String desire = (String) table.getModel().getValueAt(row, 4);
							// hierarchy
							if (hierarchy.equals("High")) {
								highLevelActs.add(act);
							} else {
								lowLevelActs.add(act);
							}
							// priority
							if (priority.equals("High")) {
								highPriorityActs.add(act);
							} else if (priority.equals("Neutral")) {
								neutralPriorityActs.add(act);
							} else {
								lowPriorityActs.add(act);
							}
							// desire
							if (desire.equals("High")) {
								highDesireActs.add(act);
							} else if (desire.equals("Neutral")) {
								neutralDesireActs.add(act);
							} else {
								lowDesireActs.add(act);
							}
						}
						// update config for hierarchy
						HashMap<String, String[]> updateMap = new HashMap<String, String[]>();
						updateMap.put("High", highLevelActs.toArray(new String[0]));
						updateMap.put("Low", lowLevelActs.toArray(new String[0]));
						UpdateConfig updateConfig = new UpdateConfig(UpdateType.SELECTED_ACT, updateMap);
						// reset filter edges panel
						panel.getConfigCards().getFilterConfigPanel().getHighLevelEdgePanel().resetFilter();
						panel.getConfigCards().getFilterConfigPanel().getLowLevelEdgePanel().resetFilter();
						// reset low level dfg panel
						panel.getContentRightPanel().remove(panel.getLowDfgPanel());
						GDPMLogSkeleton log = null;
						panel.setLowDfgPanel(new GoalDrivenDFG(log, false));
						panel.getLowDfgPanel().setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
						panel.getLowDfgPanel().setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
						panel.getContentRightPanel().add(panel.getLowDfgPanel(), BorderLayout.CENTER);
						panel.getLowDfgTitle().setText("Low-level DFG");
						// update
						chain.setObject(GoalDrivenObject.update_config_object, updateConfig);
						chain.setObject(GoalDrivenObject.high_desire_acts, highDesireActs.toArray(new String[0]));
						chain.setObject(GoalDrivenObject.low_desire_acts, lowDesireActs.toArray(new String[0]));
						chain.setObject(GoalDrivenObject.high_priority_acts, highPriorityActs.toArray(new String[0]));
						chain.setObject(GoalDrivenObject.low_priority_acts, lowPriorityActs.toArray(new String[0]));
						panel.getConfigCards().setVisible(false);
					}

				});
		// all act config cancel button
		panel.getConfigCards().getAllActivityConfigPanel().getAllActConfigCancelButton()
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						panel.getConfigCards().setVisible(false);
					}
				});
		// update the all act config
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.map_act_freq, GoalDrivenObject.selected_unique_values,
						GoalDrivenObject.unselected_unique_values };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] selectedUniqueValues = inputs.get(GoalDrivenObject.selected_unique_values);
				AttributeClassifier[] unselectedUniqueValues = inputs.get(GoalDrivenObject.unselected_unique_values);
				HashMap<String, Integer> mapActFreq = inputs.get(GoalDrivenObject.map_act_freq);
				Map<String, String> mapActToHierarchy = new HashMap<String, String>();
				for (AttributeClassifier attribute : selectedUniqueValues) {
					mapActToHierarchy.put(attribute.toString(), "High");
				}
				for (AttributeClassifier attribute : unselectedUniqueValues) {
					mapActToHierarchy.put(attribute.toString(), "Low");
				}
				int maxActFreq = Collections.max(mapActFreq.values());
				panel.getConfigCards().getAllActivityConfigPanel().setMaxActFreq(maxActFreq);
				panel.getConfigCards().getAllActivityConfigPanel().updateDefaultConfigTable(mapActToHierarchy,
						mapActFreq);

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// update the selected act on high level DFG
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Display the selected act on the high level graph";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.selected_object, GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG highLevelDFG = inputs.get(GoalDrivenObject.high_level_dfg);
				SelectedObject selectedObject = inputs.get(GoalDrivenObject.selected_object);

				if (selectedObject.getSelectedAct() != null) {
					GoalDrivenDFGUtils.highlightSelectedAct(highLevelDFG, selectedObject.getSelectedAct());
				} else {
					List<EdgeObject> listEdgeObjects = new ArrayList<>();
					listEdgeObjects.add(selectedObject.getSelectedEdgeObject());
					GoalDrivenDFGUtils.highlightSelectedEdge(highLevelDFG, listEdgeObjects, -1);
				}

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// update the selected act on low level DFG
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Display the selected act on the low level graph";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.selected_object, GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG lowLevelDFG = inputs.get(GoalDrivenObject.low_level_dfg);
				SelectedObject selectedObject = inputs.get(GoalDrivenObject.selected_object);
				if (selectedObject.getSelectedAct() != null) {
					GoalDrivenDFGUtils.highlightSelectedAct(lowLevelDFG, selectedObject.getSelectedAct());
				} else {
					List<EdgeObject> listEdgeObjects = new ArrayList<>();
					listEdgeObjects.add(selectedObject.getSelectedEdgeObject());
					GoalDrivenDFGUtils.highlightSelectedEdge(lowLevelDFG, listEdgeObjects, -1);
				}
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		/*-----------------------------------------*/

		/*--------Case config panel---------*/
		panel.getControlBar().getCaseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.6 * panel.getConfigCards().getsWidth()), 500);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "8");

			}

		});
		panel.getConfigCards().getCaseConfigPanel().getCaseConfigCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);

			}
		});
		panel.getConfigCards().getCaseConfigPanel().getCaseConfigDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);

			}
		});
		// action when choose case table clicked
		panel.getConfigCards().getCaseConfigPanel().getChooseCaseTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				JTable table = panel.getConfigCards().getCaseConfigPanel().getChooseCaseTable();
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col == 0) {
					chain.setObject(GoalDrivenObject.selected_case_index,
							table.getRowSorter().convertRowIndexToModel(row));
				}
			}
		});
		// action when choose show table clicked
		panel.getConfigCards().getCaseConfigPanel().getShowCaseTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				JTable table = panel.getConfigCards().getCaseConfigPanel().getShowCaseTable();
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col == 0) {
					GoalDrivenDFGUtils.isInSelectActMode = true;
					SelectedObject selectedObject = new SelectedObject((String) table.getValueAt(row, col), null);
					chain.setObject(GoalDrivenObject.selected_object, selectedObject);
				}
			}
		});

		// chain to setup the choose case table and setup columns for show case table
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Update choose case table";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GDPMLogSkeleton gdpmLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
				List<TraceSkeleton> traces = gdpmLogSkeleton.getLog();
				List<String[]> data = new ArrayList<String[]>();
				long maxDuration = 0;
				for (TraceSkeleton trace : traces) {
					String caseName = trace.getAttributes().get(GoalDrivenConstants.CASE_NAME).toString();
					long duration = trace.getTrace().get(trace.getTrace().size() - 1).getTime()
							- trace.getTrace().get(0).getTime();
					if (duration >= maxDuration) {
						maxDuration = duration;
					}
					String className = "Neutral";
					String[] d = new String[] { caseName, StatUtils.getDurationString(duration), className };
					data.add(d);
				}
				panel.getConfigCards().getCaseConfigPanel().setMaxDuration(maxDuration / 1000);
				panel.getConfigCards().getCaseConfigPanel().updateChooseCaseTable(data);

				List<String> columns = new ArrayList<String>();
				columns.add("Activity");
				columns.add("Timestamp");

				for (String att : gdpmLogSkeleton.getLog().get(0).getTrace().get(0).getAttributes().keySet()) {
					if (!att.equals(GoalDrivenConstants.CASE_NAME) && !att.equals(GoalDrivenConstants.EVENT_ACTIVITY)
							&& !att.equals(GoalDrivenConstants.EVENT_TIME)) {
						columns.add(att);
					}
				}

				panel.getConfigCards().getCaseConfigPanel().updateColumnShowCaseTable(columns);

				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// chain to update show case table
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Update show case table";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.full_log_skeleton, GoalDrivenObject.selected_case_index };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GDPMLogSkeleton gdpmLogSkeleton = inputs.get(GoalDrivenObject.full_log_skeleton);
				TraceSkeleton traceSkeleton = gdpmLogSkeleton.getLog()
						.get(inputs.get(GoalDrivenObject.selected_case_index));
				// update row 
				List<Object[]> data = new ArrayList<>();
				List<Object[]> caseAttributeData = new ArrayList<>();
				for (Map.Entry<String, Object> entry : traceSkeleton.getAttributes().entrySet()) {
					if (!entry.getKey().equals(GoalDrivenConstants.CASE_NAME)
							&& !entry.getKey().equals(GoalDrivenConstants.EVENT_ACTIVITY)
							&& !entry.getKey().equals(GoalDrivenConstants.EVENT_TIME))
						caseAttributeData.add(new Object[] { entry.getKey(), entry.getValue() });
				}
				for (EventSkeleton eventSkeleton : traceSkeleton.getTrace()) {
					List<Object> row = new ArrayList<>();
					row.add(eventSkeleton.getActivity());
					row.add(StatUtils.convertMillisToDateString(eventSkeleton.getTime()));
					for (Map.Entry<String, Object> entry : eventSkeleton.getAttributes().entrySet()) {
						if (!entry.getKey().equals(GoalDrivenConstants.CASE_NAME)
								&& !entry.getKey().equals(GoalDrivenConstants.EVENT_ACTIVITY)
								&& !entry.getKey().equals(GoalDrivenConstants.EVENT_TIME))
							row.add(entry.getValue());
					}
					data.add(row.toArray(new Object[0]));
				}
				// change label
				String caseName = traceSkeleton.getAttributes().get(GoalDrivenConstants.CASE_NAME).toString();
				String newLabel = "<html>Case: " + caseName + "<span style='font-weight:normal;'>" + " ("
						+ Integer.toString(data.size()) + " events" + ")" + "</span></html>";
				panel.getConfigCards().getCaseConfigPanel().getShowCaseLabel().setText(newLabel);
				panel.getConfigCards().getCaseConfigPanel().updateShowCaseTable(data);
				panel.getConfigCards().getCaseConfigPanel().updateShowCaseAttributeTable(caseAttributeData);

				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Update show case table";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.selected_path_from_high };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_path_from_high);
				String source = (String) passValues.get("source");
				String target = (String) passValues.get("target");

				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// listener for show good case in choose case table
		panel.getConfigCards().getCaseConfigPanel().getShowGoodButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Integer> goodCase = new ArrayList<>();
				// get data from table
				JTable table = panel.getConfigCards().getCaseConfigPanel().getChooseCaseTable();
				for (int row = 0; row < table.getModel().getRowCount(); row++) {
					String classCase = (String) table.getModel().getValueAt(row, 2);
					if (classCase.equals("Good")) {
						goodCase.add(row);
					}
				}
				// get highlighting edge from high level
				List<EdgeObject> highlightingEdgeHigh = new ArrayList<>();
				GoalDrivenDFG highLevelDfg = panel.getHighDfgPanel();
				GDPMLogSkeleton highLevelLog = highLevelDfg.getLog();
				for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : highLevelLog.getEdgeHashTable()
						.getEdgeTable().entrySet()) {
					Set<Integer> affectedCases = entry.getValue().keySet();
					Set<Integer> setCopy = new HashSet<>(affectedCases);
					setCopy.retainAll(goodCase);
					if (!setCopy.isEmpty()) {
						highlightingEdgeHigh.add(entry.getKey());
					}
				}
				GoalDrivenDFGUtils.highlightSelectedEdge(highLevelDfg, highlightingEdgeHigh,
						GraphConstants.HIGHLIGHT_STROKE_GOOD_CASE_COLOR);

				// get highlighting edge from low level
				List<EdgeObject> highlightingEdgeLow = new ArrayList<>();
				GoalDrivenDFG lowLevelDfg = panel.getLowDfgPanel();
				GDPMLogSkeleton lowLevelLog = lowLevelDfg.getLog();
				if (lowLevelLog.getEdgeHashTable() != null) {
					for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : lowLevelLog.getEdgeHashTable()
							.getEdgeTable().entrySet()) {
						Set<Integer> affectedCases = entry.getValue().keySet();
						Set<Integer> setCopy = new HashSet<>(affectedCases);
						setCopy.retainAll(goodCase);
						if (!setCopy.isEmpty()) {
							highlightingEdgeLow.add(entry.getKey());
						}
					}
					GoalDrivenDFGUtils.highlightSelectedEdge(lowLevelDfg, highlightingEdgeLow,
							GraphConstants.HIGHLIGHT_STROKE_GOOD_CASE_COLOR);
				}

				panel.getConfigCards().setVisible(false);
			}

		});

		// listener for show bad case in choose case table
		panel.getConfigCards().getCaseConfigPanel().getShowBadButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Integer> badCase = new ArrayList<>();
				// get data from table
				JTable table = panel.getConfigCards().getCaseConfigPanel().getChooseCaseTable();
				for (int row = 0; row < table.getModel().getRowCount(); row++) {
					String classCase = (String) table.getModel().getValueAt(row, 2);
					if (classCase.equals("Bad")) {
						badCase.add(row);
					}
				}
				// get highlighting edge from high level
				List<EdgeObject> highlightingEdgeHigh = new ArrayList<>();
				GoalDrivenDFG highLevelDfg = panel.getHighDfgPanel();
				GDPMLogSkeleton highLevelLog = highLevelDfg.getLog();
				for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : highLevelLog.getEdgeHashTable()
						.getEdgeTable().entrySet()) {
					Set<Integer> affectedCases = entry.getValue().keySet();
					Set<Integer> setCopy = new HashSet<>(affectedCases);
					setCopy.retainAll(badCase);
					if (!setCopy.isEmpty()) {
						highlightingEdgeHigh.add(entry.getKey());
					}
				}
				GoalDrivenDFGUtils.highlightSelectedEdge(highLevelDfg, highlightingEdgeHigh,
						GraphConstants.HIGHLIGHT_STROKE_BAD_CASE_COLOR);

				// get highlighting edge from low level
				List<EdgeObject> highlightingEdgeLow = new ArrayList<>();
				GoalDrivenDFG lowLevelDfg = panel.getLowDfgPanel();
				GDPMLogSkeleton lowLevelLog = lowLevelDfg.getLog();
				if (lowLevelLog.getEdgeHashTable() != null) {
					for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : lowLevelLog.getEdgeHashTable()
							.getEdgeTable().entrySet()) {
						Set<Integer> affectedCases = entry.getValue().keySet();
						Set<Integer> setCopy = new HashSet<>(affectedCases);
						setCopy.retainAll(badCase);
						if (!setCopy.isEmpty()) {
							highlightingEdgeLow.add(entry.getKey());
						}
					}
					GoalDrivenDFGUtils.highlightSelectedEdge(lowLevelDfg, highlightingEdgeLow,
							GraphConstants.HIGHLIGHT_STROKE_BAD_CASE_COLOR);
				}

				panel.getConfigCards().setVisible(false);
			}

		});

		/*-------------------------------------*/

		// mode done button
		panel.getConfigCards().getModePanel().getModeDoneButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String selectedCategory = "";
				CategoryObject selectedCategoryObject = null;

				if (panel.getConfigCards().getModePanel().getCategoryCheckBox().isSelected()) {
					for (JCheckBox cB : panel.getConfigCards().getModePanel().getListCategoriesCheckBox()) {
						if (cB.isSelected()) {
							selectedCategory = cB.getText();
							break;
						}
					}

					for (CategoryObject cO : panel.getConfigCards().getModePanel().getListCategories()) {
						if (cO.getName().equals(selectedCategory)) {
							selectedCategoryObject = cO;
							break;
						}
					}
					if (selectedCategoryObject != null) {
						chain.setObject(GoalDrivenObject.selected_mode_category, selectedCategoryObject);
					}

				}

				panel.getConfigCards().setVisible(false);
			}

		});
		// mode cancel button
		panel.getConfigCards().getModePanel().getModeCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});

		/*-----------------------------------------*/
		// act config new group button
		panel.getConfigCards().getActConfigPanel().getActConfigNewGroupButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				NewCategoryPanel myPanel = new NewCategoryPanel();

				int result = JOptionPane.showConfirmDialog(null, myPanel, "Category configuration",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					Color categoryColor = (Color) myPanel.getCmb().getSelectedItem();
					String categoryName = myPanel.getGroupNameField().getText();
					GroupActConfig newGroup = new GroupActConfig(
							panel.getConfigCards().getActConfigPanel().getAllGroupPane().getBounds().width,
							categoryName, categoryColor, 0);
					panel.getConfigCards().getActConfigPanel().addNextGroup(newGroup);
					panel.revalidate();
					panel.repaint();
					List<ValueCategoryObject> values = new ArrayList<>();
					for (int i = 0; i < myPanel.getValueTable().getRowCount(); i++) {
						ValueCategoryObject val = new ValueCategoryObject(categoryName,
								(String) myPanel.getValueTable().getValueAt(i, 0),
								(Color) myPanel.getValueTable().getValueAt(i, 1));
						values.add(val);
					}
					CategoryObject newCate = new CategoryObject(categoryName, values);
					panel.getConfigCards().getModePanel().addCategory(newCate);
					panel.getConfigCards().getModePanel().updateCategoryPanel();
					panel.revalidate();
					panel.repaint();
					chain.setObject(GoalDrivenObject.new_category, newCate);

				}

			}

		});
		// act config cancel button
		panel.getConfigCards().getActConfigPanel().getActConfigCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});
		// update category group act
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Update new category to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.new_category };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				CategoryObject newCate = inputs.get(GoalDrivenObject.new_category);
				// create jcombobox
				JComboBox<ValueCategoryObject> cmb = new JComboBox<>();
				for (ValueCategoryObject val : newCate.getValues()) {
					cmb.addItem(val);
				}
				// add new column + config that column
				JTable table = panel.getConfigCards().getActConfigPanel().getActConfigTable();
				panel.getConfigCards().getActConfigPanel().addNewColumnToActConfigTable(newCate.getName());
				panel.getConfigCards().getActConfigPanel().setUpColumn(table,
						table.getColumnModel().getColumn(table.getColumnCount() - 1), cmb);

				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// update all unique values
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique values to act config table";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.all_unique_values };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] allGroupAct = inputs.get(GoalDrivenObject.all_unique_values);
				List<AttributeClassifier> currAtt = new ArrayList<AttributeClassifier>();
				MapActivityCategoryObject currMapActCategory = panel.getConfigCards().getActConfigPanel()
						.getMapActGroup();
				DefaultTableModel actConfigTable = (DefaultTableModel) panel.getConfigCards().getActConfigPanel()
						.getActConfigTable().getModel();
				for (int i = 0; i < actConfigTable.getRowCount(); i++) {
					AttributeClassifier att = (AttributeClassifier) actConfigTable.getValueAt(i, 0);
					currAtt.add(att);
					List<ValueCategoryObject> valueCategories = new ArrayList<>();
					for (int j = 0; j < actConfigTable.getColumnCount(); j++) {
						valueCategories.add((ValueCategoryObject) actConfigTable.getValueAt(i, j));
					}
					if (!currMapActCategory.getMapActivityCategory().containsKey(att)) {
						panel.getConfigCards().getActConfigPanel().getMapActGroup().put(att, valueCategories);
					}
					if (!Arrays.asList(allGroupAct).contains(att)) {
						actConfigTable.removeRow(i);
					}
				}
				if (currAtt.size() < allGroupAct.length) {
					for (AttributeClassifier att : allGroupAct) {
						if (!currAtt.contains(att)) {
							Object[] data = new Object[actConfigTable.getColumnCount()];
							data[0] = att;
							if (currMapActCategory.getMapActivityCategory().containsKey(att)) {
								List<ValueCategoryObject> valueCategories = currMapActCategory.getMapActivityCategory()
										.get(att);
								for (int i = 1; i < data.length; i++) {
									String cate = actConfigTable.getColumnName(i);
									for (ValueCategoryObject vCO : valueCategories) {
										if (vCO.getCategory().equals(cate)) {
											data[i] = vCO;
										} else {
											data[i] = new ValueCategoryObject(cate, "", Color.WHITE);
										}
									}
								}

							} else {
								List<ValueCategoryObject> valueCategories = new ArrayList<>();
								for (int i = 1; i < data.length; i++) {
									valueCategories.add(
											new ValueCategoryObject(actConfigTable.getColumnName(i), "", Color.WHITE));
								}
							}
							actConfigTable.addRow(data);
						}
					}
				}
				chain.setObject(GoalDrivenObject.map_activity_category, currMapActCategory);
				panel.revalidate();
				panel.repaint();
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// act config done button
		panel.getConfigCards().getActConfigPanel().getActConfigDoneButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DefaultTableModel actConfigTable = (DefaultTableModel) panel.getConfigCards().getActConfigPanel()
						.getActConfigTable().getModel();
				MapActivityCategoryObject mapActCategory = new MapActivityCategoryObject();
				MapActivityCategoryObject currMapValueGroup = panel.getConfigCards().getActConfigPanel()
						.getMapActGroup();
				for (int i = 0; i < actConfigTable.getRowCount(); i++) {
					AttributeClassifier att = (AttributeClassifier) actConfigTable.getValueAt(i, 0);
					List<ValueCategoryObject> listValueCategories = new ArrayList<>();
					for (int j = 1; j < actConfigTable.getColumnCount(); j++) {
						ValueCategoryObject valueCategory = (ValueCategoryObject) actConfigTable.getValueAt(i, j);
						if (valueCategory == null) {
							valueCategory = new ValueCategoryObject(actConfigTable.getColumnName(j), "", Color.WHITE);
						}
						listValueCategories.add(valueCategory);
					}
					mapActCategory.put(att, listValueCategories);
					if (!currMapValueGroup.getMapActivityCategory().containsKey(att)) {
						panel.getConfigCards().getActConfigPanel().getMapActGroup().put(att, listValueCategories);
					} else {
						currMapValueGroup.getMapActivityCategory().replace(att, listValueCategories);
					}

				}
				chain.setObject(GoalDrivenObject.map_activity_category, mapActCategory);

				panel.getConfigCards().setVisible(false);
			}

		});
		// checkbox for category in mode panel
		panel.getConfigCards().getModePanel().getCategoryCheckBox().addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				List<JCheckBox> listCheckBoxes = panel.getConfigCards().getModePanel().getListCategoriesCheckBox();
				if (panel.getConfigCards().getModePanel().getCategoryCheckBox().isSelected()) {
					for (JCheckBox cB : listCheckBoxes) {
						cB.setEnabled(true);
					}

				} else {
					for (JCheckBox cB : listCheckBoxes) {
						cB.setSelected(false);
						cB.setEnabled(false);
					}

				}
				panel.revalidate();
				panel.repaint();
			}

		});

		/*--------Group config panel---------*/
		// group button
		panel.getControlBar().getGroupButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "5");
			}
		});
		// group config done button
		panel.getConfigCards().getGroupConfigPanel().getDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});

		// group config cancel button
		panel.getConfigCards().getGroupConfigPanel().getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().getGroupConfigPanel().getDisplayPanel().removeAll();
				panel.getConfigCards().setVisible(false);
			}
		});
		/*-----------------------------------*/

		// update all group in the group config panel
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				// TODO Auto-generated method stub
				return "all current groups";
			}

			public IvMObject<?>[] createInputObjects() {
				// TODO Auto-generated method stub
				return new IvMObject<?>[] { GoalDrivenObject.config };
			}

			public void invalidate(GoalDrivenPanel panel) {
				// TODO Auto-generated method stub

			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				// update for config in control panel
				Config config = inputs.get(GoalDrivenObject.config);
				List<GroupSkeleton> allGroups = config.getListGroupSkeletons();
				panel.getConfigCards().getGroupConfigPanel().getTableModel().setRowCount(0);
				for (GroupSkeleton groupActObject : allGroups) {
					panel.getConfigCards().getGroupConfigPanel().getTableModel()
							.addRow(new Object[] { groupActObject });
				}
				// update for popup
				PopupPanel.groupActObjects = allGroups;
				// clear right panel
				panel.getConfigCards().getGroupConfigPanel().getDisplayPanel().removeAll();
				panel.revalidate();
				panel.repaint();
			}

		});
		// trigger action when clicking on a row of group config panel
		panel.getConfigCards().getGroupConfigPanel().getGroupTable().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = panel.getConfigCards().getGroupConfigPanel().getGroupTable().getSelectedRow();
						if (selectedRow >= 0) {
							panel.getConfigCards().getGroupConfigPanel().updateDisplayPanel(selectedRow);
						}
					}
				});

		// legend button
		panel.getControlBar().getLegendButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.37 * panel.getConfigCards().getsWidth()), 200);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "6");
			}
		});
		// legend done button
		panel.getConfigCards().getLegendPanel().getLegendDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);

				String selectedMode = (String) panel.getConfigCards().getLegendPanel().getModeComboBox()
						.getSelectedItem();
				String selectedAdditionalMode = (String) panel.getConfigCards().getLegendPanel()
						.getAdditionalModeComboBox().getSelectedItem();
				chain.setObject(GoalDrivenObject.selected_mode, selectedMode);
				chain.setObject(GoalDrivenObject.selected_additional_mode, selectedAdditionalMode);
			}
		});
		// legend cancel button
		panel.getConfigCards().getLegendPanel().getLegendCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});
		// update legend in high level dfg
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Display the high level graph based on the selected mode";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_desire_acts, GoalDrivenObject.low_desire_acts,
						GoalDrivenObject.high_priority_acts, GoalDrivenObject.low_priority_acts,
						GoalDrivenObject.selected_additional_mode, GoalDrivenObject.selected_mode,
						GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG highLevelDFG = inputs.get(GoalDrivenObject.high_level_dfg);
				String selectedMode = inputs.get(GoalDrivenObject.selected_mode);
				String selectedAdditionalMode = inputs.get(GoalDrivenObject.selected_additional_mode);
				// mode legend: frequency, throughput
				switch (selectedMode) {
					case GraphConstants.MODE_FREQUENCY :
						GoalDrivenDFGUtils.displayModeFrequency(highLevelDFG);
						break;
					case GraphConstants.MODE_MEAN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMeanThroughput(highLevelDFG);
						break;
					case GraphConstants.MODE_MEDIAN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMedianThroughput(highLevelDFG);
						break;
					case GraphConstants.MODE_MIN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMinThroughput(highLevelDFG);
						break;
					case GraphConstants.MODE_MAX_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMaxThroughput(highLevelDFG);
						break;
					default :
						break;
				}
				// additional mode: desire, priority
				switch (selectedAdditionalMode) {
					case "Desirability" :
						GoalDrivenDFGUtils.displayDesirability(highLevelDFG,
								inputs.get(GoalDrivenObject.high_desire_acts),
								inputs.get(GoalDrivenObject.low_desire_acts));
						break;
					case "Priority" :
						GoalDrivenDFGUtils.displayPriority(highLevelDFG,
								inputs.get(GoalDrivenObject.high_priority_acts),
								inputs.get(GoalDrivenObject.low_priority_acts));
						break;
					case "None" :
						GoalDrivenDFGUtils.displayNoneAdditional(highLevelDFG);
						break;
					default :
						break;
				}

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
		// update legend in low level dfg
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Display the low level graph based on the selected mode";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_desire_acts, GoalDrivenObject.low_desire_acts,
						GoalDrivenObject.high_priority_acts, GoalDrivenObject.low_priority_acts,
						GoalDrivenObject.selected_additional_mode, GoalDrivenObject.selected_mode,
						GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				GoalDrivenDFG lowLevelDFG = inputs.get(GoalDrivenObject.low_level_dfg);
				String selectedMode = inputs.get(GoalDrivenObject.selected_mode);
				String selectedAdditionalMode = inputs.get(GoalDrivenObject.selected_additional_mode);
				switch (selectedMode) {
					case GraphConstants.MODE_FREQUENCY :
						GoalDrivenDFGUtils.displayModeFrequency(lowLevelDFG);
						break;
					case GraphConstants.MODE_MEAN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMeanThroughput(lowLevelDFG);
						break;
					case GraphConstants.MODE_MEDIAN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMedianThroughput(lowLevelDFG);
						break;
					case GraphConstants.MODE_MIN_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMinThroughput(lowLevelDFG);
						break;
					case GraphConstants.MODE_MAX_THROUGHPUT :
						GoalDrivenDFGUtils.displayModeMaxThroughput(lowLevelDFG);
						break;
					default :
						break;
				}
				// additional mode: desire, priority
				switch (selectedAdditionalMode) {
					case "Desirability" :
						GoalDrivenDFGUtils.displayDesirability(lowLevelDFG,
								inputs.get(GoalDrivenObject.high_desire_acts),
								inputs.get(GoalDrivenObject.low_desire_acts));
						break;
					case "Priority" :
						GoalDrivenDFGUtils.displayPriority(lowLevelDFG, inputs.get(GoalDrivenObject.high_priority_acts),
								inputs.get(GoalDrivenObject.low_priority_acts));
						break;
					case "None" :
						GoalDrivenDFGUtils.displayNoneAdditional(lowLevelDFG);
						break;
					default :
						break;

				}
			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

		// update the hierarchy config
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.selected_unique_values,
						GoalDrivenObject.unselected_unique_values };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				//				AttributeClassifier[] allUniqueValues = inputs.get(GoalDrivenObject.all_unique_values);
				AttributeClassifier[] selectedUniqueValues = inputs.get(GoalDrivenObject.selected_unique_values);
				AttributeClassifier[] unselectedUniqueValues = inputs.get(GoalDrivenObject.unselected_unique_values);

				panel.getConfigCards().getActDisplayPanel().updateConfigTable(
						panel.getConfigCards().getActDisplayPanel().getIncludeTable(), selectedUniqueValues, "Exclude");
				panel.getConfigCards().getActDisplayPanel().updateConfigTable(
						panel.getConfigCards().getActDisplayPanel().getExcludeTable(), unselectedUniqueValues,
						"Include");

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

	}

	protected void initStatPanel() {
		// show case button for path
		panel.getSidePanel().getStatisticPanel().getStatisticPathPanel().getShowCaseButton()
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// show case config panel
						panel.getConfigCards().setBounds(0, 0, (int) (0.6 * panel.getConfigCards().getsWidth()), 500);
						panel.getConfigCards().setVisible(true);
						panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "8");
						// update filter panel
						EdgeObject edgeObject = panel.getSidePanel().getStatisticPanel().getEdgeObject();
						// find the trace index
						EdgeHashTable edgeHashTable;
						Set<Integer> displayIndex = new HashSet<Integer>();
						if (HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable.getEdgeTable()
								.containsKey(edgeObject)) {
							edgeHashTable = HIGH_MakeHighLevelLog.currentHighLevelEdgeHashTable;
						} else {
							edgeHashTable = LOW_MakeLowLevelLog.currentLowLevelEdgeHashTable;
						}
						if (edgeHashTable.getEdgePositions(edgeObject) != null) {
							displayIndex = edgeHashTable.getEdgePositions(edgeObject).keySet();
						}
						panel.getConfigCards().getCaseConfigPanel().updateFilterOnPath(edgeObject, displayIndex);

					}
				});

		// display the stat details about the activity/edge
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Display the selected act on the high level graph";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.selected_object };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				SelectedObject selectedObject = inputs.get(GoalDrivenObject.selected_object);
				if (selectedObject.getSelectedAct() != null) {
					String selectedAct = selectedObject.getSelectedAct();
					Map<String, String> frequencyMap = StatUtils.getFrequencyStatForAct(selectedAct);
					List<List<Object[]>> throughputData = StatUtils.getThroughputStatForAct(selectedAct);
					List<Object[]> waitingActData = throughputData.get(0);
					List<Object[]> leadingActData = throughputData.get(1);
					// update stat panel
					panel.getSidePanel().getStatisticPanel().createStatisticPanelForActivity(selectedAct, frequencyMap,
							waitingActData, leadingActData);

				} else {
					EdgeObject edgeObject = selectedObject.getSelectedEdgeObject();
					Map<String, String> frequencyMap = StatUtils.getFrequencyStatForPath(edgeObject);
					Map<String, String> throughputMap = StatUtils.getThroughputStatForPath(edgeObject);
					// update stat panel
					panel.getSidePanel().getStatisticPanel().createStatisticPanelForPath(edgeObject, frequencyMap,
							throughputMap);
				}
				// open stat panel if closed
				String label = panel.getControlBar().getExpandButton().getText().split(" ")[0];
				if (label.equals("Expand")) {
					panel.getControlBar().getExpandButton().doClick();
				}
				panel.revalidate();
				panel.repaint();

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});
	}

	private <C> void updateObjectInGui(final IvMObject<C> object, final C value, final boolean fixed) {
		if (object.equals(IvMObject.selected_miner)) {
		} else if (object.equals(IvMObject.model) && fixed) {
		} else if (object.equals(GoalDrivenObject.selected_classifier1) && fixed) {
			//			panel.getEditModelButton().setVisible(false);
		} else if (object.equals(GoalDrivenObject.selected_classifier1)
				|| object.equals(GoalDrivenObject.classifier_for_gui1)) {
		} else if (object.equals(IvMObject.selected_noise_threshold)) {
			//			panel.getPathsSlider().setValue((Double) value);
		} else if (object.equals(IvMObject.selected_activities_threshold)) {
			//			panel.getActivitiesSlider().setValue((Double) value);
		} else if (object.equals(IvMObject.selected_visualisation_mode)) {
			//			panel.getVisualisationModeSelector().setSelectedItem(value);
		}
		//		

	}

	public static void debug(Object s) {
		System.out.println(s);
	}

	public GoalDrivenPanel getPanel() {
		return panel;
	}

	public <C> void setObject(IvMObject<C> object, C value) {
		updateObjectInGui(object, value, false);
		chain.setObject(object, value);
	}

	public <C> void setFixedObject(IvMObject<C> object, C value) {
		System.out.println("set fix object " + value.toString());
		updateObjectInGui(object, value, true);
		chain.setFixedObject(object, value);
	}
}
