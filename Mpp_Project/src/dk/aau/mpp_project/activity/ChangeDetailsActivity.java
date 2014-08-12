package dk.aau.mpp_project.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;

public class ChangeDetailsActivity extends Activity {

	private static final String	TAG	= "ChangeDetailsActivity";

	protected EditText			flatName;
	protected EditText			address;
	protected EditText			rent;
	protected ImageView			flatImage;
	protected ProgressDialog	progressDialog;
	protected String			errorMessage;

	private String				flatId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_details);

		Log.v(TAG, "# FUCKING ID ARE YOU HERE ? " + flatId);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		flatName = (EditText) findViewById(R.id.new_flat_name);
		address = (EditText) findViewById(R.id.new_flat_address);
		rent = (EditText) findViewById(R.id.rent);
		flatImage = (ImageView) findViewById(R.id.new_flat_image);

		flatId = getIntent().getStringExtra("objectId");
		flatName.setText(getIntent().getStringExtra("name"));
		address.setText(getIntent().getStringExtra("address"));
		rent.setText(Double.toString(getIntent().getDoubleExtra("rent", 0)));

		ImageButton addFromGallery = (ImageButton) findViewById(R.id.add_pic_gallery);
		ImageButton addFromCamera = (ImageButton) findViewById(R.id.add_pic_camera);

		// addFromGallery.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(
		// Intent.createChooser(intent, "Select Picture"),
		// GALLERY_REQUEST);
		// }
		// });
		//
		// addFromCamera.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent cameraIntent = new Intent(
		// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// startActivityForResult(cameraIntent, CAMERA_REQUEST);
		// }
		// });
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

			// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			if (validateData()) {

				// save flat data
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {

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

		// Success retreiving database
		if (e.isSuccess()) {

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_UPDATE_FLAT.equals(e.getAction())) {

				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				EventBus.getDefault().unregister(this);

				setResult(RESULT_OK);
				finish();
			} else if (DatabaseHelper.ACTION_GET_FLAT_BY_ID.equals(e
					.getAction())) {
				Flat myFlat = e.getExtras().getParcelable("data");

				myFlat.setName(flatName.getText().toString().trim());
				myFlat.setAddress(address.getText().toString().trim());
				myFlat.setRentAmount(Double.parseDouble(rent.getText()
						.toString().trim()));

				DatabaseHelper.updateFlat(myFlat, null);
			}
		}
		// Error occured
		else {

		}
	}

	private boolean validateData() {
		Boolean valid = true;
		if (flatName.getText().toString().trim().equals("")) {
			flatName.setError("Please enter a flat name.\n");
			valid = false;
		}
		if (address.getText().toString().trim().equals("")) {
			address.setError("Please enter an address.\n");
			valid = false;
		}
		if (rent.getText().toString().trim().equals("")) {
			rent.setError("Please enter the rent.\n");
			valid = false;
		}
		return valid;
	}
}
