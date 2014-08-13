package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.ParseException;
import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.R;
import dk.aau.mpp_project.activity.MainActivity;
import dk.aau.mpp_project.database.DatabaseHelper;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.filter.Filter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.widget.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FragmentEventHandler,SwipeRefreshLayout.OnRefreshListener {

	public String			name	= "Home";
	private ProgressDialog	progressDialog;
	private ArrayList<News>	newsList;

	private ListView		listView;
	private NewsAdapter		adapter;
	private SwipeRefreshLayout swipeRefresh;
	private boolean showProgress = true;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.fragment_section_home, container, false);
		swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,  android.R.color.holo_orange_light, android.R.color.holo_red_light);

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
//		llMain.setBackgroundResource(R.drawable.flat1small);
		if(flat!=null){
			if(flat.getPhoto()!=null){
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Config.RGB_565;
					options.inSampleSize = 2;
					
					Bitmap b = BitmapFactory.decodeByteArray(flat.getPhoto().getData() , 0, flat.getPhoto().getData().length, options);
					
					Display display = getActivity().getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					b = Filter.rescale(b, width, false);
					llMain.setBackgroundDrawable(new BitmapDrawable(getResources(), Filter.fastblur(b, 20)));
	//				viewHolder.flatImage.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(b, 1000, 100, true)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.flat1small));
			}
		}

		TextView titleFlat = (TextView) rlMain.findViewById(R.id.titleFlat);
		titleFlat.setText(flat.getName());
		titleFlat.setTextColor(Color.WHITE);
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

		System.out.println("number of users "+ flat.getFlatUsers().size());
		int i = 0;
		for(MyUser u : flat.getFlatUsers()){
			System.out.println("ID again: "+u.getFacebookId());
			// Adding roommate avatars
			ImageView userImage = new ImageView(rootView.getContext());
			UrlImageViewHelper.setUrlDrawable(userImage, "http://graph.facebook.com/"+u.getFacebookId()+"/picture?type=large", R.drawable.image_user, 3600000);
//			userImage.setImageDrawable(getResources().getDrawable(R.drawable.av1));
			userImage.setAdjustViewBounds(true);
//			userImage.setMaxHeight(200);
//			userImage.setMaxWidth(200);
			
			bottom.addView(userImage, i);
			i++;
		}

		listView.addHeaderView(rlMain);
		newsList = new ArrayList<News>();
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

		if (progressDialog != null && !progressDialog.isShowing() && showProgress )
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

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {

			@Override public void run() {
				showProgress = false;
				swipeRefresh.setRefreshing(false);
			}
		}, 2000);
		DatabaseHelper.getNewsByFlat(((MainActivity)getActivity()).getMyFlat());
	}
	

	public class NewsAdapter extends ArrayAdapter<News> {

		public class ViewHolder {
			public CircularImageView	photo;
			public TextView				comment;
			public TextView				date;
			public TextView             sender;
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

//			Log.i("PARSE", "" + news);
//			Log.i("PARSE", ""+news.getUser());
//			Log.i("PARSE", ""+news.getUser().getName());
//			viewHolder.sender.setText("From "+news.getUser().getName());
			viewHolder.comment.setText("\""+news.getComment()+"\"");
			viewHolder.date.setText(news.getDate());
//			viewHolder.photo.setImageDrawable(drawable)
//			MyUser user = (MyUser) news.get(News.USER);
			UrlImageViewHelper.setUrlDrawable(viewHolder.photo, "http://graph.facebook.com/"+news.getUser().getFacebookId()+"/picture?type=large", R.drawable.image_user, 3600000);


			return convertView;
		}
	}
}