package dk.aau.mpp_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import dk.aau.mpp_project.filter.CircleImageView;
import dk.aau.mpp_project.filter.Filter;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.MyUser;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.widget.CircularImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements FragmentEventHandler,
		SwipeRefreshLayout.OnRefreshListener {

	public String				name			= "Home";
	private ProgressDialog		progressDialog;
	private ArrayList<News>		newsList;

	private ListView			listView;
	private NewsAdapter			adapter;
	private PhotoAdapter		photoAdapter;
	private SwipeRefreshLayout	swipeRefresh;
	private boolean				showProgress	= true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.fragment_section_home, container, false);
		swipeRefresh = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipe_container);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

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

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		// llMain.measure(width, 500);
		// llMain.setBackgroundResource(R.drawable.flat1small);
		if (flat != null) {
			if (flat.getPhoto() != null) {
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Config.RGB_565;
					options.inSampleSize = 2;

					Bitmap b = BitmapFactory.decodeByteArray(flat.getPhoto()
							.getData(), 0, flat.getPhoto().getData().length,
							options);

					b = Filter.rescale(b, width, false);
					if (b.getHeight() > 400)
						b = Bitmap.createBitmap(b, 0, b.getHeight() - 400,
								b.getWidth(), 400);
					llMain.setBackgroundDrawable(new BitmapDrawable(
							getResources(), Filter.fastblur(b, 20)));
					// viewHolder.flatImage.setBackgroundDrawable(new
					// BitmapDrawable(getResources(),
					// Bitmap.createScaledBitmap(b, 1000, 100, true)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				llMain.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.flat1small));
			}
		}

		TextView titleFlat = (TextView) rlMain.findViewById(R.id.titleFlat);
		titleFlat.setText(flat.getName());
		titleFlat.setTextColor(Color.WHITE);
		titleFlat.setTypeface(null, Typeface.BOLD);
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

		System.out.println("number of users " + flat.getFlatUsers().size());
		int i = 0;
		for (final MyUser u : flat.getFlatUsers()) {
			System.out.println("ID again: " + u.getFacebookId());
			// Adding roommate avatars

			CircleImageView userImage = new CircleImageView(
					rootView.getContext());
			Resources r = getResources();
			int px = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
			Drawable d = new ScaleDrawable(getResources().getDrawable(
					R.drawable.image_user), 0, 50, 50).getDrawable();
			UrlImageViewHelper.setUrlDrawable(userImage,
					"http://graph.facebook.com/" + u.getFacebookId()
							+ "/picture?width=" + px + "&height=" + px, d,
					3600000);
			userImage.setBorderWidth(3);
			userImage.setBorderColor(Color.WHITE);
			userImage.setPadding(10, 10, 10, 10);
			
			MainActivity.getUserDetails(u).setPhotoView(userImage);
			
			userImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = "https://www.facebook.com/"
							+ u.getFacebookId();
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});

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
			
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					showProgress = false;
					swipeRefresh.setRefreshing(false);
				}
			});

			progressDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					showProgress = false;
					swipeRefresh.setRefreshing(false);
				}
			});
		}

		if (progressDialog != null && !progressDialog.isShowing()
				&& showProgress)
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
				Collections.reverse(tmp);
				newsList.clear();
				newsList.addAll(tmp);

				adapter.notifyDataSetChanged();

				showProgress = false;
				swipeRefresh.setRefreshing(false);

			} else if (DatabaseHelper.ACTION_GET_OPERATIONS_FLATS.equals(e
					.getAction())) {

			}
		}
	}

	@Override
	public void onRefresh() {

		DatabaseHelper
				.getNewsByFlat(((MainActivity) getActivity()).getMyFlat());
	}

	public class PhotoAdapter extends ArrayAdapter<MyUser> {

		public class ViewHolder {
			public CircularImageView	photo;
		}

		private Context	context;
		private int		resource;

		public PhotoAdapter(Context context, int resource, List<MyUser> objects) {
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
						.findViewById(R.id.user_photo);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			MyUser u = this.getItem(position);

			UrlImageViewHelper.setUrlDrawable(viewHolder.photo,
					"http://graph.facebook.com/" + u.getFacebookId()
							+ "/picture?type=large", R.drawable.image_user,
					3600000);

			return convertView;
		}
	}

	public class NewsAdapter extends ArrayAdapter<News> {

		public class ViewHolder {
			public CircularImageView	photo;
			public TextView				comment;
			public TextView				date;
			public TextView				sender;
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

			viewHolder.comment.setText("\"" + news.getComment() + "\"");
			viewHolder.date.setText(news.getDate());
			viewHolder.photo.addShadow();
			UrlImageViewHelper.setUrlDrawable(viewHolder.photo,
					"http://graph.facebook.com/"
							+ news.getUser().getFacebookId()
							+ "/picture?type=large", R.drawable.image_user,
					3600000);

			return convertView;
		}
	}
}
