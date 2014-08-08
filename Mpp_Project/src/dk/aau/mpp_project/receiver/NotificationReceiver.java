package dk.aau.mpp_project.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		Log.v("TAG", "#### " + action);

		if ("dk.aau.mpp_project.action.push".equals(action)) {

		}
	}

}
