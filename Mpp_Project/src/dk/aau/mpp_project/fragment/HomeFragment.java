package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.News;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements FragmentEventHandler{

	public String	name	= "Home";
    private ProgressDialog progressDialog;
    private ArrayList<News> tabNews;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_section_home,
				container, false);

        //getting information about the current environment
        final MyUser user = ((MainActivity)getActivity()).getMyUser();
        final Flat flat = ((MainActivity)getActivity()).getMyFlat();
        ArrayList<Card> cards = new ArrayList<Card>();
		CardListView listView = (CardListView) rootView
				.findViewById(R.id.cardListHome);

        if(flat == null){
            Toast.makeText(getActivity(), "Error : No flat found !", Toast.LENGTH_LONG).show();
            return rootView;
        }
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
		title.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		title.setTextSize(30);
		title.setTextColor(Color.WHITE);
		title.setText(flat.getName());
		title.setPadding(20, 10, 0, 0);
		top.addView(title, 0);
		final ImageButton loc = new ImageButton(rootView.getContext());
		loc.setBackgroundResource(android.R.color.transparent);
		loc.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_action_loc));

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flat != null) {
                    String map = "http://maps.google.co.in/maps?q=" + flat.getAddress();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);
                }
            }
        });
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

        if(flat != null){
            DatabaseHelper.getNewsByFlat(flat);
            if(tabNews != null) {
                for (News item : tabNews){
                    Card cur = new Card(getActivity());
                    CardHeader head = new CardHeader(getActivity());
                    CardThumbnail thumb = new CardThumbnail(getActivity());

                    thumb.setDrawableResource(R.drawable.av1);
                    head.setTitle("From "+item.getUser().getName());

                    cur.addCardThumbnail(thumb);
                    cur.addCardHeader(head);
                    cur.setTitle(item.getComment());

                    cards.add(cur);
                }
            }
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(
                getActivity(), cards);
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
                tabNews =  e.getExtras().getParcelableArrayList("data");

            } else if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e
                    .getAction())) {

            }
        }
    }
}