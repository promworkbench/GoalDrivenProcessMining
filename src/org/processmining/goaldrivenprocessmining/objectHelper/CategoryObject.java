package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.List;
import java.util.Objects;

public class CategoryObject {

	private String name;
	private List<ValueCategoryObject> values;

	public CategoryObject(String name, List<ValueCategoryObject> values) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ValueCategoryObject> getValues() {
		return values;
	}

	public void setValues(List<ValueCategoryObject> values) {
		this.values = values;
	}

	public int hashCode() {
		return Objects.hash(name, values);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryObject other = (CategoryObject) obj;
		return Objects.equals(name, other.name) && Objects.equals(values, other.values);
	}

	public String toString() {
		return this.name;
	}
	
	
}
