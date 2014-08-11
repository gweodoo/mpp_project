package dk.aau.mpp_project.activity;


import dk.aau.mpp_project.R;
import dk.aau.mpp_project.R.layout;
import dk.aau.mpp_project.filter.Filter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AddNewFlatActivity extends Activity {

	protected EditText flatName;
	protected EditText password;
	protected EditText passwordRepeat;
	protected EditText address;
	protected ImageView flatImage;
	protected final int GALLERY_REQUEST = 1777;
    private static final int CAMERA_REQUEST = 1888; 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_flat);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		ImageButton addFromGallery = (ImageButton) findViewById(R.id.add_pic_gallery);
		ImageButton addFromCamera = (ImageButton) findViewById(R.id.add_pic_camera);
		
		addFromGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST );
			}
		});
		
		addFromCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                startActivityForResult(cameraIntent, CAMERA_REQUEST);				
			}
		});
		
//		CardListView cardList = (CardListView) findViewById(R.id.cardList);
//		Card c1 = new AddFlatLoginCard(getApplication());
//		c1.setClickable(false);
//		Card c2 = new AddFlatDetailCard(getApplication());
//		c2.setClickable(false);
//		ArrayList<Card> cards = new ArrayList<Card>();
//		cards.add(c1);
//		cards.add(c2);
//		CardArrayAdapter adapter = new CardArrayAdapter(getApplicationContext(), cards);
//		cardList.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_flat_ok, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.action_new_flat_ok:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
        case android.R.id.home:
            // app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        flatImage = (ImageView) findViewById(R.id.new_flat_image);
    	if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
            Bitmap b = (Bitmap) data.getExtras().get("data"); 
            flatImage.setImageBitmap(rescale(b));
        }
    	else if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            Bitmap b = BitmapFactory.decodeFile(picturePath);

            
            flatImage.setImageBitmap(rescale(b));
            //flatImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    	}
    } 
    
    public Bitmap rescale(Bitmap b){
        final int maxSize = 1000;
        int outWidth;
        int outHeight;
        int inWidth = b.getWidth();
        int inHeight = b.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth; 
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight; 
        }
        b = Bitmap.createScaledBitmap(b, outWidth, outHeight, false);
        return Filter.fastblur(b, 20);
    }

}
