package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.Objects;

public class EdgeObject {
	
	private String node1;
	private String node2;
	public EdgeObject(String node1, String node2) {
		this.node1 = node1;
		this.node2 = node2;
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
		return "EdgeObject [node1=" + node1 + ", node2=" + node2 + "]";
	}
	
	
	
}