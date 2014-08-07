package dk.aau.mpp_project.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import dk.aau.mpp_project.R;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by adamj on 04/08/14.
 */
public class LoansFragment extends Fragment {
    private View curView;
    private Button addButton;
    private TextView amountField;
    private TextView titleField;
    private Spinner idPeople;
    private CardListView tableView;
    private CardArrayAdapter mCardArrayAdapter;

    // TODO: To remove later (after parse integration)
    private static int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Hashtable<Integer, String> peopleTable = new Hashtable<Integer, String>();
        Log.i("NEWS", "Hi");
        curView = inflater.inflate(R.layout.fragment_loans, container, false);
        curView = inflater.inflate(R.layout.fragment_section_loans, container, false);
        addButton = (Button)curView.findViewById(R.id.addingButton);
        amountField = (TextView)curView.findViewById(R.id.amountField);
        titleField = (TextView)curView.findViewById(R.id.titleField);
        idPeople = (Spinner) curView.findViewById(R.id.personSpinner);
        tableView = (CardListView)curView.findViewById(R.id.cardList);
        //adding header text
        TextView t = new TextView(this.getActivity());
        t.setTextSize(20);
        t.setText("Last loans");
        t.setPadding(30, 50, 0, 30);
        tableView.addHeaderView(t);

        // setting CardListView
       mCardArrayAdapter = new CardArrayAdapter(getActivity(),new ArrayList<Card>());

        if (tableView!=null){
            tableView.setAdapter(mCardArrayAdapter);
        }

        refreshItemsList();

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

        amountField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    if(!v.isFocused()){
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                return false;
            }
        });

        titleField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && v.getId() == R.id.titleField){
                    InputMethodManager imm =  (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

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
        Log.i("LOANS", "REFRESH");
        //TODO: Interact with Parse to get Items
        Toast.makeText(getActivity().getBaseContext(), "Refresh", Toast.LENGTH_LONG).show();
        ArrayList<Card> cards = new ArrayList<Card>();

        mAdapter.setData(list);
        //TODO: Get previous entries with parse
        //Generating cards
        Card card = new Card(this.getActivity());
        CardThumbnail thumb = new CardThumbnail(this.getActivity());
        thumb.setDrawableResource(R.drawable.ic_launcher);

        card.addCardThumbnail(thumb);
        card.setTitle("Card "+(i++));
        //Create a CardHeader
        CardHeader header = new CardHeader(curView.getContext());
        header.setTitle("Card " + i);
        //Add Header to card
        card.addCardHeader(header);

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