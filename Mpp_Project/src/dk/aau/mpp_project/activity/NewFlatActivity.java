package dk.aau.mpp_project.activity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.R.layout;
import dk.aau.mpp_project.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NewFlatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_flat);
		
		ArrayList<Card> cards = new ArrayList<Card>();

		// Generating cards
		for (int i = 0; i <= 20; i++) {
			// Create a Card
			Card card = new Card(getApplicationContext());
			card.setTitle("This is a sample card...");
			// Create a CardHeader
			CardHeader header = new CardHeader(getApplicationContext());
			header.setTitle("Card " + (i + 1));
			// Add Header to card
			card.addCardHeader(header);

			cards.add(card);
		}

		CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(
				getApplicationContext(), cards);

		CardListView listView = (CardListView) findViewById(R.id.cardList);
		
		if (listView != null) {
			listView.setAdapter(mCardArrayAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_flat, menu);
		return true;
	}

}