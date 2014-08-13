package dk.aau.mpp_project.adapter;

import java.util.ArrayList;

import dk.aau.mpp_project.R;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.SpinnerModel;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/***** Adapter class extends with ArrayAdapter ******/
public class SpinnerAdapter extends ArrayAdapter<MyUser> {

	private Resources		res;
	private LayoutInflater	inflater;

	/************* CustomAdapter Constructor *****************/
	public SpinnerAdapter(Context context, int textViewResourceId,
			ArrayList<MyUser> objects, Resources resLocal) {
		super(context, textViewResourceId, objects);

		/********** Take passed values **********/
		res = resLocal;

		/*********** Layout inflator to call external xml layout () **********************/
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	// This funtion called for each row ( Called data.size() times )
	public View getCustomView(int position, View convertView, ViewGroup parent) {

		/********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
		View row = inflater.inflate(R.layout.spinner_rows, parent, false);

		/***** Get each Model object from Arraylist ********/
		MyUser user = getItem(position);

		TextView spinnerUser = (TextView) row.findViewById(R.id.spinnerUser);
		TextView sub = (TextView) row.findViewById(R.id.sub);
		ImageView companyLogo = (ImageView) row.findViewById(R.id.image);

		// if (position == 0) {
		//
		// // Default selected Spinner item
		// spinnerUser.setText("All");
		// sub.setText("");
		// } else {
		// Set values for spinner each row
		spinnerUser.setText(user.getName());
		companyLogo.setImageResource(res.getIdentifier(
				"dk.aau.mpp_project:drawable/" + "image_user", null, null));
		// }

		return row;
	}
}