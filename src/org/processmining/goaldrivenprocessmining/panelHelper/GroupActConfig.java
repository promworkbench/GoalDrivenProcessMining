package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class GroupActConfig extends JPanel{
	private final int index;
	private String groupName;
	private Color groupColor;
	
	public GroupActConfig(int width, String groupName, Color groupColor, int index) {
		this.index = index;
		double groupActConfigSize[][] = { { 0.5*width, 0.5*width },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(groupActConfigSize));
		this.groupName = groupName;
		this.groupColor = groupColor;
		JLabel nameLabel = new JLabel("Name:");
		add(nameLabel, "0, 0");
		JLabel colorLabel = new JLabel("Color:");
		add(colorLabel, "1, 0");
		JTextField name = new JTextField(groupName);
		name.setEditable(false);
		add(name, "0, 1");
		JPanel color = new JPanel();
		color.setBackground(groupColor);
		add(color, "1,1");
	}

	public int getIndex() {
		return index;
	}

	public String getGroupName() {
		return groupName;
	}

	public Color getGroupColor() {
		return groupColor;
	}
	
}
