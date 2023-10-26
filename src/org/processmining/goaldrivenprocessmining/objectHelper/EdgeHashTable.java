package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeHashTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7131839169691770596L;
	private Map<EdgeObject, Map<Integer, List<Integer[]>>> edgeTable;

	public EdgeHashTable() {
		this.edgeTable = new HashMap<>();
	}

	public void addEdge(EdgeObject edge, Map<Integer, List<Integer[]>> edgeTable) {
		for (Map.Entry<Integer, List<Integer[]>> entry : edgeTable.entrySet()) {
			for (Integer[] i : entry.getValue()) {
				this.addEdge(edge, entry.getKey(), i[0], i[1]);
			}
		}
	}

	public void addEdge(EdgeObject edge, int caseNumber, int source, int target) {
		Map<Integer, List<Integer[]>> caseTable = edgeTable.get(edge);
		if (caseTable == null) {
			caseTable = new HashMap<>();
			edgeTable.put(edge, caseTable);
		}
		List<Integer[]> positions = caseTable.get(caseNumber);
		if (positions == null) {
			positions = new ArrayList<>();
			caseTable.put(caseNumber, positions);
		}
		positions.add(new Integer[] { source, target });
	}

	public Map<Integer, List<Integer[]>> getEdgePositions(EdgeObject edge) {
		Map<Integer, List<Integer[]>> caseTable = edgeTable.get(edge);
		if (caseTable != null) {
			return caseTable;
		}
		return null;
	}

	public Map<EdgeObject, Map<Integer, List<Integer[]>>> getEdgeTable() {
		return edgeTable;
	}

	public void setEdgeTable(Map<EdgeObject, Map<Integer, List<Integer[]>>> edgeTable) {
		this.edgeTable = edgeTable;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EdgeHashTable: {\n");
		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : edgeTable.entrySet()) {
			sb.append("EdgeObject: ").append(entry.getKey()).append(" \n=>\n  {\n");
			for (Map.Entry<Integer, List<Integer[]>> innerEntry : entry.getValue().entrySet()) {
				sb.append("  Trace: ").append(innerEntry.getKey()).append(" => ");
				sb.append("Pos: [");
				for (Integer[] array : innerEntry.getValue()) {
					sb.append(arrayToString(array)).append(", ");
				}
				sb.append("]\n");
			}
			sb.append("  }\n");
		}
		sb.append("}");
		return sb.toString();
	}

	private String arrayToString(Integer[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i < array.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
