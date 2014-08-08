package dk.aau.mpp_project.fragment;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.LogInActivity;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.model.Flat;

public class SettingsFragment extends Fragment {

	private Button	logoutButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section_settings,
				container, false);

		logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		((Button) rootView.findViewById(R.id.buttonCreateFlat))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (EventBus.getDefault().isRegistered(getActivity())) {

							if (((MainActivity) getActivity()).getMyUser() != null) {
								Flat flat = new Flat();

								flat.setName("Aalborg");
								flat.setAddress("Danemark");
								flat.setAdminId(((MainActivity) getActivity())
										.getMyUser().getObjectId());
								flat.setRentAmount(800);

								DatabaseHelper.createFlat(
										((MainActivity) getActivity())
												.getMyUser(), flat, "password");
							}
						}
					}
				});

//		// Fetch Facebook user info if the session is active
//		Session session = ParseFacebookUtils.getSession();
//		if (session != null && session.isOpened()) {
//			
//		} else {
//			startLoginActivity();
//		}
		return rootView;
	}

	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//
//		ParseUser currentUser = ParseUser.getCurrentUser();
//		if (currentUser != null) {
//
//		} else {
//			// If the user is not logged in, go to the
//			// activity showing the login view.
//			startLoginActivity();
//		}
//	}

	private void startLoginActivity() {
		Intent intent = new Intent(getActivity(), LogInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().finish();
	}
}