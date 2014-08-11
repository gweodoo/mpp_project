package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.parse.ParseUser;
import de.greenrobot.event.EventBus;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.LogInActivity;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements FragmentEventHandler{

    private ProgressDialog progressDialog;
	private TextView	textViewLogout;
	private TextView	textViewChangeDetails;
	private TextView	textViewLeaveFlat;

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
				onLeaveButtonClicked();
			}
		});

		return rootView;
	}

	protected void onLeaveButtonClicked() {
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
            if (DatabaseHelper.ACTION_GET_USER_FLATS.equals(e.getAction())) {
                // You know what you need (List or simple object)
                // If List: ArrayList<Flat> flatsList =
                // e.getExtras().getParcelableArrayList("data");
                // If Object only : Flat flat =
                // e.getExtras().getParcelable("data");

                ArrayList<Flat> flatsList = e.getExtras()
                        .getParcelableArrayList("data");

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
    }
}
