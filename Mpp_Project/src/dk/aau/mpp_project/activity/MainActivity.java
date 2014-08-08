package dk.aau.mpp_project.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
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
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.fragment.ExpensesFragment;
import dk.aau.mpp_project.fragment.HomeFragment;
import dk.aau.mpp_project.fragment.LoansFragment;
import dk.aau.mpp_project.fragment.SettingsFragment;
import dk.aau.mpp_project.model.MyUser;

import java.util.ArrayList;

/**
 * URL Example
 * https://github.com/ParsePlatform/IntegratingFacebookTutorial/blob/master/
 * IntegratingFacebookTutorial
 * -Android/src/com/parse/integratingfacebooktutorial/UserDetailsActivity.java
 * 
 */

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String		TAG	= null;
	ViewPager						mViewPager;
	private AppSectionsPagerAdapter	mAppSectionsPagerAdapter;
	private ArrayList<Fragment>		listFragments;
	private MyUser					myUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {

		} else {
			startLoginActivity();
		}

		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// creating Fragments
		listFragments = new ArrayList<Fragment>();
		listFragments.add(new HomeFragment());
		listFragments.add(new ExpensesFragment());
		listFragments.add(new LoansFragment());
		listFragments.add(new SettingsFragment());

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
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
					}
				});

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
			// actionBar.addTab(actionBar.newTab().setText(mAppSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_new_flat:
			Intent intent = new Intent(this, NewFlatActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			myUser = (MyUser) currentUser;

			String facebookId = MyApplication
					.getOption(MyUser.FACEBOOK_ID, "0");
			String name = MyApplication.getOption(MyUser.NAME, "0");
			String birthday = MyApplication.getOption(MyUser.BIRTHDAY, "0");

			myUser.setBirthday(birthday);
			myUser.setFacebookId(facebookId);
			myUser.setName(name);

			Log.v(TAG, "# First Name :" + myUser.getName());
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

	public MyUser getMyUser() {
		return myUser;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
