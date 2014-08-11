package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.Operation;

import java.util.ArrayList;

public class ExpensesFragment extends ListFragment implements FragmentEventHandler{
	
	private ArrayList<Operation> myArray= new ArrayList<Operation>();
	private OperationAdapter mAdapter;
	private Spinner idPeople;
    private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View expensesView = inflater.inflate(R.layout.fragment_expenses, container, false);
		
		idPeople = (Spinner) expensesView.findViewById(R.id.peopleSpinner);
		
        ArrayList<String> array = new ArrayList<String>();
        array.add("hi");
        array.add("ho");
        array.add("ha");
        
        ArrayAdapter<String> spinnerContent = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array);
        spinnerContent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idPeople.setAdapter(spinnerContent);
		
		mAdapter=new OperationAdapter(getActivity(),R.layout.layout_expensesitem,myArray);
		setListAdapter(mAdapter);
		

        Operation temp;
		for(int i=0;i<20;i++)
		{
			temp=new Operation(new Flat(), "test", "test", 10.2,"hello", "hello", false);
			

			myArray.add(temp);
			
		}
        
		mAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		return expensesView;
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
