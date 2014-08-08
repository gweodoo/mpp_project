package dk.aau.mpp_project.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.Operation;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        curView = inflater.inflate(R.layout.fragment_section_loans, container, false);
        tableView = (CardListView)curView.findViewById(R.id.cardListLoans);

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

        return curView;
    }

    private void refreshItemsList() {
        Log.i("LOANS", "REFRESH");
        Toast.makeText(getActivity().getBaseContext(), "Refresh", Toast.LENGTH_LONG).show();
        ArrayList<Card> cards = new ArrayList<Card>();
        ArrayList<Operation> tabItems = new ArrayList<Operation>();

        //TODO: Get previous entries with parse
        tabItems.add(new Operation(new Flat(), "Me", "You", 10.0f, "", "This is the first Loan Test", false));

        Collections.sort(tabItems, new Comparator<Operation>() {
            @Override
            public int compare(Operation lhs, Operation rhs) {
                if (lhs.getDate().compareTo(rhs.getDate()) > 0)
                    return 1;
                else return -1;
            }
        });
        for(Operation item : tabItems) {
            //Generating cards
            CustomCard card = new CustomCard(getActivity(), item);
            card.init();
            cards.add(card);
        }
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        tableView.setAdapter(mCardArrayAdapter);

    }
    public class CustomCard extends Card {

        private Operation cur;
        public CustomCard(Context context, Operation item) {
            super(context, R.layout.layout_loan_item);
            cur = item;
        }

        public void init(){
            //setTitle("Date          : "+ cur.getDate()+"" +"\nComment : "+cur.getComment());
            //Create a CardHeader
            //CardHeader header = new CardHeader(curView.getContext());
            //header.setTitle(cur.getTo()+ " ows "+cur.getLender() + " "+(new DecimalFormat("#.00")).format(cur.getAmount())+" €");
            //Add Header to card
            //addCardHeader(header);
            Log.i("Debug", " PROUT");
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            TextView toto = (TextView)parent.findViewById(R.id.infoItem);
            Log.e("Debug"," : toto = "+toto);
            toto.setText("Banzai...");
            ((CheckBox)parent.findViewById(R.id.isPaid)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(getActivity(), "PlopMagic", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}