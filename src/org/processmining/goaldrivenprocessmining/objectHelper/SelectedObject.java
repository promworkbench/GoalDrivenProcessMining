package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedObject {
	private String selectedAct;
	private EdgeObject selectedEdgeObject;

	public SelectedObject(String selectedAct, EdgeObject selecEdgeObject) {
		this.selectedAct = selectedAct;
		this.selectedEdgeObject = selecEdgeObject;
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

}
