package dk.aau.mpp_project.activity;

import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {
	private static final String	TAG	= "ChangePasswordActivity";

	protected EditText			oldPassword;
	protected EditText			newPassword;
	protected EditText			repeatPassword;

	protected ProgressDialog	progressDialog;
	protected String			errorMessage;

	private String				flatId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		oldPassword = (EditText) findViewById(R.id.old_flat_password);
		newPassword = (EditText) findViewById(R.id.new_flat_password);
		repeatPassword = (EditText) findViewById(R.id.new_flat_password_repeat);

		flatId = getIntent().getStringExtra("objectId");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_flat_ok, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_new_flat_ok:

			if (validateData()) {

				// save flat data
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {

					if (!EventBus.getDefault().isRegistered(this))
						EventBus.getDefault().register(this);

					DatabaseHelper.getFlatById(flatId);
				}

			} else {

			}
			break;
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onEventMainThread(StartEvent e) {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Saving flat...");
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

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_UPDATE_FLAT.equals(e.getAction())) {

				EventBus.getDefault().unregister(this);

				setResult(RESULT_OK);
				finish();
			} else if (DatabaseHelper.ACTION_GET_FLAT_BY_ID.equals(e
					.getAction())) {
				Flat myFlat = e.getExtras().getParcelable("data");

				String cryptedPsw = DatabaseHelper.SHA1(oldPassword.getText()
						.toString().trim());

				if (myFlat.getString(Flat.PASSWORD).equals(cryptedPsw)) {

					DatabaseHelper.updateFlat(myFlat, newPassword.getText()
							.toString().trim());
				} else {
					Toast.makeText(this, "The current password is incorrect.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		// Error occured
		else {

		}
	}

	private boolean validateData() {
		Boolean valid = true;
		if (oldPassword.getText().toString().trim().equals("")) {
			oldPassword.setError("Please enter the current password\n");
			valid = false;
		}
		if (newPassword.getText().toString().trim().equals("")) {
			newPassword.setError("Please enter a new password.\n");
			valid = false;
		}
		if (repeatPassword.getText().toString().trim().equals("")) {
			repeatPassword.setError("Please repeat the password\n");
			valid = false;
		}

		if (!newPassword.getText().toString()
				.equals(repeatPassword.getText().toString())) {
			repeatPassword.setError("Please enter same password again.\n");
			valid = false;
		}

		return valid;
	}
}
