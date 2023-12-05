package org.processmining.goaldrivenprocessmining.objectHelper;

public class SelectedObject {
	private String selectedAct;
	private EdgeObject selecEdgeObject;

	public SelectedObject(String selectedAct, EdgeObject selecEdgeObject) {
		this.selectedAct = selectedAct;
		this.selecEdgeObject = selecEdgeObject;
	}

	public String getSelectedAct() {
		return selectedAct;
	}

	public void setSelectedAct(String selectedAct) {
		this.selectedAct = selectedAct;
	}

	public EdgeObject getSelecEdgeObject() {
		return selecEdgeObject;
	}

	public void setSelecEdgeObject(EdgeObject selecEdgeObject) {
		this.selecEdgeObject = selecEdgeObject;
	}

}
