package dk.aau.mpp_project.activity;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;

import dk.aau.mpp_project.AddNewFlatActivity;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.cards.ChooseFlatCard;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewFlatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_flat);
		
		ArrayList<Card> cards = new ArrayList<Card>();

		// Generating cards
		for (int i = 0; i < 5; i++) {
			// Create a Card
			TextView t = new TextView(getApplicationContext());
			t.setText("This is a test flat");
			t.setTextSize(30);
			ImageView img = new ImageView(getApplicationContext());
			img.setBackgroundResource(R.drawable.flat1small);
			img.setPadding(0, 0, 0, 10);
			Card card = new ChooseFlatCard(getApplicationContext(), t, img);
			
			card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card arg0, View arg1) {
					// REDIRECT TO FLAT HERE
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				}
			});
			
//			card.setTitle("This is a sample card...");
			// Create a CardHeader
//			CardHeader header = new CardHeader(getApplicationContext());
//			header.setTitle("Card " + (i + 1));
			// Add Header to card
//			card.addCardHeader(header);
	        //Create thumbnail 
			
			card.setInnerLayout(R.layout.choose_flat);
//			TextView t = (TextView) findViewById(R.id.choose_card_text);
//			ImageView img = (ImageView) findViewById(R.id.choose_card_image);
//			t.setText("This is a test flat");
//			img.setBackgroundResource(R.drawable.flat1small);
			
			
	        //Set resource

	        //Add thumbnail to a card
//			card.setCardView(view);
			
			
			
			cards.add(card);
		}

		CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(
				getApplicationContext(), cards);

		CardListView listView = (CardListView) findViewById(R.id.cardList);
		
		if (listView != null) {
			listView.setAdapter(mCardArrayAdapter);
		}
		
		
		Button newFlatButton = (Button) findViewById(R.id.newFlatButton);
		Button joinFlatButton = (Button) findViewById(R.id.joinFlatButton);
		
		newFlatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddNewFlatActivity.class);
				startActivity(intent);
			}
		});
		
		joinFlatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.new_flat, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return true;
	}

}
