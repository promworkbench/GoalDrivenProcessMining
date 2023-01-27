package org.processmining.goaldrivenprocessmining.objectHelper;

import java.awt.Color;

public class GroupActObject {
	private String groupName;
	private Color groupColor;

	public GroupActObject(String groupName, Color groupColor) {
		this.groupName = groupName;
		this.groupColor = groupColor;
	}

	public String getGroupName() {
		return groupName;
	}

	public Color getGroupColor() {
		return groupColor;
	}

	public String toString() {
		return this.groupName;
	}

}
