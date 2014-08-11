package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import de.greenrobot.event.EventBus;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.LogInActivity;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.activity.NewFlatActivity;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;

public class SettingsFragment extends Fragment implements FragmentEventHandler {

	private ProgressDialog	progressDialog;

	private TextView		textViewLogout;
	private TextView		textViewChangeDetails;
	private TextView		textViewLeaveFlat;

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

		textViewLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		textViewChangeDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		textViewLeaveFlat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLeaveFlatButtonClicked();
			}
		});

		return rootView;
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
		if (progressDialog == null) {
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
