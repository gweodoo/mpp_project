package dk.aau.mpp_project.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.parse.ParseFile;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.R.layout;
import dk.aau.mpp_project.R.menu;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.filter.Filter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint.Join;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class FlatLoginActivity extends Activity {

	protected EditText flatId;
	protected EditText flatPassword;
	private Flat flat;
	private ProgressDialog	progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flat_login);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		flatId = (EditText) findViewById(R.id.joinFlatId);
		flatPassword = (EditText) findViewById(R.id.joinFlatPassword);
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
			if(validate()){
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {
					System.out.println("GET FLAT");
					DatabaseHelper.getFlatById(flatId.getText().toString());
				}
			}
			return true;
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean validate(){
		System.out.println("VALIDATE");
		if(flatId.getText().toString().equals(""))
			{flatId.setError("Please enter a flat name!"); return false;}
		if(flatPassword.getText().toString().equals(""))
			{flatPassword.setError("Please enter a password!"); return false;}
		return true;
	}
	
	

	public void onEventMainThread(StartEvent e) {

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

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		// Success retreiving database
		if (e.isSuccess()) {

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_GET_FLAT_BY_ID.equals(e.getAction())) {
                flat = e.getExtras().getParcelable("data");
                ParseUser currentUser = ParseUser.getCurrentUser();
                System.out.println("JOIN FLAT");
                DatabaseHelper.joinFlat((MyUser)currentUser, flat, flatPassword.getText().toString());
			}
			if (DatabaseHelper.ACTION_JOIN_FLAT.equals(e.getAction())) {
				Toast t = Toast.makeText(getApplicationContext(), "logged in flat", 2000);
				t.show();
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			}
			else{
				Intent data = new Intent(getApplicationContext(), MainActivity.class);
				data.putExtra("data", flat.getObjectId().toString());
				setResult(RESULT_OK, data);
				startActivity(data);
				finish();
			}
		}
		// Error occured
		else {
			Toast t = Toast.makeText(getApplicationContext(), "Something went wrong. Please check ID and password!", 2000);
			t.show();
		}

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

}
