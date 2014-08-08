package dk.aau.mpp_project.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.model.Operation;
import dk.aau.mpp_project.model.MyUser;

public class MyApplication extends Application {

	// Parse parameters
	public static final String			CHANNEL		= "colloc";
	private static final String			ID_APP		= "S0ckBwXaRNgx9B87f5VE5GZGwUggjOVvj7NNLsAh";
	private static final String			ID_CLIENT	= "hBicP6lpgj85DPwtgCnwBF8ZWs0xeNFwljqiJNqg";

	private static SharedPreferences	sharedPref;

	@Override
	public void onCreate() {
		super.onCreate();

		initParse();

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	private void initParse() {
		Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

		ParseObject.registerSubclass(Flat.class);
		ParseObject.registerSubclass(News.class);
		ParseObject.registerSubclass(Operation.class);
		ParseUser.registerSubclass(MyUser.class);

		// Needed to initialize Parse
		Parse.initialize(this, ID_APP, ID_CLIENT);
		// Needed for Facebook Login
		ParseFacebookUtils.initialize(getString(R.string.app_id));

		// Needed for push notifications
		PushService.setDefaultPushCallback(this, MainActivity.class);
		PushService.subscribe(this, CHANNEL, MainActivity.class);

		// Installation of the phone (each phone as a unique installation)
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		// If there is a connection, the installation is recorded in the Parse
		// Database.
		pi.saveEventually();
	}

	public static SharedPreferences getSharedPref() {
		return sharedPref;
	}

	public static String getOption(String key, String defaultValue) {
		return getSharedPref().getString(key, defaultValue);
	}

	public static void setOption(String key, String value) {
		getSharedPref().edit().putString(key, value).commit();
	}

	public static void setOption(String key, Boolean value) {
		getSharedPref().edit().putBoolean(key, value).commit();
	}

	public static void setOption(String key, Integer value) {
		getSharedPref().edit().putInt(key, value).commit();
	}

	public static void setOption(String key, Float value) {
		getSharedPref().edit().putFloat(key, value).commit();
	}
}
