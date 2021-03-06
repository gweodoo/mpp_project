package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseUser;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.*;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;

public class SettingsFragment extends Fragment implements FragmentEventHandler {

	private ProgressDialog	progressDialog;
	private TextView		textViewLogout;
	private TextView		textViewChangeDetails;
	private TextView		textViewChangePassword;
	private TextView		textViewLeaveFlat;
	private TextView		textViewFlatId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section_settings,
				container, false);

		textViewLogout = (TextView) rootView.findViewById(R.id.textViewLogout);
		textViewChangeDetails = (TextView) rootView
				.findViewById(R.id.textViewChangeDetails);
		textViewLeaveFlat = (TextView) rootView
				.findViewById(R.id.textViewLeaveFlat);
		textViewFlatId = (TextView) rootView.findViewById(R.id.textViewFlatId);
		textViewChangePassword = (TextView) rootView
				.findViewById(R.id.textViewChangePassword);

		textViewFlatId.setText("Flat ID : "
				+ ((MainActivity) getActivity()).getMyFlat().getObjectId());

		textViewLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		textViewChangeDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onChangeDetailsFlatClicked();
			}
		});

		textViewLeaveFlat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLeaveFlatButtonClicked();
			}
		});

		textViewChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onChangePasswordClicked();
			}
		});

		return rootView;
	}

	protected void onChangePasswordClicked() {
		Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);

		DatabaseHelper.createNews(((MainActivity)getActivity()).getMyFlat(), ((MainActivity)getActivity()).getMyUser(), "password Update");
		Flat flat = ((MainActivity) getActivity()).getMyFlat();
		intent.putExtra("objectId", flat.getObjectId());
		getActivity().startActivityForResult(intent,
				MainActivity.REQUEST_CODE_CHANGE_PASSWORD_ACTIIVTY);
	}

	protected void onChangeDetailsFlatClicked() {
		Intent intent = new Intent(getActivity(), ChangeDetailsActivity.class);
		DatabaseHelper.createNews(((MainActivity)getActivity()).getMyFlat(), ((MainActivity)getActivity()).getMyUser(), "Details Update");
		Flat flat = ((MainActivity) getActivity()).getMyFlat();
		intent.putExtra("objectId", flat.getObjectId());
		intent.putExtra("name", flat.getName());
		intent.putExtra("address", flat.getAddress());
		intent.putExtra("rent", flat.getRentAmount());
		getActivity().startActivityForResult(intent,
				MainActivity.REQUEST_CODE_CHANGE_DETAILS_ACTIIVTY);
	}

	protected void onLeaveFlatButtonClicked() {
		DatabaseHelper.leaveFlat(((MainActivity) getActivity()).getMyUser(),
				((MainActivity) getActivity()).getMyFlat());
	}

	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	//
	// ParseUser currentUser = ParseUser.getCurrentUser();
	// if (currentUser != null) {
	//
	// } else {
	// // If the user is not logged in, go to the
	// // activity showing the login view.
	// startLoginActivity();
	// }
	// }

	private void startLoginActivity() {
		Intent intent = new Intent(getActivity(), LogInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onResumeEvent() {

		EventBus.getDefault().register(this);
	}

	@Override
	public void onPauseEvent() {

		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(StartEvent e) {
		if (progressDialog == null && getActivity() != null) {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
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
			if (DatabaseHelper.ACTION_LEAVE_FLAT.equals(e.getAction())) {
				// You know what you need (List or simple object)
				// If List: ArrayList<Flat> flatsList =
				// e.getExtras().getParcelableArrayList("data");
				// If Object only : Flat flat =
				// e.getExtras().getParcelable("data");
				DatabaseHelper.createNews(((MainActivity)getActivity()).getMyFlat(), ((MainActivity)getActivity()).getMyUser(), "Flat leaving");
				MyApplication.setOption(MyApplication.CURRENT_FLAT, "-1");

				Intent intent = new Intent(getActivity(), NewFlatActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		} else {
			if (DatabaseHelper.ACTION_LEAVE_FLAT.equals(e.getAction())) {
				Toast.makeText(
						getActivity(),
						"An error occured while trying to leave the flat. Please try again.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
