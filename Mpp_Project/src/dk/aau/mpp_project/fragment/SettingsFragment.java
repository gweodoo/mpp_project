package dk.aau.mpp_project.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.LogInActivity;


public class SettingsFragment extends Fragment {
	
	private Button logoutButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_settings, container, false);

<<<<<<< HEAD:Mpp_Project/src/dk/aau/mpp_project/activity/MainActivity.java
	protected static final String	TAG	= "MainActivity";
	private Button					logoutButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		logoutButton = (Button) findViewById(R.id.logoutButton);
=======
		logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
>>>>>>> Tabs:Mpp_Project/src/fragments/SettingsFragment.java
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		} else {
			startLoginActivity();
		}
        return rootView;
    }
    
    
	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {

		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}

	private void startLoginActivity() {
		Intent intent = new Intent(getActivity(), LogInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().finish();
	}
<<<<<<< HEAD:Mpp_Project/src/dk/aau/mpp_project/activity/MainActivity.java

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// Create a JSON object to hold the profile info
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object
								userProfile.put("facebookId", user.getId());
								
								Log.v(TAG, "# Facebook ID : " + user.getId());
								
								userProfile.put("name", user.getName());
								
								Log.v(TAG, "# Name : " + user.getName());
								
								if (user.getBirthday() != null) {
									userProfile.put("birthday",
											user.getBirthday());
								}

								Log.v(TAG, "# JSON : " + userProfile);

								// Save the user profile info in a user property
								// ParseUser currentUser = ParseUser
								// .getCurrentUser();
								// currentUser.put("profile", userProfile);
								// currentUser.saveInBackground();

								// Show the user info
								// updateViewsWithProfileInfo();
							} catch (JSONException e) {
								Log.d(TAG, "Error parsing returned user data.");
							}

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(TAG,
										"The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.d(TAG, "Some other error: "
										+ response.getError().getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();

	}
}
=======
}
>>>>>>> Tabs:Mpp_Project/src/fragments/SettingsFragment.java
