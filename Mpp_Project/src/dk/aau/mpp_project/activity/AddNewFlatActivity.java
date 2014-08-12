package dk.aau.mpp_project.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import com.parse.ParseFile;
import com.parse.ParseUser;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.filter.Filter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AddNewFlatActivity extends Activity {

	protected EditText flatName;
	protected EditText password;
	protected EditText passwordRepeat;
	protected EditText address;
	protected ImageView flatImage;
	private ProgressDialog progressDialog;
	protected final int GALLERY_REQUEST = 1777;
	private static final int CAMERA_REQUEST = 1888;
	private static Bitmap bitmap;

	protected String errorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_flat);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		flatName = (EditText) findViewById(R.id.new_flat_name);
		password = (EditText) findViewById(R.id.new_flat_password);
		passwordRepeat = (EditText) findViewById(R.id.new_flat_password_repeat);
		address = (EditText) findViewById(R.id.new_flat_address);
		flatImage = (ImageView) findViewById(R.id.new_flat_image);

		ImageButton addFromGallery = (ImageButton) findViewById(R.id.add_pic_gallery);
		ImageButton addFromCamera = (ImageButton) findViewById(R.id.add_pic_camera);

		addFromGallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						GALLERY_REQUEST);
			}
		});

		addFromCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

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
			if (validateData()) {

				// save flat data
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {
					Bitmap b = Filter.rescale(bitmap, 300, true);//flatImage.getDrawingCache();

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					
					
					ParseFile img = new ParseFile("flat.jpg", byteArray);
//					img.saveInBackground();
					Flat flat = new Flat(flatName.getText().toString(), address
							.getText().toString(), currentUser.getObjectId(),
							0.0, img);
					DatabaseHelper.createFlat((MyUser)currentUser, flat, password.getText().toString());
//					flat.saveInBackground(new SaveCallback() {
//						@Override
//						public void done(ParseException e) {
//
//							if (progressDialog != null
//									&& progressDialog.isShowing()) {
//								progressDialog.dismiss();
//							}
//						}
//					});

				}

				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);

			} else {

			}
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

	public void onEventMainThread(StartEvent e) {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Saving flat...");
			progressDialog.setCancelable(false);
		}

		if (progressDialog != null && !progressDialog.isShowing())
			progressDialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		flatImage = (ImageView) findViewById(R.id.new_flat_image);
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			bitmap = (Bitmap) data.getExtras().get("data");
			
	        /*File f = new File(Environment.getExternalStorageDirectory().toString());

//            for (File temp : f.listFiles()) {
//
//                if (temp.getName().equals("temp.jpg")) {

//                    f = temp;
	        
                    File photo = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Config.RGB_565;
					options.inSampleSize = 2;
                    
                    
                    bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);
                    photo.delete();
                    if(bitmap==null)
                    	System.out.println("NULL");
                   //pic = photo;
                    
//                    break;
//                }
//
//            }*/
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
			
			
			flatImage.setImageBitmap(Filter.rescale(bitmap, width, false));
//            b = Filter.rescale(b, 1000, filter)
//			flatImage.setImageBitmap(b);
		} else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inSampleSize = 2;

			bitmap = BitmapFactory.decodeFile(picturePath, options);
			bitmap = Filter.rescale(bitmap, 300, false);
//			flatImage.setDrawingCacheEnabled(true);
			flatImage.setImageBitmap(bitmap);
			// flatImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
	}


	private boolean validateData() {
		Boolean valid = true;
		if (flatName.getText().toString().equals("")) {
			flatName.setError("Please enter a flat name.\n");
			valid = false;
		}
		if (password.getText().toString().equals("")) {
			password.setError("Please enter a password.\n");
			valid = false;
		}
		if (passwordRepeat.getText().toString().equals("")) {
			passwordRepeat.setError("Please repeat password.\n");
			valid = false;
		}
		if (address.getText().toString().equals("")) {
			address.setError("Please enter an address.\n");
			valid = false;
		}

		if (!password.getText().toString()
				.equals(passwordRepeat.getText().toString())) {
			passwordRepeat.setError("Please enter same password again.\n");
			valid = false;
		}
		return valid;
	}

	public boolean saveImageToInternalStorage(Bitmap image, String filename) {

		try {
			// Use the compress method on the Bitmap object to write image to
			// the OutputStream
			FileOutputStream fos = getApplicationContext().openFileOutput(
					filename, Context.MODE_PRIVATE);

			// Writing the bitmap to the output stream
			image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();

			return true;
		} catch (Exception e) {
			Log.e("saveToInternalStorage()", e.getMessage());
			return false;
		}
	}

}
