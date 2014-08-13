package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.adapter.SpinnerAdapter;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.Operation;
import dk.aau.mpp_project.model.SpinnerModel;

import java.util.ArrayList;

public class ExpensesFragment extends ListFragment implements
		FragmentEventHandler, OnMultiChoiceClickListener {

	private Flat					flat;

	private ArrayList<Operation>	operationsList			= new ArrayList<Operation>();
	private OperationAdapter		operationAdapter;

	private ArrayList<String>		shareMate				= new ArrayList<String>();

	private ProgressDialog			progressDialog;
	private EditText				commentText, amountText;

	private boolean[]				selected;

	// spinner photo
	private ArrayList<SpinnerModel>	customListViewValuesArr	= new ArrayList<SpinnerModel>();
	private Spinner					spinnerPeople;
	private SpinnerAdapter			spinnerAdapter;

	private MainActivity			activity				= null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View expensesView = inflater.inflate(R.layout.fragment_expenses,
				container, false);

		spinnerPeople = (Spinner) expensesView.findViewById(R.id.peopleSpinner);
		commentText = (EditText) expensesView.findViewById(R.id.commentText);
		amountText = (EditText) expensesView.findViewById(R.id.amountText);
		ImageButton bt = (ImageButton) expensesView.findViewById(R.id.okButton);

		flat = ((MainActivity) getActivity()).getMyFlat();

		operationAdapter = new OperationAdapter(getActivity(),
				R.layout.layout_expensesitem, operationsList);

		// Spinner PHOTO
		activity = (MainActivity) getActivity();

		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);

		DatabaseHelper.getOperationsByFlat(flat);

		Resources res = getResources();
		// Create custom spinnerAdapter object ( see below
		// CustooperationAdapter.java )
		spinnerAdapter = new SpinnerAdapter(activity, R.layout.spinner_rows,
				customListViewValuesArr, res);

		// Set spinnerAdapter to spinner
		spinnerPeople.setAdapter(spinnerAdapter);

		// Listener called when spinner item selected
		spinnerPeople.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long id) {
				// your code here

				// Get selected row data to show on screen
				String SpinnerUser = ((TextView) v
						.findViewById(R.id.spinnerUser)).getText().toString();
				String sub = ((TextView) v.findViewById(R.id.sub)).getText()
						.toString();

				shareMate.clear();
				Toast.makeText(getActivity(), "the User : " + SpinnerUser,
						Toast.LENGTH_SHORT).show();
				shareMate.add(SpinnerUser);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
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

				MyUser lender = ((MainActivity) getActivity()).getMyUser();

				// shareMate.add("0");

				// We create an operation object
				Operation temp = null;

				for (MyUser to : flat.getFlatUsers()) {
					if (shareMate.get(0).equals("ALL")) {
						temp = new Operation(flat, lender, to,
								Double.valueOf(amountText.getText().toString()
										.trim()), "", commentText.getText()
										.toString().trim(), false);
						
						DatabaseHelper.createOperation(flat, temp);
					} else {
						if (to.getName().equals(shareMate.get(0))) {
							temp = new Operation(flat, lender, to, Double
									.valueOf(amountText.getText().toString().trim()),
									"", commentText.getText().toString().trim(), false);
							
							DatabaseHelper.createOperation(flat, temp);
						}
					}
				}
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
			}
		});

		return expensesView;
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
			if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS
					.equals(e.getAction())) {
				// We have got all operations
				ArrayList<Operation> arri = e.getExtras()
						.getParcelableArrayList("data");

				operationsList.clear();
				operationsList.addAll(arri);
				operationAdapter.notifyDataSetChanged();

				Log.i("ExpensesFragment", "Operation :" + operationsList.size());
				Toast.makeText(getActivity(),
						"Operation :" + operationsList.size(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void setListData(ArrayList<MyUser> list) {

		// Now i have taken static values by loop.
		// For further inhancement we can take data by webservice / json / xml;

		for (MyUser item : list) {

			final SpinnerModel sched = new SpinnerModel();

			// Firstly take data in model object
			if (!item.getName().equals(null))
				sched.setUser(item.getName());
			sched.setImage("image_user");

			// Take Model Object in ArrayList
			customListViewValuesArr.add(sched);

		}
		Toast.makeText(getActivity(),
				"nombre elements: " + customListViewValuesArr.size(),
				Toast.LENGTH_SHORT).show();
		Log.i("ExpensesFragment", "nombre elements dans la list de Users: "
				+ customListViewValuesArr.size());
		spinnerAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
			selected[which] = true;
		else
			selected[which] = false;
	}
}
