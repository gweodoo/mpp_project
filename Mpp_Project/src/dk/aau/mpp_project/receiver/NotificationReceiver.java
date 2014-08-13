package dk.aau.mpp_project.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.LogInActivity;

public class NotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		try {
			JSONObject json = new JSONObject(intent.getExtras().getString(
					"com.parse.Data"));

			Log.v("TAG", "#### " + action);
			Log.v("TAG", json.getString("msg"));

			if ("colloc.action.push".equals(action)) {
				createNotification(context, json.getString("msg"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void createNotification(Context context, String msg) {
		final NotificationManager mNotification = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final Intent launchNotifiactionIntent = new Intent(context,
				LogInActivity.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0, launchNotifiactionIntent, PendingIntent.FLAG_ONE_SHOT);

		Notification.Builder builder = new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_stat_icon)
				.setContentTitle(
						context.getResources().getString(R.string.app_name))
				.setContentText(msg).setContentIntent(pendingIntent);

		Notification noti = builder.build();

		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotification.notify(0, noti);
	}

}
