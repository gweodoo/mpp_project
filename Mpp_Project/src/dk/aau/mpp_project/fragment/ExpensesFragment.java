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

public class ExpensesFragment extends ListFragment {
	
	private ArrayList<Operation> myArray= new ArrayList<Operation>();
	private OperationAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mAdapter=new OperationAdapter(getActivity(),R.layout.layout_expensesitem,myArray);
		setListAdapter(mAdapter);
		
        View expensesView = inflater.inflate(R.layout.fragment_expenses, container, false);

        Operation temp;
		for(int i=0;i<20;i++)
		{
//			temp= new Operation(i, i*2,(i*2)+1, 1000*i,
//					(i+3)+" month "+(2013+i),"This is the expense number : "+i);
			temp=new Operation(new Flat(), "test", "test", 10.2, "hello", false);
			
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