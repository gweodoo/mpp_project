package dk.aau.mpp_project.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.filter.CircleImageView;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.Operation;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class, representing a fragment, displays all information about expenses for a specific user, the current one
 * Each item displayed contains a check box, allowing lender to agree when he have been refunded
 */
public class LoansFragment extends Fragment implements FragmentEventHandler,SwipeRefreshLayout.OnRefreshListener {
    /**
     * Class members
     */
    private View curView;
	private Activity mActivity;
    private CardListView tableView;
    private ProgressDialog progressDialog;
    private ArrayList<Operation> tabOperations;
	private SwipeRefreshLayout swipeRefresh;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //calling current fragment view
        curView = inflater.inflate(R.layout.fragment_section_loans, container, false);
		swipeRefresh = (SwipeRefreshLayout)curView.findViewById(R.id.swipe_container);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  android.R.color.holo_orange_light, android.R.color.holo_red_light);

        //getting the cardListview
        tableView = (CardListView)curView.findViewById(R.id.cardListLoans);
        
        return curView;
    }

    /**
     * Function which refresh the listview with up-to-date data.
     */
    private void refreshItemsList() {
        //getting information about the current environment
        MyUser user = ((MainActivity)mActivity).getMyUser();
        Flat flat = ((MainActivity)mActivity).getMyFlat();

        // some checks if neither user nor flat have been found (which should be errors)
        if(user == null){
            Toast.makeText(mActivity, "Error : Who are you ?", Toast.LENGTH_LONG).show();
            return;
        }

        if(flat == null){
            Toast.makeText(mActivity, "Error : No flat found for you !", Toast.LENGTH_LONG).show();
            return;
        }

        //get all entries as operations for the current flat in the database
        DatabaseHelper.getOperationsByFlat(flat);
    }

    @Override
    public void onResumeEvent() {
        //when fragment has focus, register it in event table (used to block user interface when critical interaction with database
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPauseEvent() {
        //release the event tracking when the fragment lost focus
        EventBus.getDefault().unregister(this);
    }

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {

			@Override public void run() {
				swipeRefresh.setRefreshing(false);
			}
		}, 2000);
		refreshItemsList();
	}

	/**
     * After the cardslib using, we redefine our own card model, thanks to some layouts
     */
    public class CustomCard extends Card {

        private Operation cur;
        //create the card and get the operation attached to the current card
        public CustomCard(Context context, Operation item) {
            super(context, R.layout.layout_loan_item);
            cur = item;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            //getting fields in card
            TextView info = (TextView)parent.findViewById(R.id.infoItem);
            TextView amount = (TextView)parent.findViewById(R.id.amountItem);
			CircleImageView picUser = (CircleImageView)parent.findViewById(R.id.picUser);
			CircleImageView picYou = (CircleImageView)parent.findViewById(R.id.picYou);
			ImageView picArrow = (ImageView)parent.findViewById(R.id.picArrow);
            CheckBox paid = (CheckBox)parent.findViewById(R.id.isPaid);
            LinearLayout layout = (LinearLayout)parent.findViewById(R.id.loan_card_layout);

            //setting comments
            info.setText(cur.getComment());

            //setting amount (cast with two decimals)
            amount.setText((new DecimalFormat("#.00")).format(cur.getAmount())+" Kr");

            //setting users pictures (you and the other one)
			Resources r = getResources();
			int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
			Drawable d = new ScaleDrawable(getResources().getDrawable(R.drawable.image_user), 0, 50, 50).getDrawable();
			UrlImageViewHelper.setUrlDrawable(picYou, "http://graph.facebook.com/" + ((MainActivity) mActivity).getMyUser().getFacebookId() + "/picture?width="+px+"&height="+px, d, 3600000);

//			picYou.setImageDrawable(MainActivity.getUserDetails(user))
			
			Log.i("TOOT", "" + ((MainActivity) mActivity).getMyUser().getFacebookId());
//			picYou.setBorderWidth(3);
//			picYou.setBorderColor(Color.WHITE);
//			picYou.setPadding(10, 10, 10, 10);

            //setting paid status
            paid.setChecked(cur.getIsPaid());

            //differentiation whether current user is the lender or not
			if(cur.getLender().equals(((MainActivity)mActivity).getMyUser())) {
                //in this case : green background, checkbox enabled
                layout.setBackgroundColor(Color.parseColor("#E3FBE9"));
                picArrow.setImageDrawable(getResources().getDrawable(R.drawable.green_arrow));
                paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //TODO: mark items as paid in parse
						cur.setIsPaid(isChecked);
						cur.saveInBackground();
                        Toast.makeText(mActivity, "Transaction registered", Toast.LENGTH_LONG).show();
                    }
                });
