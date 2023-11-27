package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;


public class ModePanel extends JPanel {
	
	private List<CategoryObject> listCategories;
	private List<JCheckBox> listCategoriesCheckBox;
	private JCheckBox categoryCheckBox;
	private final JPanel catePanel;
	private final JButton modeCancelButton;
	private final JButton modeDoneButton;

	public ModePanel(int width) {
//		setBackground(new Color(18, 18, 18));
		double modeConfigSize[][] = { { 0.25*width, 0.25*width },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM} };
		setLayout(new TableLayout(modeConfigSize));
		this.listCategories = new ArrayList<>();
		this.listCategoriesCheckBox = new ArrayList<>();
		categoryCheckBox = new JCheckBox("Category");
		
		
		catePanel = new JPanel();
		catePanel.setEnabled(false);
		catePanel.setBackground(Color.WHITE);
//		catePanel.setPreferredSize(new Dimension((int) 0.45*width, 200));
		
		JPanel modeEndPanel = new JPanel();
		modeEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		modeCancelButton = new JButton("Cancel");
		modeDoneButton = new JButton("Done");
		modeEndPanel.add(modeCancelButton);
		modeEndPanel.add(modeDoneButton);
		
		add(categoryCheckBox, "0, 0");
		add(catePanel, "0, 1, 1, 1");
		add(modeEndPanel, "0, 2, 1, 2");
	}
	
	public void addCategory(CategoryObject category) {
		this.listCategories.add(category);
	}
	
	public void deleteCategory(CategoryObject category) {
		for (CategoryObject cate: this.listCategories) {
			if (cate.equals(category)) {
				this.listCategories.remove(cate);
				break;
			}
		}
	}

	public JButton getModeCancelButton() {
		return modeCancelButton;
	}

	public JButton getModeDoneButton() {
		return modeDoneButton;
	}
	
	public void updateCategoryPanel() {
		this.catePanel.removeAll();
		for (int i = 0; i < this.listCategoriesCheckBox.size(); i++) {
			this.listCategoriesCheckBox.remove(i);
		}
		for (CategoryObject cate: this.listCategories) {
			JCheckBox cB = new JCheckBox(cate.getName());
			cB.setBackground(Color.WHITE);
			cB.setEnabled(false);
			this.catePanel.add(cB);
			this.listCategoriesCheckBox.add(cB);
			this.configCategoryCheckBox();
		}
		this.revalidate();
		this.repaint();
	}
	
	public void configCategoryCheckBox() {
		for (final JCheckBox cB: this.listCategoriesCheckBox) {
			cB.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					if (cB.isSelected()) {
						for (final JCheckBox cB2: listCategoriesCheckBox) {
							if (!cB2.equals(cB)) {
								cB2.setSelected(false);
							}
						}
					}
				}
				
			});
		}
	}

	public JCheckBox getCategoryCheckBox() {
		return categoryCheckBox;
	}

	public List<JCheckBox> getListCategoriesCheckBox() {
		return listCategoriesCheckBox;
	}

	public List<CategoryObject> getListCategories() {
		return listCategories;
	}
	
	
	
}
