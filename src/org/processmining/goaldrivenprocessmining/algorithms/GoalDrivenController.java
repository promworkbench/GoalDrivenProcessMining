package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.chain.GoalDrivenObject;
import org.processmining.goaldrivenprocessmining.algorithms.panel.GoalDrivenPanel;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.ValueCategoryObject;
import org.processmining.goaldrivenprocessmining.panelHelper.GroupActConfig;
import org.processmining.goaldrivenprocessmining.panelHelper.NewCategoryPanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkGuiAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.UserStatus;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.Miner;

import graph.controls.EdgeClickControl;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class GoalDrivenController {
	private final GoalDrivenPanel panel;
	private final GoalDrivenConfiguration configuration;
	private final DataChain<GoalDrivenConfiguration> chain;
	private final PluginContext context;
	private final UserStatus userStatus;

	public GoalDrivenController(final PluginContext context, final GoalDrivenConfiguration configuration,
			final XLog log, final ProMCanceller canceller) {

		this.configuration = configuration;
		this.panel = configuration.getPanel();
		this.userStatus = new UserStatus();
		this.context = context;
		chain = configuration.getChain();

		//initialise gui handlers
		initGui(canceller, configuration);

		//set up the controller view
		chain.setOnChange(new Runnable() {
			public void run() {
				panel.getControllerView().pushCompleteChainLinks(chain);
			}
		});
		//start the chain
		chain.setFixedObject(IvMObject.input_log, log);
	}

	protected void initGui(final ProMCanceller canceller, final GoalDrivenConfiguration configuration) {

		initGuiClassifiers1();

		initGuiUniqueValue();

		initGuiControlBar();

		initGuiMiner();

		GoalDrivenExportController.initialise(chain, configuration, panel);

		initGuiGraph();
		initGuiGraph1();

	}

	protected void initGuiMiner() {
		//miner
		setObject(IvMObject.selected_miner, new Miner());
		chain.setObject(IvMObject.selected_miner, (VisualMinerWrapper) new DfgMiner());
		//		panel.getMinerSelection().addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent arg0) {
		//				
		//			}
		//		});

		//noise threshold
		setObject(IvMObject.selected_noise_threshold, 1.0);
		chain.setObject(IvMObject.selected_noise_threshold, 1.0);
		//		panel.getPathsSlider().addChangeListener(new ChangeListener() {
		//			public void stateChanged(ChangeEvent e) {
		//				if (!panel.getPathsSlider().getSlider().getValueIsAdjusting()) {
		//					chain.setObject(IvMObject.selected_noise_threshold, panel.getPathsSlider().getValue());
		//				}
		//			}
		//		});

	}

	protected void initGuiGraph() {
		System.out.println(chain);
		panel.getGraph().addControlListener(new EdgeClickControl(chain));
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "model dot";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.high_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				//				if (inputs.has(IvMObject.graph_svg_aligned) && inputs.has(IvMObject.graph_dot_aligned)) {
				if (inputs.has(GoalDrivenObject.high_level_dfg)) {
					panel.getGraph().updateDFG(inputs.get(GoalDrivenObject.high_level_dfg));
					panel.revalidate();
					panel.repaint();
				}
				//				else {
				//					System.out.println("--- not in");
				//					Dot dot = inputs.get(IvMObject.graph_dot);
				//					File t = new File("C:\\D\\data\\abc");
				//					dot.exportToFile(t);
				//					SVGDiagram svg = inputs.get(IvMObject.graph_svg);
				//					panel.getGraph().changeDot(dot, svg, true);
				//				}
			}

			public void invalidate(GoalDrivenPanel panel) {
			}
		});

	}

	protected void initGuiGraph1() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {
			public String getName() {
				return "edge dfg model dot";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.low_level_dfg };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				if (inputs.has(GoalDrivenObject.low_level_dfg)) {
					System.out.println("asdf low");
					panel.getGraph2().updateDFG(inputs.get(GoalDrivenObject.low_level_dfg));
					panel.revalidate();
					panel.repaint();
				}
				//				else {
				//					Dot dot = inputs.get(GoalDrivenObject.graph_dot_edge);
				//					SVGDiagram svg = inputs.get(GoalDrivenObject.graph_svg_edge);
				//					panel.getGraph2().changeDot(dot, svg, true);
				//
				//				}
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

	protected void initGuiClassifiers1() {
		//get the selected classifier to the gui
		System.out.println("init gui classifier");
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "classifier to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.classifier_for_gui1 };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] value = inputs.get(GoalDrivenObject.classifier_for_gui1);
				//				panel.getClassifiers1().getMultiComboBox().setSelectedItems(value);
				panel.getClassifiers1().getMultiComboBox().setSelectedItem(value[0]);

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

		//update data on classifiers
		panel.getClassifiers1().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chain.setObject(GoalDrivenObject.selected_classifier1,
						panel.getClassifiers1().getSelectedClassifier()[0]);
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
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				double sWidth = screenSize.getWidth();
				double size[][] = { { 0.5 * sWidth, 0.5 * sWidth },
						{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
				panel.setLayout(new TableLayout(size));
				panel.add(panel.getControlBar(), "0,0,1,0");
				panel.add(panel.getLayeredPanel(), "0,1,1,1");
				if (label.equals("Collapse")) {
					double sizeContent[][] = { { 0.5 * sWidth, 0.5 * sWidth }, { TableLayoutConstants.FILL } };
					panel.getContentPanel().setLayout(new TableLayout(sizeContent));
					panel.getContentPanel().add(panel.getGraph(), "0,0");
					panel.getContentPanel().add(panel.getGraph2(), "1,0");
					panel.getControlBar().getExpandButton().setText("Expand stat window");
				} else {
					double sizeContent[][] = { { 0.37 * sWidth, 0.37 * sWidth, 0.26 * sWidth },
							{ TableLayoutConstants.FILL } };
					panel.getContentPanel().setLayout(new TableLayout(sizeContent));
					panel.getContentPanel().add(panel.getGraph(), "0,0");
					panel.getContentPanel().add(panel.getGraph2(), "1,0");
					panel.getContentPanel().add(panel.getSidePanel(), "2,0");
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

		// act done button		
		panel.getConfigCards().getActDisplayPanel().getActDoneButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				AttributeClassifier[] attInclude = new AttributeClassifier[panel.getConfigCards().getActDisplayPanel()
						.getIncludeTable().getModel().getRowCount()];
				for (int i = 0; i < attInclude.length; i++) {
					AttributeClassifier att = (AttributeClassifier) panel.getConfigCards().getActDisplayPanel()
							.getIncludeTable().getValueAt(i, 0);
					attInclude[i] = att;
					if (!panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().keySet().contains(att)) {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().put(att, "include");
					} else {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().replace(att, "include");
					}

				}
				AttributeClassifier[] attExclude = new AttributeClassifier[panel.getConfigCards().getActDisplayPanel()
						.getExcludeTable().getModel().getRowCount()];
				for (int i = 0; i < attExclude.length; i++) {
					AttributeClassifier att = (AttributeClassifier) panel.getConfigCards().getActDisplayPanel()
							.getExcludeTable().getValueAt(i, 0);
					attExclude[i] = att;
					if (!panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().keySet().contains(att)) {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().put(att, "exclude");
					} else {
						panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().replace(att, "exclude");
					}
				}
				panel.getConfigCards().setVisible(false);
				chain.setObject(GoalDrivenObject.selected_unique_values, attInclude);
				chain.setObject(GoalDrivenObject.unselected_unique_values, attExclude);

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
		// update group act
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
				newCate.getValues().forEach(cmb::addItem);
				// add new column + config that column
				DefaultTableModel model = (DefaultTableModel) panel.getConfigCards().getActConfigPanel()
						.getActConfigTable().getModel();
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
									valueCategories.add(new ValueCategoryObject(actConfigTable.getColumnName(i), "", Color.WHITE));
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

	}

	protected void initGuiUniqueValue() {
		//get the selected classifier to the gui
		chain.register(new DataChainLinkGuiAbstract<GoalDrivenConfiguration, GoalDrivenPanel>() {

			public String getName() {
				return "Unique value to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { GoalDrivenObject.all_unique_values };
			}

			public void updateGui(GoalDrivenPanel panel, IvMObjectValues inputs) throws Exception {
				AttributeClassifier[] allUniqueValues = inputs.get(GoalDrivenObject.all_unique_values);
				List<AttributeClassifier> lAttInclude = new ArrayList<>();
				List<AttributeClassifier> lAttExclude = new ArrayList<>();
				TableModel includeModel = panel.getConfigCards().getActDisplayPanel().getIncludeTable().getModel();
				for (int i = 0; i < includeModel.getRowCount(); i++) {
					AttributeClassifier att = (AttributeClassifier) includeModel.getValueAt(i, 0);
					if (Arrays.asList(allUniqueValues).contains(att)) {
						lAttInclude.add((AttributeClassifier) includeModel.getValueAt(i, 0));
					}
				}
				TableModel excludeModel = panel.getConfigCards().getActDisplayPanel().getExcludeTable().getModel();
				for (int i = 0; i < excludeModel.getRowCount(); i++) {
					AttributeClassifier att = (AttributeClassifier) excludeModel.getValueAt(i, 0);
					if (Arrays.asList(allUniqueValues).contains(att)) {
						lAttExclude.add((AttributeClassifier) excludeModel.getValueAt(i, 0));
					}
				}
				for (int i = 0; i < allUniqueValues.length; i++) {
					if (!lAttInclude.contains(allUniqueValues[i]) && !lAttExclude.contains(allUniqueValues[i])) {
						if (panel.getConfigCards().getActDisplayPanel().getMapTableAttribute()
								.containsKey(allUniqueValues[i])) {
							if (panel.getConfigCards().getActDisplayPanel().getMapTableAttribute()
									.get(allUniqueValues[i]).equals("include")) {
								lAttInclude.add(allUniqueValues[i]);
							} else {
								lAttExclude.add(allUniqueValues[i]);
							}
						} else {
							panel.getConfigCards().getActDisplayPanel().getMapTableAttribute().put(allUniqueValues[i],
									"include");
							lAttInclude.add(allUniqueValues[i]);
						}

					}
				}
				AttributeClassifier[] arrAttInclude = new AttributeClassifier[lAttInclude.size()];
				for (int i = 0; i < arrAttInclude.length; i++) {
					arrAttInclude[i] = lAttInclude.get(i);
				}
				AttributeClassifier[] arrAttExclude = new AttributeClassifier[lAttExclude.size()];
				for (int i = 0; i < arrAttExclude.length; i++) {
					arrAttExclude[i] = lAttExclude.get(i);
				}

				panel.getConfigCards().getActDisplayPanel().updateConfigTable(
						panel.getConfigCards().getActDisplayPanel().getIncludeTable(), arrAttInclude, "Exclude");
				panel.getConfigCards().getActDisplayPanel().updateConfigTable(
						panel.getConfigCards().getActDisplayPanel().getExcludeTable(), arrAttExclude, "Include");

				chain.setObject(GoalDrivenObject.selected_unique_values, arrAttInclude);
				chain.setObject(GoalDrivenObject.unselected_unique_values, arrAttExclude);

			}

			public void invalidate(GoalDrivenPanel panel) {
				//no action necessary (combobox will be disabled until new classifiers are computed)
			}
		});

	}

	/**
	 * Sets the status message of number. The status message stays in view until
	 * it is reset using NULL for that number.
	 * 
	 * @param message
	 * @param number
	 */
	//	public void setStatus(String message, int number) {
	//		userStatus.setStatus(message, number);
	//		panel.getStatusLabel().setText(userStatus.getText());
	//		panel.getStatusLabel().repaint();
	//	}
	//

	private <C> void updateObjectInGui(final IvMObject<C> object, final C value, final boolean fixed) {
		if (object.equals(IvMObject.selected_miner)) {
		} else if (object.equals(IvMObject.model) && fixed) {
			//			panel.getActivitiesSlider().setVisible(false);
			//			panel.getPathsSlider().setVisible(false);
			panel.getPreMiningFiltersButton().setVisible(false);
		} else if (object.equals(GoalDrivenObject.selected_classifier1) && fixed) {
			//			panel.getEditModelButton().setVisible(false);
			panel.getClassifierLabel().setVisible(false);
			panel.getClassifiers1().setVisible(false);
		} else if (object.equals(GoalDrivenObject.selected_classifier1)
				|| object.equals(GoalDrivenObject.classifier_for_gui1)) {
			panel.getClassifiers1().getMultiComboBox().setSelectedItem(((AttributeClassifier[]) value)[0]);
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
