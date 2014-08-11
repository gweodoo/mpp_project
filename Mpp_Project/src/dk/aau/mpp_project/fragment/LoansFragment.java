package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.Operation;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by adamj on 04/08/14.
 */
public class LoansFragment extends Fragment implements FragmentEventHandler {
    private View curView;
    private Button addButton;
    private TextView amountField;
    private TextView titleField;
    private Spinner idPeople;
    private CardListView tableView;
    private CardArrayAdapter mCardArrayAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Operation> tabOperations;

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
        Toast.makeText(getActivity().getBaseContext(), "Refresh", Toast.LENGTH_LONG).show();
        ArrayList<Card> cards = new ArrayList<Card>();
        ArrayList<Operation> tabItems = new ArrayList<Operation>();

        //TODO: Get previous entries with parse
        //DatabaseHelper.getOperationsByFlat(new Flat());
        //TODO: Get specific operations
        tabItems.add(new Operation(new Flat(), "Me", "You", 10.0f, "", "This is the first Loan Test", false));
        tabItems.add(new Operation(new Flat(), "You", "Me", 255.0f, "", "This is the Second Loan Test", true));
        tabItems.add(new Operation(new Flat(), "You", "Me", 1000.0f, "", "This is the Third Loan Test", false));
        tabItems.add(new Operation(new Flat(), "Me", "You", 10500.0f, "", "This is the Fourth Loan Test", true));

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


    @Override
    public void onResumeEvent() {

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPauseEvent() {

        EventBus.getDefault().unregister(this);
    }

    public class CustomCard extends Card {

        private Operation cur;
        public CustomCard(Context context, Operation item) {
            super(context, R.layout.layout_loan_item);
            cur = item;
        }

        public void init(){

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            TextView info = (TextView)parent.findViewById(R.id.infoItem);
            info.setText(cur.getComment());
            TextView amount = (TextView)parent.findViewById(R.id.amountItem);
            amount.setText((new DecimalFormat("#.00")).format(cur.getAmount())+" €");
            ImageView picUser = (ImageView)parent.findViewById(R.id.picUser);
            ImageView picYou = (ImageView)parent.findViewById(R.id.picYou);
            ImageView picArrow = (ImageView)parent.findViewById(R.id.picArrow);

            picUser.setImageDrawable(getResources().getDrawable(R.drawable.av1));
            picYou.setImageDrawable(getResources().getDrawable(R.drawable.av1));

            CheckBox paid = (CheckBox)parent.findViewById(R.id.isPaid);
            paid.setChecked(cur.getIsPaid());
            LinearLayout layout = (LinearLayout)parent.findViewById(R.id.loan_card_layout);
            if(cur.getLender().equals(((MainActivity)getActivity()).getMyUser().getObjectId())) {
                layout.setBackgroundColor(Color.parseColor("#E3FBE9"));
                picArrow.setImageDrawable(getResources().getDrawable(R.drawable.green_arrow));
                paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //TODO: mark items as paid in parse
                        Toast.makeText(getActivity(), "Transaction registered", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                layout.setBackgroundColor(Color.parseColor("#FFDFDF"));
                picArrow.setImageDrawable(getResources().getDrawable(R.drawable.red_arrow));
                paid.setClickable(false);
            }
        }
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
            if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e.getAction())) {
                tabOperations = e.getExtras().getParcelableArrayList("data");
            }
        }
    }
}