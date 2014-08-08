package dk.aau.mpp_project.cards;
import dk.aau.mpp_project.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import it.gmariotti.cardslib.library.internal.Card;

public class ChooseFlatCard extends Card {

        protected TextView mTitle;
		protected ImageView mImage;

        /**
         * Constructor with a custom inner layout
         * @param context
         */
        public ChooseFlatCard(Context context) {
            this(context, R.layout.choose_flat);
        }

        /**
         *
         * @param context
         * @param innerLayout
         */
        public ChooseFlatCard(Context context, int innerLayout) {
            super(context, innerLayout);
            init();
        }

        /**
         * Init
         */
        private void init(){

            //No Header

            //Set a OnClickListener listener
            setOnClickListener(new OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                }
            });
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            //Retrieve elements
            mTitle = (TextView) parent.findViewById(R.id.choose_card_text);
            mImage = (ImageView) parent.findViewById(R.id.choose_card_image);


            if (mTitle!=null)
                mTitle.setText("Test flat");
            
            if(mImage != null)
            	mImage.setBackgroundResource(R.drawable.flat1small);

        }
    }