package dk.aau.mpp_project.activity;

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
import com.parse.*;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.MyUser;

import java.util.Arrays;
import java.util.List;

public class LogInActivity extends Activity {

	protected static final String	TAG	= "LoginActivity";
    private ProgressDialog	progressDialog;
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

			Intent intent = new Intent(this, NewFlatActivity.class);
			startActivity(intent);
			finish();
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

									goToNewFlatActivity();
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

	public void onEventMainThread(StartEvent e) {
		Log.d(TAG, "# StartEvent");

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
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
		}
		// Error occured
		else {
			Log.i(TAG, "# Request NOT Success");
		}
	}
}
