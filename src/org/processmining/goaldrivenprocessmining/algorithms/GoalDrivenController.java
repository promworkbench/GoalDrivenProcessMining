package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.CONFIG_Update;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.Config;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.SelectedGroup;
import org.processmining.goaldrivenprocessmining.objectHelper.StatNodeObject;
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
import graph.GraphConstants;
import graph.utils.node.GoalDrivenDFGUtils;
import prefuse.Visualization;
import prefuse.visual.VisualItem;

public class GoalDrivenController {
	private static GoalDrivenPanel panel;
	private static DataChain<GoalDrivenConfiguration> chain;

	public GoalDrivenController(final PluginContext context, final GoalDrivenConfiguration configuration,
			final XLog log, final ProMCanceller canceller) {

		this.panel = configuration.getPanel();
		this.chain = configuration.getChain();

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

	public static void expandSelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		SelectedGroup selectedGroup = new SelectedGroup(groupSkeleton, isHighLevel, true);
		chain.setObject(GoalDrivenObject.selected_group, selectedGroup);
	}

	public static void collapseSelectedGroup(GroupSkeleton groupSkeleton, Boolean isHighLevel) {
		SelectedGroup selectedGroup = new SelectedGroup(groupSkeleton, isHighLevel, false);
		chain.setObject(GoalDrivenObject.selected_group, selectedGroup);
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
		//		if (isHighLevel) {
		//			GoalDrivenDFGUtils.addGroupToDFG(panel.getHighDfgPanel(), newGroupSkeleton);
		//
		//		} else {
		//			GoalDrivenDFGUtils.addGroupToDFG(panel.getLowDfgPanel(), newGroupSkeleton);
		//		}
		GoalDrivenDFGUtils.addGroupState(newGroupSkeleton);
		GoalDrivenDFGUtils.updateDfg(panel.getHighDfgPanel());
		// update config
		Config updatedConfig = CONFIG_Update.currentConfig == null ? new Config() : CONFIG_Update.currentConfig;
		updatedConfig.addGroup(newGroupSkeleton);
		CONFIG_Update.currentConfig = updatedConfig;
		chain.setObject(GoalDrivenObject.config, updatedConfig);

	}

