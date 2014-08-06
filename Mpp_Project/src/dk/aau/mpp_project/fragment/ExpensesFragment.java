package dk.aau.mpp_project.fragment;

import java.util.ArrayList;
import java.util.List;

import dk.aau.mpp_project.R;


import dk.aau.mpp_project.adapter.OperationAdapter;
import dk.aau.mpp_project.model.Operation;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpensesFragment extends ListFragment {
	
	private ArrayList<Operation> myArray= new ArrayList<Operation>();
	private OperationAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mAdapter=new OperationAdapter(getActivity(),R.layout.fragment_expenses,myArray);
		setListAdapter(mAdapter);
		
        View expensesView = inflater.inflate(R.layout.fragment_expenses, container, false);

        Operation temp;
		for(int i=0;i<20;i++)
		{
			temp= new Operation(i, i*2,(i*2)+1, 1000*i,
					(i+3)+" month "+(2013+i),"This is the expense number : "+i);
			myArray.add(temp);
			
		}
        
		mAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		return expensesView;
	}
	
	
	

}