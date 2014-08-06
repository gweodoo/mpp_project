package dk.aau.mpp_project.event;

public class StartEvent {
	
	private String action;
	
	public StartEvent(String action) {
		this.setAction(action);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
