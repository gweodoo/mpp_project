package dk.aau.mpp_project.activity;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import dk.aau.mpp_project.R;

public class LogInActivity extends Activity {

	protected static final String	TAG	= "LoginActivity";

	private Button					loginButton;
	private Dialog					progressDialog;

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
			goToMainActivity();
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		this.progressDialog = ProgressDialog.show(this, "",
				"Logging in bitches...", true);

		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location");

		ParseFacebookUtils.logIn(this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LogInActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(TAG, "User signed up and logged in through Facebook!");
					goToMainActivity();
				} else {
					Log.d(TAG, "User logged in through Facebook!");
					goToMainActivity();
				}
			}
		});
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
