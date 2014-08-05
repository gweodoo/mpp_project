package dk.aau.mpp_project.fragments;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import dk.aau.mpp_project.R;

public class HomeFragment extends Fragment {
	
	public String name ="Home";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_home, container, false);

        ArrayList<Card> cards = new ArrayList<Card>();

        for(int i = 0; i<=20; i++){
	        //Create a Card
	        Card card = new Card(rootView.getContext());
	        card.setTitle("This is a sample card...");
	        //Create a CardHeader
	        CardHeader header = new CardHeader(rootView.getContext());
	        header.setTitle("Card "+(i+1));
	        //Add Header to card
	        card.addCardHeader(header);
	
	        cards.add(card);
        }
        
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        ListView listView = (ListView) rootView.findViewById(R.id.cardList);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }

        return rootView;
    }
}