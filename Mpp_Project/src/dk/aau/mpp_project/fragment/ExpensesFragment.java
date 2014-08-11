package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.Operation;

import java.util.ArrayList;

public class ExpensesFragment extends ListFragment implements FragmentEventHandler{
	
	private ArrayList<Operation> myArray= new ArrayList<Operation>();
	private OperationAdapter mAdapter;
	private Spinner idPeople;
    private ProgressDialog progressDialog;
    private EditText commentText,amountText;
    private String shareMate;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View expensesView = inflater.inflate(R.layout.fragment_expenses, container, false);
		
		idPeople = (Spinner) expensesView.findViewById(R.id.peopleSpinner);
		commentText=(EditText)expensesView.findViewById(R.id.commentText);
		amountText=(EditText)expensesView.findViewById(R.id.amountText);
		
		
		//We fill the spinner with some infos useless...
        ArrayList<String> array = new ArrayList<String>();
        array.add("hi");
        array.add("ho");
        array.add("ha");
        
        ArrayAdapter<String> spinnerContent = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array);
        spinnerContent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idPeople.setAdapter(spinnerContent);
		
		mAdapter=new OperationAdapter(getActivity(),R.layout.layout_expensesitem,myArray);
		setListAdapter(mAdapter);
		
		//We fill the array with 20 entries useless
        Operation temp;
		for(int i=0;i<20;i++)
		{
			temp=new Operation(new Flat(), "test", "test", 10.2,"hello", "hello", false);
			
			myArray.add(temp);
			
		}
        
		mAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		
		//We add a listener on the add button
		ImageButton bt=(ImageButton)expensesView.findViewById(R.id.okButton);
		bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            	
            	MyUser user=((MainActivity)getActivity()).getMyUser();
            	Flat flatId=((MainActivity)getActivity()).getMyFlat();
            	
            	//We create an operation object
            	Operation temp;
            	//We fill it
            	temp=new Operation(flatId,user.getName(),shareMate,Double.valueOf(amountText.getText().toString()),"",commentText.getText().toString(),false);
            
            }
		});
		
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
