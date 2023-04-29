package org.processmining.goaldrivenprocessmining.objectHelper;

import java.util.ArrayList;
import java.util.List;

public class IndirectedEdgeCarrierObject {

	private List<EdgeObject> listIndirectedEdge;

	public IndirectedEdgeCarrierObject() {
		this.listIndirectedEdge = new ArrayList<>();
	}

	public List<EdgeObject> getListIndirectedEdge() {
		return listIndirectedEdge;
	}

	public void addEdge(EdgeObject tupleNode) {
		this.listIndirectedEdge.add(tupleNode);
	}

	public String toString() {
		String res = "[";
		for (EdgeObject ob: this.listIndirectedEdge) {
			res += ob.toString();
			res += ", ";
		}
		res += "]";
		return res;
	}

}
