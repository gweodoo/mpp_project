package dk.aau.mpp_project.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.filter.Filter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;

import java.util.ArrayList;
import java.util.List;

public class NewFlatActivity extends Activity {

	private static final String	TAG	= "NewFlatActivity";

	private ArrayList<Flat>		flatsList;
	private FlatAdapter			adapter;
	private ProgressDialog		progressDialog;
	private final int ADD_FLAT_REQUEST = 4567;
	private final int JOIN_FLAT_REQUEST = 1234;

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
				startActivityForResult(intent, ADD_FLAT_REQUEST);
			}
		});

		joinFlatButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						FlatLoginActivity.class);
				startActivityForResult(intent, JOIN_FLAT_REQUEST);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK && requestCode == ADD_FLAT_REQUEST){
			checkForCurrentFlat();
		}
		if(resultCode==RESULT_OK && requestCode == JOIN_FLAT_REQUEST){
			progressDialog.setMessage("Joining flat...");
			checkForCurrentFlat();
		}
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
					Intent data = new Intent();
					data.putExtra("data", flatId);
					setResult(RESULT_OK, data);
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

	public void onEventMainThread(FinishedEvent e) {
		Log.d(TAG, "# FinishedEvent");

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		// Success retreiving database
		if (e.isSuccess()) {

			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_GET_USER_FLATS.equals(e.getAction())) {
                ArrayList<Flat> tmp = e.getExtras().getParcelableArrayList("data");
				flatsList.clear();
				flatsList.addAll(tmp);

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
			if(flat.getPhoto()!=null){
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Config.RGB_565;
					options.inSampleSize = 2;
					
					Bitmap b = BitmapFactory.decodeByteArray(flat.getPhoto().getData() , 0, flat.getPhoto().getData().length, options);
					
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					b = Filter.rescale(b, width, false);
					viewHolder.flatImage.setImageBitmap(Filter.fastblur(b, 20));
	//				viewHolder.flatImage.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(b, 1000, 100, true)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				viewHolder.flatImage.setImageDrawable(getResources().getDrawable(R.drawable.flat1small));
			}
			viewHolder.flatTitle.setText(flat.getName());

			return convertView;
		}

	}
}
