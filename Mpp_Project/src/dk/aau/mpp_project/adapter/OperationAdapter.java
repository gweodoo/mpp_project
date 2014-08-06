package dk.aau.mpp_project.adapter;

import java.util.ArrayList;



import dk.aau.mpp_project.R;
import dk.aau.mpp_project.model.Operation;
import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OperationAdapter extends ArrayAdapter<Operation> {
	private ArrayList<Operation> objects;
	
	public OperationAdapter(Context context, int textViewResourceId, ArrayList<Operation> objects)
	{
		super(context, textViewResourceId, objects);
		this.objects=objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v=convertView;
	
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.fragment_expenses, null);
		}
		
		Operation i=objects.get(position);
		
		if (i != null) {

			// on référencie les objets TextView 
			//pour les remplir

			TextView tt = (TextView) v.findViewById(R.id.titletext);
			TextView ttd = (TextView) v.findViewById(R.id.valuetext);
			TextView dtd= (TextView) v.findViewById(R.id.datetext);


			// on vérifie que les TextView existe réellement et ne sont pas réutilisés
			if (tt != null){
				tt.setText(i.getTitle());
			}
			if (ttd != null){
				ttd.setText(i.getValue());
			}
			if (dtd != null){
				dtd.setText(i.getDate());
			}

		}
		
		return v;
	}

}
