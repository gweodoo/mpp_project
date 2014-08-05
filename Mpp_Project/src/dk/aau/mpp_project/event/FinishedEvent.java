package dk.aau.mpp_project.event;

import android.os.Bundle;

public class FinishedEvent {

	private boolean	isSuccess;
	private Bundle	extras;

	public FinishedEvent(boolean isSuccess, Bundle extras) {
		this.isSuccess = isSuccess;
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
}