//				Resources r2 = getResources();
//				int px2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r2.getDisplayMetrics());
//				Drawable d2 = new ScaleDrawable(getResources().getDrawable(R.drawable.image_user), 0, 50, 50).getDrawable();
                picUser.setImageDrawable(MainActivity.getUserDetails(cur.getTo()).getPhotoView().getDrawable());
//				UrlImageViewHelper.setUrlDrawable(picUser, "http://graph.facebook.com/" + cur.getTo().getFacebookId() + "/picture?width=" + px2 + "&height=" + px2, d2, 3600000);
//				picUser.setBorderWidth(3);
//				picUser.setBorderColor(Color.WHITE);
//				picUser.setPadding(10, 10, 10, 10);
            }
            else{
                //otherwise red background and checkbox disabled (but still visible)
                layout.setBackgroundColor(Color.parseColor("#FFECEC"));
                picArrow.setImageDrawable(getResources().getDrawable(R.drawable.red_arrow));
                paid.setClickable(false);
				Resources r2 = getResources();
				int px2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r2.getDisplayMetrics());
				Drawable d2 = new ScaleDrawable(getResources().getDrawable(R.drawable.image_user), 0, 50, 50).getDrawable();
				picUser.setImageDrawable(MainActivity.getUserDetails(cur.getLender()).getPhotoView().getDrawable());
//				UrlImageViewHelper.setUrlDrawable(picUser, "http://graph.facebook.com/" + cur.getLender().getFacebookId() + "/picture?width=" + px2 + "&height=" + px2, d2, 3600000);
//				picUser.setBorderWidth(3);
//				picUser.setBorderColor(Color.WHITE);
//				picUser.setPadding(10, 10, 10, 10);
            }
        }
    }

    //function definition when a starting event is triggered by the fragment
    public void onEventMainThread(StartEvent e) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
        }

        //display an infinite progressDialog, in order to interrupt user experience
        //this is used when the application requires critical request to the database
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    //function definition when a FinishedEvent is triggered by the fragment
    public void onEventMainThread(FinishedEvent e) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Success retreiving database
        if (e.isSuccess()) {
            //getting specific data according request type
            if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e.getAction())) {
                tabOperations = e.getExtras().getParcelableArrayList("data");
                fillListView();
            }
        }
    }

    public void fillListView(){

        MyUser user = ((MainActivity)mActivity).getMyUser();
        ArrayList<Card> cards = new ArrayList<Card>();

        //if no operations have been found in the database
        if(tabOperations == null){
            Toast.makeText(mActivity, "No transactions found for this flat", Toast.LENGTH_LONG).show();
            return;
        }

        //sorting operations in order to have most recent first (= on top)
//        Collections.sort(tabOperations, new Comparator<Operation>() {
//            @Override
//            public int compare(Operation lhs, Operation rhs) {
//                //comparison between operation dates (maybe need to re-check)
//                if (lhs.getDate().compareTo(rhs.getDate()) > 0)
//                    return 1;
//                else return -1;
//            }
//        });

        //for each item loaded from the database
        for(Operation item : tabOperations) {
            // if the current user, is not implied in the current operation, shift it and continue
            if(! (item.getLender().equals(user) || item.getTo().equals(user))){
                continue;
            }

            //Generating cards content
            CustomCard card = new CustomCard(mActivity, item);
            //adding the card to the list of new loaded cards
            cards.add(card);
        }

        //if there are no operations found for this user, we stop here
        if(cards.size() == 0){
            Toast.makeText(mActivity, "No transactions where you are implied have been found :)", Toast.LENGTH_LONG).show();
            return;
        }

        //setting the new adapter to the cardListView
        tableView.setAdapter(new CardArrayAdapter(mActivity, cards));
    }
}