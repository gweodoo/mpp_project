package dk.aau.mpp_project.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;

public class LogInActivity extends Activity {

	protected static final String	TAG	= "LoginActivity";

	private Button					loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initView();
		initListener();

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();

		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
//			myUser = new MyUser(currentUser.getString(MyUser.FACEBOOK_ID),
//					currentUser.getString(MyUser.NAME),
//					currentUser.getString(MyUser.BIRTHDAY));
//			myUser.setObjectId(currentUser.getObjectId());
//
//			DatabaseHelper.getUserFlats(myUser);
			
			Intent intent = new Intent(this, NewFlatActivity.class);
			startActivity(intent);
			
//			goToNewFlatActivity();
//			goToMainActivity(new Flat());

		}
	}

	private void initView() {
		loginButton = (Button) findViewById(R.id.loginButton);
	}

	private void initListener() {
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {

		EventBus.getDefault().post(new StartEvent(DatabaseHelper.ACTION_LOGIN));

		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_birthday");

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {

				if (user == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(TAG, "User signed up and logged in through Facebook!");
					makeMeRequest();
				} else {
					Log.d(TAG, "User logged in through Facebook!");

					makeMeRequest();
				}
			}
		});
	}

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {

							final MyUser myUser = new MyUser(user.getId(), user
									.getName(), user.getBirthday());

							Log.v(TAG,
									"# FACEBOOK ID : " + myUser.getFacebookId());

							myUser.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {

									if (progressDialog != null
											&& progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									MyApplication.setOption(MyUser.FACEBOOK_ID,
											myUser.getFacebookId());
									MyApplication.setOption(MyUser.NAME,
											myUser.getName());
									MyApplication.setOption(MyUser.BIRTHDAY,
											myUser.getBirthday());

									goToMainActivity();
								}
							});

						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(TAG,
										"The facebook session was invalidated.");
							} else {
								Log.d(TAG, "Some other error: "
										+ response.getError().getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void goToNewFlatActivity() {
		Intent intent = new Intent(this, NewFlatActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		EventBus.getDefault().unregister(this);
	}

	private ProgressDialog	progressDialog;

	public void onEventMainThread(StartEvent e) {
		Log.d(TAG, "# StartEvent");

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
		}

		if (progressDialog != null && !progressDialog.isShowing())
			progressDialog.show();
	}

	public void onEventMainThread(FinishedEvent e) {
		Log.d(TAG, "# FinishedEvent");

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		// Success retreiving database
		if (e.isSuccess()) {
			Log.i(TAG, "# Request Success");

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_GET_USER_FLATS.equals(e.getAction())) {
				// You know what you need (List or simple object)
				// If List: ArrayList<Flat> flatsList =
				// e.getExtras().getParcelableArrayList("data");
				// If Object only : Flat flat =
				// e.getExtras().getParcelable("data");

//				ArrayList<Flat> flatsList = e.getExtras()
//						.getParcelableArrayList("data");

				// if (flatsList.size() == 0) {
				// goToNewFlatActivity();
				// } else if (flatsList.size() == 1) {
				// goToMainActivity(flatsList.get(0));
				// } else if (flatsList.size() > 1) {
				// goToNewFlatActivity(flatsList);
				// }

				// for (Flat f : flatsList)
				// Log.v(TAG,
				// "# Flat : " + f.getName() + " : "
				// + f.getRentAmount() + "$");

			} else if (DatabaseHelper.ACTION_GET_NEWS_FLATS.equals(e
					.getAction())) {

			} else if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e
					.getAction())) {

			}
		}
		// Error occured
		else {
			Log.i(TAG, "# Request NOT Success");
		}
	}
}
