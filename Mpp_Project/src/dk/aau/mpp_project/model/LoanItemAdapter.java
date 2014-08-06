package dk.aau.mpp_project.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import dk.aau.mpp_project.R;

import java.util.ArrayList;

/**
 * Created by adamj on 06/08/14.
 */
public class LoanItemAdapter extends ArrayAdapter<LoanItem> {

    private LayoutInflater mInflater;
    public LoanItemAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<LoanItem> data){
        clear();
        if(data == null) return;
        for(LoanItem item : data){
            add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.layout_loanitem, null);
        }

        TextView info = (TextView)convertView.findViewById(R.id.infoItem);
        TextView amount = (TextView)convertView.findViewById(R.id.amountItem);
        CheckBox isPaid = (CheckBox)convertView.findViewById(R.id.isPaid);
//        TextView info = (TextView)rowView.findViewById(R.id.infoItem);
        LoanItem item = getItem(position);
        if (item!= null) {
            info.setText(item.getInfo());
            amount.setText(item.getAmount() + " €");
            isPaid.setChecked(false);
        }
        Log.i("INFO", "ADAPTER");
        return convertView;
    }
}
