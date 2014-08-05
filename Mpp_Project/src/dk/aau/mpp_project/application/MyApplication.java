package dk.aau.mpp_project.application;


import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.PushService;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;

import android.app.Application;

public class MyApplication extends Application {

	// Parse parameters
	public static final String	CHANNEL		= "colloc";
	private static final String	ID_APP		= "S0ckBwXaRNgx9B87f5VE5GZGwUggjOVvj7NNLsAh";
	private static final String	ID_CLIENT	= "hBicP6lpgj85DPwtgCnwBF8ZWs0xeNFwljqiJNqg";

	@Override
	public void onCreate() {
		super.onCreate();
		
		initParse();
	}
	
	private void initParse() {
		Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

		// Needed to initialize Parse
		Parse.initialize(this, ID_APP, ID_CLIENT);
		// Needed for Facebook Login
		ParseFacebookUtils.initialize(getString(R.string.app_id));

		// Needed for push notifications
		PushService.setDefaultPushCallback(this, MainActivity.class);
		PushService.subscribe(this, CHANNEL, MainActivity.class);

		// Installation of the phone (each phone as a unique installation)
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		// If there is a connection, the installation is recorded in the Parse Database.
		pi.saveEventually();
	}
}
