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
			v = inflater.inflate(R.layout.layout_expensesitem, parent, false);
		}
		
		Operation i=objects.get(position);
		
		if (i != null) {

			// on référencie les objets TextView 
			//pour les remplir

			TextView commentText = (TextView) v.findViewById(R.id.commenttext);
			TextView priceText = (TextView) v.findViewById(R.id.pricetext);
			TextView dateText= (TextView) v.findViewById(R.id.datetext);


			// on vérifie que les TextView existe réellement et ne sont pas réutilisés
			if (commentText != null){
				//tt.setText(i.getTitle());
				commentText.setText(i.getComment());
			}
			if (priceText != null){
				//ttd.setText(i.getValue());
				priceText.setText(Double.toString(i.getAmount()));
			}
			if (dateText != null){
				//dtd.setText(i.getDate());
				dateText.setText(i.getLender());
			}

		}
		
		return v;
	}

}
