package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements FragmentEventHandler{

	public String	name	= "Home";
    private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section_home,
				container, false);

		ArrayList<Card> cards = new ArrayList<Card>();

		// Generating cards
		for (int i = 0; i <= 20; i++) {
			// Create a Card
			Card card = new Card(rootView.getContext());
			card.setTitle("This is a sample card...");
			// Create a CardHeader
			CardHeader header = new CardHeader(rootView.getContext());
			header.setTitle("Card " + (i + 1));
			// Add Header to card
			card.addCardHeader(header);

			cards.add(card);
		}

		CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(
				getActivity(), cards);

		CardListView listView = (CardListView) rootView
				.findViewById(R.id.cardListHome);
        listView.setId(0);
		// Adding header flat image
		View v = null;
		LinearLayout main = new LinearLayout(rootView.getContext());
		main.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.flat1small));
		LinearLayout top = new LinearLayout(rootView.getContext());
		main.setOrientation(LinearLayout.VERTICAL);
		top.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout bottom = new LinearLayout(rootView.getContext());
		bottom.setOrientation(LinearLayout.HORIZONTAL);

		// adding FLat Title and navigation button
		TextView title = new TextView(this.getActivity());
		title.setTextSize(30);
		title.setTextColor(Color.WHITE);
		title.setText("Flat Title");
		title.setPadding(20, 10, 0, 0);
		top.addView(title, 0);
		final ImageButton loc = new ImageButton(rootView.getContext());
		loc.setBackgroundResource(android.R.color.transparent);
		loc.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_loc));

		top.addView(loc);
		top.setPadding(0, 0, 0, 150);

		// Adding roommate avatars
		ImageView a1 = new ImageView(rootView.getContext());
		a1.setImageDrawable(getResources().getDrawable(R.drawable.av1));
		a1.setAdjustViewBounds(true);
		a1.setMaxHeight(200);
		a1.setMaxWidth(200);

		ImageView a2 = new ImageView(rootView.getContext());
		a2.setImageDrawable(getResources().getDrawable(R.drawable.av2));
		a2.setAdjustViewBounds(true);
		a2.setMaxHeight(200);
		a2.setMaxWidth(200);

		ImageView a3 = new ImageView(rootView.getContext());
		a3.setImageDrawable(getResources().getDrawable(R.drawable.av3));
		a3.setAdjustViewBounds(true);
		a3.setMaxHeight(200);
		a3.setMaxWidth(200);

		ImageView a4 = new ImageView(rootView.getContext());
		a4.setImageDrawable(getResources().getDrawable(R.drawable.av4));
		a4.setAdjustViewBounds(true);
		a4.setMaxHeight(200);
		a4.setMaxWidth(200);

		bottom.addView(a1, 0);
		bottom.addView(a2, 1);
		bottom.addView(a3, 2);
		bottom.addView(a4, 3);

		main.addView(top);
		main.addView(bottom);

		listView.addHeaderView(main);

		// adding header text
		TextView t = new TextView(this.getActivity());
		t.setTextSize(20);
		t.setText("Last activities");
		t.setPadding(30, 50, 0, 30);
		// t.setTypeface(tf, style)
		listView.addHeaderView(t);

		if (listView != null) {
			listView.setAdapter(mCardArrayAdapter);
		}

		return rootView;
    }

    @Override
    public void onResumeEvent() {

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPauseEvent() {

        EventBus.getDefault().unregister(this);
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

            // Check for what you wanted to retrieve
            if (DatabaseHelper.ACTION_GET_USER_FLATS.equals(e.getAction())) {
                // You know what you need (List or simple object)
                // If List: ArrayList<Flat> flatsList =
                // e.getExtras().getParcelableArrayList("data");
                // If Object only : Flat flat =
                // e.getExtras().getParcelable("data");

                ArrayList<Flat> flatsList = e.getExtras()
                        .getParcelableArrayList("data");

                // if (flatsList.size() == 0) {
                // goToNewFlatActivity();
                // } else if (flatsList.size() == 1) {
                // goToMainActivity(flatsList.get(0));
                // } else if (flatsList.size() > 1) {
                // goToNewFlatActivity(flatsList);
                // }

                // for (Flat f : flatsList)
                // Log.v(TAG,
                // "# Flat : " + f.getName() + " : "
                // + f.getRentAmount() + "$");

            } else if (DatabaseHelper.ACTION_GET_NEWS_FLATS.equals(e
                    .getAction())) {

            } else if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e
                    .getAction())) {

            }
        }
    }
}