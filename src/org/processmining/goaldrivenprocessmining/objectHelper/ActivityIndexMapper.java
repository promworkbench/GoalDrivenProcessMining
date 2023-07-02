package org.processmining.goaldrivenprocessmining.objectHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityIndexMapper implements Serializable {
	private HashMap<Integer, String> indexToActivityMap;

	public ActivityIndexMapper() {
		this.indexToActivityMap = new HashMap<>();
	}

	public ActivityIndexMapper(HashMap<Integer, String> indexToActivityMap) {
		this.indexToActivityMap = indexToActivityMap;
	}

	public void assignActivity(List<String> actList) {
		int index = this.indexToActivityMap.size();
		for (String act : actList) {
			this.indexToActivityMap.put(index, act);
			index++;
		}
	}

	public int getIndexFromActivity(String act) throws Exception {
		for (Map.Entry<Integer, String> entry : this.indexToActivityMap.entrySet()) {
			if (act.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new Exception("Unknown act");
	}

	public String getActivityFromIndex(int index) throws Exception {
		for (Map.Entry<Integer, String> entry : this.indexToActivityMap.entrySet()) {
			if (index == entry.getKey()) {
				return entry.getValue();
			}
		}
		throw new Exception("Unknown index");
	}
	public Boolean isAssigned(String act) {
		try {
			int index = this.getIndexFromActivity(act);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
