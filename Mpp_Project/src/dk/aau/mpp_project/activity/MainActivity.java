package dk.aau.mpp_project.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.fragment.ExpensesFragment;
import dk.aau.mpp_project.fragment.FragmentEventHandler;
import dk.aau.mpp_project.fragment.HomeFragment;
import dk.aau.mpp_project.fragment.LoansFragment;
import dk.aau.mpp_project.fragment.SettingsFragment;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;

/**
 * URL Example
 * https://github.com/ParsePlatform/IntegratingFacebookTutorial/blob/master/
 * IntegratingFacebookTutorial
 * -Android/src/com/parse/integratingfacebooktutorial/UserDetailsActivity.java
 * 
 */

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String		TAG								= "MainActivity";
	private static final int		REQUEST_CODE_NEW_FLAT_ACTIVITY	= 1;

	private ViewPager				mViewPager;
	private AppSectionsPagerAdapter	mAppSectionsPagerAdapter;
	private ArrayList<Fragment>		listFragments;
	private MyUser					myUser;
	private Flat					myFlat;
	private int						curFragment						= -1;
	private ProgressDialog			progressDialog;

	private ActionBar				actionBar;

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				myUser = (MyUser) currentUser;

				String facebookId = MyApplication.getOption(MyUser.FACEBOOK_ID,
						"0");
				String name = MyApplication.getOption(MyUser.NAME, "0");
				String birthday = MyApplication.getOption(MyUser.BIRTHDAY, "0");

				myUser.setBirthday(birthday);
				myUser.setFacebookId(facebookId);
				myUser.setName(name);

				Log.v(TAG, "# First Name :" + myUser.getName());

				if (MyApplication.getSharedPref().contains(
						MyApplication.CURRENT_FLAT)) {
					String flatId = MyApplication.getOption(
							MyApplication.CURRENT_FLAT, "-1");

					EventBus.getDefault().register(this);

					DatabaseHelper.getFlatById(flatId);
				}

			} else {
				// If the user is not logged in, go to the
				// activity showing the login view.
				startLoginActivity();
			}
		} else {
			startLoginActivity();
		}

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);

		// Set up the action bar.
		actionBar = getActionBar();
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private void initTabsPlease() {

		// creating Fragments
		listFragments = new ArrayList<Fragment>();
		listFragments.add(new HomeFragment());
		listFragments.add(new ExpensesFragment());
		listFragments.add(new LoansFragment());
		listFragments.add(new SettingsFragment());

		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(mAppSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between different app sections, select
						// the corresponding tab.
						// We can also use ActionBar.Tab#select() to do this if
						// we have a reference to the
						// Tab.
						actionBar.setSelectedNavigationItem(position);
						if (curFragment > -1) {
							((FragmentEventHandler) listFragments
									.get(curFragment)).onPauseEvent();
						}
						curFragment = position;
						((FragmentEventHandler) listFragments.get(curFragment))
								.onResumeEvent();
					}
				});

		actionBar.removeAllTabs();

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setIcon(mAppSectionsPagerAdapter.getPageIcon(i))
					.setTabListener(this));
		}
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
			if (DatabaseHelper.ACTION_GET_FLAT_BY_ID.equals(e.getAction())) {
				EventBus.getDefault().unregister(this);

				myFlat = e.getExtras().getParcelable("data");

				initTabsPlease();
			}
		}
		// Error occured
		else {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public Flat getMyFlat() {
		return myFlat;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_new_flat:
			Intent intent = new Intent(this, NewFlatActivity.class);
			intent.putExtra("should_check", false);
			startActivityForResult(intent, REQUEST_CODE_NEW_FLAT_ACTIVITY);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LogInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	public MyUser getMyUser() {
		return myUser;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_NEW_FLAT_ACTIVITY) {

				String flatId = data.getStringExtra("data");

				EventBus.getDefault().register(this);

				DatabaseHelper.getFlatById(flatId);
			}
		}

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			return listFragments.get(i);
		}

		@Override
		public int getCount() {
			return listFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Ho";
			case 1:
				return "Ex";
			case 2:
				return "Lo";
			case 3:
				return "Se";
			default:
				return "Section" + (position + 1);
			}
		}

		public int getPageIcon(int position) {
			switch (position) {
			case 0:
				return R.drawable.ic_action_home;
			case 1:
				return R.drawable.ic_action_expsenses;
			case 2:
				return R.drawable.ic_action_plan;
			case 3:
				return R.drawable.ic_action_settings;
			default:
				return R.drawable.ic_action_home;
			}
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
	}
}
