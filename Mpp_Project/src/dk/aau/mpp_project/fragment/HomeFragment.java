package dk.aau.mpp_project.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.widget.CircularImageView;

public class HomeFragment extends Fragment implements FragmentEventHandler {

	public String			name	= "Home";
	private ProgressDialog	progressDialog;
	private ArrayList<News>	newsList;

	private ListView		listView;
	private NewsAdapter		adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.fragment_section_home, container, false);

		newsList = new ArrayList<News>();

		// getting information about the current environment
		final MyUser user = ((MainActivity) getActivity()).getMyUser();
		final Flat flat = ((MainActivity) getActivity()).getMyFlat();

		listView = (ListView) rootView.findViewById(R.id.list);

		if (flat == null) {
			Toast.makeText(getActivity(), "Error : No flat found !",
					Toast.LENGTH_LONG).show();
			return rootView;
		}

		RelativeLayout rlMain = (RelativeLayout) inflater.inflate(
				R.layout.layout_home_top_view, null, false);

		RelativeLayout llMain = (RelativeLayout) rlMain.findViewById(R.id.main);
		llMain.setBackgroundResource(R.drawable.flat1small);

		TextView titleFlat = (TextView) rlMain.findViewById(R.id.titleFlat);
		titleFlat.setText(flat.getName());

		ImageButton buttonLocation = (ImageButton) rlMain
				.findViewById(R.id.buttonLocation);
		buttonLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flat != null) {
					String map = "http://maps.google.co.in/maps?q="
							+ flat.getAddress();
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
					startActivity(i);
				}
			}
		});

		LinearLayout bottom = (LinearLayout) rlMain.findViewById(R.id.bottom);

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

		listView.addHeaderView(rlMain);

		adapter = new NewsAdapter(getActivity(), R.layout.layout_item_news,
				newsList);
		listView.setAdapter(adapter);

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

			if (DatabaseHelper.ACTION_GET_NEWS_FLATS.equals(e.getAction())) {
				ArrayList<News> tmp = e.getExtras().getParcelableArrayList(
						"data");

				newsList.clear();
				newsList.addAll(tmp);

				adapter.notifyDataSetChanged();

			} else if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e
					.getAction())) {

			}
		}
	}

	public class NewsAdapter extends ArrayAdapter<News> {

		public class ViewHolder {
			public CircularImageView	photo;
			public TextView				comment;
			public TextView				date;
		}

		private Context	context;
		private int		resource;

		public NewsAdapter(Context context, int resource, List<News> objects) {
			super(context, resource, objects);

			this.context = context;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(resource, parent, false);

				viewHolder = new ViewHolder();

				viewHolder.photo = (CircularImageView) convertView
						.findViewById(R.id.photo);
				viewHolder.comment = (TextView) convertView
						.findViewById(R.id.comment);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.date);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			News news = this.getItem(position);

			viewHolder.comment.setText(news.getComment());
			viewHolder.date.setText(news.getDate());

			return convertView;
		}
	}
}