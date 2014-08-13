package dk.aau.mpp_project.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.adapter.SpinnerAdapter;
import dk.aau.mpp_project.application.MyApplication;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.Operation;

public class ExpensesFragment extends ListFragment implements
		FragmentEventHandler, SwipeRefreshLayout.OnRefreshListener {

	private Flat					flat;

	private ArrayList<Operation>	operationsList	= new ArrayList<Operation>();
	private OperationAdapter		operationAdapter;

	private ProgressDialog			progressDialog;
	private EditText				commentText, amountText;

	private SwipeRefreshLayout		swipeRefresh;
	private boolean					showProgress	= true;

	// spinner photo
	private Spinner					spinnerPeople;
	private SpinnerAdapter			spinnerAdapter;

	private MyUser					selectedUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View expensesView = inflater.inflate(R.layout.fragment_expenses,
				container, false);

		swipeRefresh = (SwipeRefreshLayout) expensesView
				.findViewById(R.id.swipe_container);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		commentText = (EditText) expensesView.findViewById(R.id.commentText);
		amountText = (EditText) expensesView.findViewById(R.id.amountText);
		ImageButton bt = (ImageButton) expensesView.findViewById(R.id.okButton);
		spinnerPeople = (Spinner) expensesView.findViewById(R.id.peopleSpinner);

		flat = ((MainActivity) getActivity()).getMyFlat();

		operationAdapter = new OperationAdapter(getActivity(),
				R.layout.layout_expensesitem, operationsList);

		// Spinner PHOTO
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);

		Resources res = getResources();
		// Create custom spinnerAdapter object ( see below
		// CustooperationAdapter.java )
		spinnerAdapter = new SpinnerAdapter(getActivity(),
				R.layout.spinner_rows, flat.getFlatUsers(), res);

		// Set spinnerAdapter to spinner
		spinnerPeople.setAdapter(spinnerAdapter);

		// Listener called when spinner item selected
		spinnerPeople.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {

				selectedUser = spinnerAdapter.getItem(position);

				Log.v("Tag", "# Selected : " + selectedUser.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		// FIN Spinner PHOTO

		operationAdapter = new OperationAdapter(getActivity(),
				R.layout.layout_expensesitem, operationsList);
		setListAdapter(operationAdapter);

		// We add a listener on the add button
		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (amountText.getText().toString().trim().length() > 0
						&& commentText.getText().toString().trim().length() > 0) {
					MyUser lender = ((MainActivity) getActivity()).getMyUser();

					// We create an operation object

					// for (MyUser to : flat.getFlatUsers()) {
					// if (shareMate.get(0).equals("ALL")) {
					// temp = new Operation(flat, lender, to,
					// Double.valueOf(amountText.getText().toString()
					// .trim()), "", commentText.getText()
					// .toString().trim(), false);
					//
					// DatabaseHelper.createOperation(flat, temp);
					// } else {
					// if (to.getName().equals(shareMate.get(0))) {
					// temp = new Operation(flat, lender, to, Double
					// .valueOf(amountText.getText().toString()
					// .trim()), "", commentText.getText()
					// .toString().trim(), false);
					//
					// DatabaseHelper.createOperation(flat, temp);
					// }
					// }
					Operation temp = new Operation(flat, lender, selectedUser,
							Double.valueOf(amountText.getText().toString()
									.trim()), "", commentText.getText()
									.toString().trim(), false);

					DatabaseHelper.createOperation(temp);
					// }
					// We fill it
					Log.i("ExpensesFragment",
							"FlatId = "
									+ flat
									+ " - User = "
									+ lender
									+ " - Amount = "
									+ Double.valueOf(amountText.getText()
											.toString()));
					commentText.setText("");
					amountText.setText("");
					
					try {
						DatabaseHelper.parseSendPush(MyApplication.CHANNEL, selectedUser.getFacebookId(), "You owe "+lender.getName()+" "+Double.valueOf(amountText.getText().toString()
										.trim()));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					Toast.makeText(getActivity(), "Fields missing",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return expensesView;
	}

	@Override
	public void onRefresh() {

		DatabaseHelper.getOperationsByFlat(flat);
	}

	@Override
	public void onResumeEvent() {
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	@Override
	public void onPauseEvent() {

		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(StartEvent e) {
		Log.i("ExpensesFragment", "onEventMainThread Start");

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
		}

		if (progressDialog != null && !progressDialog.isShowing()
				&& showProgress)
			progressDialog.show();
	}

	public void onEventMainThread(FinishedEvent e) {

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		// Success retreiving database
		if (e.isSuccess()) {
			// Check for what you wanted to retrieve
			if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS
					.equals(e.getAction())) {
				showProgress = false;
				swipeRefresh.setRefreshing(false);
				
				// We have got all operations
				ArrayList<Operation> arri = e.getExtras()
						.getParcelableArrayList("data");
				
				Collections.reverse(arri);

				operationsList.clear();
				operationsList.addAll(arri);
				operationAdapter.notifyDataSetChanged();

				Log.i("ExpensesFragment", "Operation :" + operationsList.size());
			}
		}
	}
	//
	// public class UserAdapter extends ArrayAdapter<MyUser> {
	//
	// public class ViewHolder {
	// public ImageView image;
	// public TextView name;
	// }
	//
	// private int resource;
	// private LayoutInflater inflater;
	//
	// public UserAdapter(Context context, int resource, List<MyUser> objects) {
	// super(context, resource, objects);
	//
	// this.resource = resource;
	// inflater = LayoutInflater.from(context);
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder viewHolder = null;
	//
	// if (convertView == null) {
	// viewHolder = new ViewHolder();
	//
	// convertView = inflater.inflate(resource, parent, false);
	//
	// viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
	// viewHolder.name = (TextView) convertView.findViewById(R.id.spinnerUser);
	//
	// convertView.setTag(viewHolder);
	// } else {
	// viewHolder = (ViewHolder) convertView.getTag();
	// }
	//
	// MyUser user = getItem(position);
	//
	// viewHolder.name
	//
	// return convertView;
	// }
	// }
}
