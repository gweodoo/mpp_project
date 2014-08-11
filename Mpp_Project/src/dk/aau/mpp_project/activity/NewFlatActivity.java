package dk.aau.mpp_project.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.parse.ParseUser;

import dk.aau.mpp_project.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.activity.AddNewFlatActivity;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;

public class NewFlatActivity extends Activity {

	private static final String	TAG	= "NewFlatActivity";

	private ArrayList<Flat>		flatsList;
	private FlatAdapter			adapter;
	private ProgressDialog		progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_flat);

		// TODO
		if (getIntent().getBooleanExtra("should_check", true))
			checkForCurrentFlat();

		flatsList = new ArrayList<Flat>();
		adapter = new FlatAdapter(getApplicationContext(),
				R.layout.layout_flat_item, flatsList);

		ListView listView = (ListView) findViewById(R.id.list);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long arg3) {
				Flat flat = adapter.getItem(position);

				MyApplication.setOption(MyApplication.CURRENT_FLAT,
						flat.getObjectId());

				checkForCurrentFlat();
			}
		});

		Button newFlatButton = (Button) findViewById(R.id.newFlatButton);
		Button joinFlatButton = (Button) findViewById(R.id.joinFlatButton);

		newFlatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						AddNewFlatActivity.class);
				startActivity(intent);
			}
		});

		joinFlatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void checkForCurrentFlat() {
		if (MyApplication.getSharedPref().contains(MyApplication.CURRENT_FLAT)) {
			String flatId = MyApplication.getOption(MyApplication.CURRENT_FLAT,
					"-1");

			if (flatId != "-1") {
				if (getIntent().getBooleanExtra("should_check", true)) {
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.new_flat, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		EventBus.getDefault().register(this);

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			MyUser myUser = (MyUser) currentUser;

			String facebookId = MyApplication
					.getOption(MyUser.FACEBOOK_ID, "0");
			String name = MyApplication.getOption(MyUser.NAME, "0");
			String birthday = MyApplication.getOption(MyUser.BIRTHDAY, "0");

			myUser.setBirthday(birthday);
			myUser.setFacebookId(facebookId);
			myUser.setName(name);

			DatabaseHelper.getUserFlats(myUser);

		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LogInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
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

	@SuppressWarnings("unchecked")
	public void onEventMainThread(FinishedEvent e) {
		Log.d(TAG, "# FinishedEvent");

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		// Success retreiving database
		if (e.isSuccess()) {

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_GET_USER_FLATS.equals(e.getAction())) {
				flatsList.clear();
				flatsList.addAll((Collection<? extends Flat>) e.getExtras()
						.getParcelableArrayList("data"));

				adapter.notifyDataSetChanged();
			}
		}
		// Error occured
		else {

		}
	}

	public class FlatAdapter extends ArrayAdapter<Flat> {

		public class ViewHolder {
			public ImageView	flatImage;
			public TextView		flatTitle;
		}

		private Context	context;
		private int		resource;

		public FlatAdapter(Context context, int resource, List<Flat> objects) {
			super(context, resource, objects);

			this.context = context;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(resource, parent, false);

				viewHolder = new ViewHolder();

				viewHolder.flatImage = (ImageView) convertView
						.findViewById(R.id.flatImage);
				viewHolder.flatTitle = (TextView) convertView
						.findViewById(R.id.flatTitle);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Flat flat = this.getItem(position);

			viewHolder.flatTitle.setText(flat.getName());

			return convertView;
		}

	}
}
