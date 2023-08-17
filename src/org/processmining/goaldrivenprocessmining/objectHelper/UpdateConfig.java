package org.processmining.goaldrivenprocessmining.objectHelper;

public class UpdateConfig {
	public enum UpdateType {
		SELECTED_ACT, GROUP, CATEGORY, FILTER
	}

	public enum UpdateAction {
		ADD, REMOVE
	}

	private UpdateType updateType;
	private UpdateAction updateAction;
	private Object updateObject;

	public UpdateConfig() {
	}

	public UpdateConfig(UpdateType updateType, Object updateObject) {
		this.updateType = updateType;
		this.updateObject = updateObject;
	}

	public UpdateConfig(UpdateType updateType, UpdateAction updateAction, Object updateObject) {
		this.updateType = updateType;
		this.updateAction = updateAction;
		this.updateObject = updateObject;
	}

	public UpdateAction getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(UpdateAction updateAction) {
		this.updateAction = updateAction;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	public Object getUpdateObject() {
		return updateObject;
	}

	public void setUpdateObject(Object updateObject) {
		this.updateObject = updateObject;
	}

}
