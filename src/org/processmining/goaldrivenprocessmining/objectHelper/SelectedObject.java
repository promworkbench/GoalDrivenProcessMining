package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedObject {
	private String selectedAct;
	private EdgeObject selectedEdgeObject;
	private Boolean isHighLevel;

	public SelectedObject(String selectedAct, EdgeObject selecEdgeObject, Boolean isHighLevel) {
		this.selectedAct = selectedAct;
		this.selectedEdgeObject = selecEdgeObject;
		this.isHighLevel = isHighLevel;
	}

	public String getSelectedAct() {
		return selectedAct;
	}

	public void setSelectedAct(String selectedAct) {
		this.selectedAct = selectedAct;
	}

	public EdgeObject getSelectedEdgeObject() {
		return selectedEdgeObject;
	}

	public void setSelectedEdgeObject(EdgeObject selecEdgeObject) {
		this.selectedEdgeObject = selecEdgeObject;
	}

	public Boolean getIsHighLevel() {
		return isHighLevel;
	}

	public void setIsHighLevel(Boolean isHighLevel) {
		this.isHighLevel = isHighLevel;
	}
}
