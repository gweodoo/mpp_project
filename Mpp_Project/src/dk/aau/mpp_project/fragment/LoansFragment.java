package dk.aau.mpp_project.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.model.LoanItem;
import dk.aau.mpp_project.model.LoanItemAdapter;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by adamj on 04/08/14.
 */
public class LoansFragment extends ListFragment {
    private View curView;
    private Button addButton;
    private TextView amountField;
    private TextView titleField;
    private Spinner idPeople;
    private ListView tableView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Hashtable<Integer, String> peopleTable = new Hashtable<Integer, String>();
        Log.i("NEWS", "Hi");
        curView = inflater.inflate(R.layout.fragment_loans, container, false);
        addButton = (Button)curView.findViewById(R.id.addingButton);
        amountField = (TextView)curView.findViewById(R.id.amountField);
        titleField = (TextView)curView.findViewById(R.id.titleField);
        idPeople = (Spinner) curView.findViewById(R.id.personSpinner);
        tableView = (ListView)curView.findViewById(R.id.listItem);

        /**
         * Fill the person spinner
         */
        //TODO: Fill list with Parse
        ArrayList<String> array = new ArrayList<String>();
        array.add("hi");
        array.add("ho");
        array.add("ha");
        ArrayAdapter<String> spinnerContent = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, array);
        spinnerContent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idPeople.setAdapter(spinnerContent);

        /**
         * Setting add Button trigger
         */
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
        Log.i("NEWS", "Hi");
        return curView;
    }

    private void addNewItem(){
        if(amountField.getText().toString().equals("") || idPeople.getSelectedItemId() == Spinner.INVALID_POSITION){
            Toast.makeText(getActivity().getBaseContext(), "Amount and People have to be filled !", Toast.LENGTH_LONG).show();
            return;
        }
        float amount = Float.parseFloat(amountField.getText().toString());

        if(amount <= 0 ){
            Toast.makeText(getActivity().getBaseContext(), "The amount cannot be equal to zero !", Toast.LENGTH_LONG).show();
            return;
        }

        String title = titleField.getText().toString();

//        int id = Integer.parseInt(idPeople.getSelectedItem().toString());
        //TODO: Adding to parse
        Log.i("ADD", "To "+ idPeople.getSelectedItem() +" Amount = "+amount+" / TITLE = "+title);
        refreshItemsList();
    }

    private void refreshItemsList() {
        //TODO: Interact with Parse to get Items
        Toast.makeText(getActivity().getBaseContext(), "Refresh", Toast.LENGTH_LONG).show();
        ArrayList<LoanItem> list = new ArrayList<LoanItem>();
        for(int i = 0; i < 1; i++) {
            list.add(new LoanItem("", "First", 10.0f, false));
        }

        tableView.setAdapter(new LoanItemAdapter(getActivity(),R.layout.layout_loanitem, list));

//            LinearLayout new_item = new LinearLayout(getActivity());
//            ImageView picUser = new ImageView(getActivity());
//            ImageView arrow = new ImageView(getActivity());
//            TextView info = new TextView(getActivity());
//            TextView amount = new TextView(getActivity());
//            CheckBox isPaid = new CheckBox(getActivity());
//
//            isPaid.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getActivity().getBaseContext(), "Changed status", Toast.LENGTH_LONG).show();
//                }
//            });
//            // object configuration
//            int d = (int)getActivity().getBaseContext().getResources().getDisplayMetrics().density;
//            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(50*d, 50*d);
//            param1.gravity = Gravity.CENTER_VERTICAL;
//            param1.setMargins(5*d, 5*d, 5*d, 5*d);
//            picUser.setLayoutParams(param1);
//            picUser.setBackgroundColor(Color.RED);
//
//
//            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(50*d, 50*d);
//            param2.setMargins(5 * d, 5 * d, 5 * d, 5 * d);
//            param2.gravity = Gravity.CENTER_VERTICAL;
//            arrow.setLayoutParams(param2);
//            arrow.setBackgroundColor(Color.BLUE);
//
//            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            param3.setMargins(10*d, 10*d, 10*d, 10*d);
//            param3.gravity = Gravity.CENTER_VERTICAL;
//            info.setText("plop"+ i);
//
//            LinearLayout.LayoutParams param4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            param4.setMargins(5*d, 5*d, 5*d, 5*d);
//            param4.gravity = Gravity.CENTER_VERTICAL;
//            amount.setTextColor(Color.parseColor("#979593"));
//            amount.setText(i + " €");
//
//            LinearLayout.LayoutParams param5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            param5.setMargins(5*d, 5*d, 5*d, 5*d);
//            param5.gravity = Gravity.CENTER_VERTICAL;
//            param5.weight = 0.10f;
//            isPaid.setText("Paid");
//            isPaid.setChecked(false);
//
//            //LAYOUT CONFIGURATION
//            LinearLayout.LayoutParams param6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            param6.setMargins(0*d, 20*d, 0*d, 0*d);
//            new_item.setLayoutParams(param6);
//            new_item.setBackgroundColor(Color.parseColor("#ffffff"));
//
//            // ADDING
//            new_item.addView(picUser);
//            new_item.addView(arrow);
//            new_item.addView(info);
//            new_item.addView(amount);
//            new_item.addView(isPaid);
//          tableView.addView(new_item);
    }
}