	protected void initGui(final ProMCanceller canceller, final GoalDrivenConfiguration configuration) {
		initGuiUniqueValue();
		initGuiControlBar();
		initGuiSidePanel();
		initGuiMiner();
		initHighLevelGraph();
		initLowLevelGraph();
		initGroupGraph();
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
					panel.getContentLeftPanel().remove(panel.getHighDfgPanel());
					panel.setHighDfgPanel(inputs.get(GoalDrivenObject.high_level_dfg));
					panel.getHighDfgPanel().setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
					panel.getHighDfgPanel().setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
					//					panel.getHighDfgPanel().updateDFG(inputs.get(GoalDrivenObject.high_level_dfg));
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
					if (!inputs.get(GoalDrivenObject.low_level_dfg).getLog().getActivityHashTable().getActivityTable().isEmpty()) {
						panel.getContentRightPanel().remove(panel.getLowDfgPanel());
						panel.setLowDfgPanel(inputs.get(GoalDrivenObject.low_level_dfg));
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
						panel.getLowDfgTitle().setText("Low-level DFG:");
						isLowClear = true;
					}
					panel.revalidate();
					panel.repaint();
				}
				if (!isLowClear) {
					if (inputs.has(GoalDrivenObject.selected_source_target_node)) {
						HashMap<String, Object> passValues = inputs.get(GoalDrivenObject.selected_source_target_node);
						String source = (String) passValues.get("source");
						String target = (String) passValues.get("target");
						panel.getLowDfgTitle().setText("Low-level DFG: " + source + " --> " + target);
						panel.revalidate();
						panel.repaint();
					}
				}

			}

			public void invalidate(GoalDrivenPanel panel) {
				//here, we could put the graph on blank, but that is annoying
				//				Dot dot = new Dot();
				//				DotNode dotNode = dot.addNode("...");
				//				dotNode.setOption("shape", "plaintext");
				//				panel.getGraph().changeDot(dot, true);
			}
		});

		//mode switch

	}

	protected void initGroupGraph() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "update the graph of selected group to panel";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg,
						GoalDrivenObject.selected_source_target_node };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {

			}

			public void invalidate(GoalDrivenPanel panel) {
				//here, we could put the graph on blank, but that is annoying
				//				Dot dot = new Dot();
				//				DotNode dotNode = dot.addNode("...");
				//				dotNode.setOption("shape", "plaintext");
				//				panel.getGraph().changeDot(dot, true);
			}
		});
	}

	protected void initGuiControlBar() {

		// mode button
		panel.getControlBar().getModeButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.5 * 0.37 * panel.getConfigCards().getsWidth()), 200);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "1");

			}
		});
		// act button
		panel.getControlBar().getActButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.37 * panel.getConfigCards().getsWidth()), 200);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "4");

			}
		});
		// filter button
		panel.getControlBar().getFilterButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.37 * panel.getConfigCards().getsWidth()), 200);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "2");

			}
		});
		// expand button
		panel.getControlBar().getExpandButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String label = panel.getControlBar().getExpandButton().getText().split(" ")[0];
				panel.setLayout(new BorderLayout());
				panel.add(panel.getControlBar(), BorderLayout.NORTH);
				panel.add(panel.getLayeredPanel(), BorderLayout.CENTER);
				if (label.equals("Collapse")) {
					panel.getContentPanel().setLayout(new GridBagLayout());
					GridBagConstraints gbcHighDfgPanel = GoalDrivenPanel.createGridBagConstraints(0, 0, 0.5);
					panel.getContentLeftPanel().add(panel.getHighDfgPanel());
					panel.getContentPanel().add(panel.getContentLeftPanel(), gbcHighDfgPanel);
					GridBagConstraints gbcLowDfgPanel = GoalDrivenPanel.createGridBagConstraints(1, 0, 0.5);
					panel.getContentRightPanel().add(panel.getLowDfgPanel());
					panel.getContentPanel().add(panel.getContentRightPanel(), gbcLowDfgPanel);
					panel.getControlBar().getExpandButton().setText("Expand stat window");
				} else {
					panel.getContentPanel().setLayout(new GridBagLayout());
					GridBagConstraints gbcHighDfgPanel = GoalDrivenPanel.createGridBagConstraints(0, 0, 0.37);
					panel.getContentLeftPanel().add(panel.getHighDfgPanel());
					panel.getContentPanel().add(panel.getContentLeftPanel(), gbcHighDfgPanel);
					GridBagConstraints gbcLowDfgPanel = GoalDrivenPanel.createGridBagConstraints(1, 0, 0.37);
					panel.getContentRightPanel().add(panel.getLowDfgPanel());
					panel.getContentPanel().add(panel.getContentRightPanel(), gbcLowDfgPanel);
					GridBagConstraints gbcSidePanel = GoalDrivenPanel.createGridBagConstraints(2, 0, 0.24);
					panel.getContentPanel().add(panel.getSidePanel(), gbcSidePanel);
					panel.getControlBar().getExpandButton().setText("Collapse stat window");
				}

				panel.revalidate();
				panel.repaint();

			}

		});
		// act config button
		panel.getControlBar().getActConfigButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setBounds(0, 0, (int) (0.37 * panel.getConfigCards().getsWidth()), 200);
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "3");

			}

		});
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
		// act done button		
		panel.getConfigCards().getActDisplayPanel().getActDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String[] attInclude = new String[panel.getConfigCards().getActDisplayPanel().getIncludeTable()
						.getModel().getRowCount()];
				for (int i = 0; i < attInclude.length; i++) {
					AttributeClassifier att = (AttributeClassifier) panel.getConfigCards().getActDisplayPanel()
							.getIncludeTable().getValueAt(i, 0);
					attInclude[i] = att.toString();
					if (!panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().keySet().contains(att)) {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().put(att, "include");
					} else {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().replace(att, "include");
					}

				}
				String[] attExclude = new String[panel.getConfigCards().getActDisplayPanel().getExcludeTable()
						.getModel().getRowCount()];
				for (int i = 0; i < attExclude.length; i++) {
					AttributeClassifier att = (AttributeClassifier) panel.getConfigCards().getActDisplayPanel()
							.getExcludeTable().getValueAt(i, 0);
					attExclude[i] = att.toString();
					if (!panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().keySet().contains(att)) {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().put(att, "exclude");
					} else {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().replace(att, "exclude");
					}
				}
				panel.getConfigCards().setVisible(false);
				HashMap<String, String[]> updateMap = new HashMap<String, String[]>();
				updateMap.put("High", attInclude);
				updateMap.put("Low", attExclude);
				UpdateConfig updateConfig = new UpdateConfig(UpdateType.SELECTED_ACT, updateMap);

				// update
				chain.setObject(GoalDrivenObject.update_config_object, updateConfig);
				//				panel.getLowDfgTitle().setText("");

			}
		});
		// act cancel button
		panel.getConfigCards().getActDisplayPanel().getActCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.getConfigCards().setVisible(false);
			}
		});
		// filter done button
		// filter cancel button
		panel.getConfigCards().getFilterConfigPanel().getFilterCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel.getConfigCards().setVisible(false);
			}
		});
		// filter act slider
		panel.getConfigCards().getFilterConfigPanel().getActivitiesSlider().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!panel.getConfigCards().getFilterConfigPanel().getActivitiesSlider().getSlider()
						.getValueIsAdjusting()) {
					chain.setObject(IvMObject.selected_activities_threshold,
							panel.getConfigCards().getFilterConfigPanel().getActivitiesSlider().getValue());
				}
			}
		});
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
				panel.getConfigCards().setVisible(true);
				panel.getConfigCards().getLayoutCard().show(panel.getConfigCards(), "6");
			}
		});
		// legend done button
		panel.getConfigCards().getLegendPanel().getDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});

		// legend cancel button
		panel.getConfigCards().getLegendPanel().getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.getConfigCards().setVisible(false);
			}
		});

	}

	protected void initGuiSidePanel() {
		//stat panel in side panel
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "update stat for selected objects";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.stat_selected_node };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				if (inputs.has(GoalDrivenObject.stat_selected_node)) {
					StatNodeObject statNodeObject = inputs.get(GoalDrivenObject.stat_selected_node);
					HashMap<String, String> attributes = new HashMap<String, String>();
					attributes.put("Total occurrences", Integer.toString(statNodeObject.getTotalOccurences()));
					attributes.put("Average occurrences per case", Float.toString(statNodeObject.getAvgOccurences()));
					attributes.put("Average throughput time per case", statNodeObject.getAvgThroughputTime());
					panel.getSidePanel().getStatisticPanel().setContentMap(attributes);
					panel.getSidePanel().getStatisticPanel().updateStatistics();
					panel.revalidate();
					panel.repaint();
				}
			}

			public void invalidate(GoalDrivenPanel panel) {
			}
		});

	}

	protected void initGuiUniqueValue() {
		//get the selected classifier to the gui
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
