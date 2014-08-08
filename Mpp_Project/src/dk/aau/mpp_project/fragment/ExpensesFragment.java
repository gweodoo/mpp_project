package dk.aau.mpp_project.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.Operation;

import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ExpensesFragment extends ListFragment {
	
	private ArrayList<Operation> myArray= new ArrayList<Operation>();
	private OperationAdapter mAdapter;
	private Spinner idPeople;
	
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
			
			Log.d("expenses",temp.getComment()+"<-5->");
			Log.d("expenses",temp.getDate()+"<-6->");
			Log.d("expenses",temp.getLender()+"<-7->");
			
			myArray.add(temp);
			
		}
        
		mAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		return expensesView;
	}
	
	
	

}
