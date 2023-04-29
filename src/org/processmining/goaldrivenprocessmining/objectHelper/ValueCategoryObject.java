package org.processmining.goaldrivenprocessmining.objectHelper;

import java.awt.Color;

public class ValueCategoryObject {
	private String category;
	private String valueName;
	private Color valueColor;

	public ValueCategoryObject(String category, String valueName, Color valueColor) {
		this.category = category;
		this.valueName = valueName;
		this.valueColor = valueColor;
	}

	public String toString() {
		return this.valueName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public Color getValueColor() {
		return valueColor;
	}

	public void setValueColor(Color valueColor) {
		this.valueColor = valueColor;
	}

	
}
