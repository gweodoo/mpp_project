package dk.aau.mpp_project.database;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.model.Operation;
import dk.aau.mpp_project.model.User;

public class DatabaseHelper {

	private static final String	TAG				= "DatabaseHelper";

	public static final String	FLAT			= "Flat";
	public static final String	OPERATION		= "Operation";
	public static final String	NEWS			= "News";
	public static final String	USER			= "User";
	public static final String	FILLING_TABLE	= "FillingTable";

	public static void createOperation(Operation operation) {
		ParseObject operationObject = new ParseObject(OPERATION);

		operationObject.put(Operation.FLAT_ID, operation.getFlatId());
		operationObject.put(Operation.LENDER, operation.getLender());
		operationObject.put(Operation.TO, operation.getTo());
		operationObject.put(Operation.AMOUNT, operation.getAmount());
		operationObject.put(Operation.COMMENT, operation.getComment());
		operationObject.put(Operation.IS_PAID, operation.getIsPaid());

		operationObject.saveInBackground();
	}

	public static void createFlat(Flat flat) {
		ParseObject flatObject = new ParseObject(FLAT);

		flatObject.put(Flat.NAME, flat.getName());
		flatObject.put(Flat.ADDRESS, flat.getAddress());
		flatObject.put(Flat.RENT_AMOUNT, flat.getRentAmount());
		flatObject.put(Flat.ADMIN_ID, flat.getAdminId());

		flatObject.saveInBackground();
	}

	public static void createNews(News news) {
		ParseObject newsObject = new ParseObject(NEWS);

		newsObject.put(News.FLAT_ID, news.getFlatId());
		newsObject.put(News.USER_ID, news.getUserId());
		newsObject.put(News.COMMENT, news.getComment());

		newsObject.saveInBackground();
	}

	public static void getUserFlats(int userId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(FILLING_TABLE);

		// Where
		query.whereEqualTo(User.ID, userId);

		// Select
		ArrayList<String> selectedKeys = new ArrayList<String>();
		selectedKeys.add(Flat.ID);
		query.selectKeys(selectedKeys);

		// Exec
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size()
							+ " flats of user");

					ParseQuery<ParseObject> query2 = ParseQuery.getQuery(FLAT);
					query2.whereContainedIn(Flat.ID, objectList);

					query2.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> objectList,
								ParseException e) {
							if (e == null) {
								Log.d(TAG, "# Retrieved " + objectList.size()
										+ " news");

								ArrayList<Flat> flatsList = new ArrayList<Flat>();

								flatsList.clear();

								for (ParseObject o : objectList) {
									Flat flat = new Flat(
											o.getString(Flat.NAME), o
													.getString(Flat.ADDRESS), o
													.getInt(Flat.ADMIN_ID),
											o.getDouble(Flat.RENT_AMOUNT));

									flatsList.add(flat);
								}

								Bundle extras = new Bundle();
								extras.putParcelableArrayList("data", flatsList);

								EventBus.getDefault().post(
										new FinishedEvent(true, extras));

							} else {
								Log.d(TAG, "Error: " + e.getMessage());
								EventBus.getDefault().post(
										new FinishedEvent(false, null));
							}
						}
					});

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					EventBus.getDefault().post(new FinishedEvent(false, null));
				}
			}
		});
	}

	public static void getFlatById(int flatId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(FLAT);

		query.whereEqualTo(Flat.ID, flatId);

		query.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					Flat flat = new Flat(object.getString(Flat.NAME), object
							.getString(Flat.ADDRESS), object
							.getInt(Flat.ADMIN_ID), object
							.getDouble(Flat.RENT_AMOUNT));

					Bundle extras = new Bundle();
					extras.putParcelable("data", flat);

					EventBus.getDefault().post(new FinishedEvent(true, extras));
				} else {
					// something went wrong
					EventBus.getDefault().post(new FinishedEvent(false, null));
				}
			}
		});
	}

	public static void getNewsByFlatId(int flatId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(NEWS);

		query.whereEqualTo(News.FLAT_ID, flatId);

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size() + " news");

					ArrayList<News> newsList = new ArrayList<News>();

					newsList.clear();

					for (ParseObject o : objectList) {
						News news = new News(o.getInt(News.FLAT_ID), o
								.getInt(News.USER_ID), o
								.getString(News.COMMENT));

						newsList.add(news);
					}

					Bundle extras = new Bundle();
					extras.putParcelableArrayList("data", newsList);

					EventBus.getDefault().post(new FinishedEvent(true, extras));

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					EventBus.getDefault().post(new FinishedEvent(false, null));
				}
			}
		});
	}
}
