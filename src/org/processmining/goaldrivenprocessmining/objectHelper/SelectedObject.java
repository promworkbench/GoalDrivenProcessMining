package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedObject {
	private String selectedAct;
	private EdgeObject selectedEdgeObject;
	private Boolean isHighLevel;
	private Boolean isLowLevel;

	public SelectedObject(String selectedAct, EdgeObject selecEdgeObject, Boolean isHighLevel, Boolean isLowLevel) {
		this.selectedAct = selectedAct;
		this.selectedEdgeObject = selecEdgeObject;
		this.isHighLevel = isHighLevel;
		this.isLowLevel = isLowLevel;
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

	public Boolean getIsLowLevel() {
		return isLowLevel;
	}

	public void setIsLowLevel(Boolean isLowLevel) {
		this.isLowLevel = isLowLevel;
	}

}
