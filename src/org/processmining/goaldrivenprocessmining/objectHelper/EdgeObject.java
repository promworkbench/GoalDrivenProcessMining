package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.Objects;

public class EdgeObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8507054403422044697L;
	private String node1;
	private String node2;
	private Boolean isIndirected;

	public EdgeObject(String node1, String node2, Boolean isIndirected) {
		this.node1 = node1;
		this.node2 = node2;
		this.isIndirected = isIndirected;
	}

	public EdgeObject(String node1, String node2) {
		this.node1 = node1;
		this.node2 = node2;
		this.isIndirected = false;
	}

	public String getNode1() {
		return node1;
	}

	public void setNode1(String node1) {
		this.node1 = node1;
	}

	public String getNode2() {
		return node2;
	}

	public void setNode2(String node2) {
		this.node2 = node2;
	}

	public Boolean getIsIndirected() {
		return isIndirected;
	}

	public void setIsIndirected(Boolean isIndirected) {
		this.isIndirected = isIndirected;
	}

	public int hashCode() {
		return Objects.hash(node1, node2);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdgeObject other = (EdgeObject) obj;
		return Objects.equals(node1, other.node1) && Objects.equals(node2, other.node2);
	}

	public String toString() {
		return "EdgeObject [node1=" + node1 + ", node2=" + node2 + ", isIndirected=" + isIndirected + "]";
	}
	
}