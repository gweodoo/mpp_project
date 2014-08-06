package dk.aau.mpp_project.event;

import android.os.Bundle;

public class FinishedEvent {

	private boolean	isSuccess;
	private String	action;
	private Bundle	extras;

	public FinishedEvent(boolean isSuccess, String action, Bundle extras) {
		this.isSuccess = isSuccess;
		this.action = action;
		this.extras = extras;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Bundle getExtras() {
		return extras;
	}

	public void setExtras(Bundle extras) {
		this.extras = extras;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
