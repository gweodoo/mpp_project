package dk.aau.mpp_project.database;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.FillingTable;
import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.News;
import dk.aau.mpp_project.model.Operation;
import dk.aau.mpp_project.model.MyUser;

public class DatabaseHelper {

	private static final String	TAG							= "DatabaseHelper";

	public static final String	FLAT						= "Flat";
	public static final String	OPERATION					= "Operation";
	public static final String	NEWS						= "News";
	public static final String	USER						= "MyUser";
	public static final String	FILLING_TABLE				= "FillingTable";

	public static final String	ACTION_GET_USER_FLATS		= "ACTION_GET_USER_FLATS";
	public static final String	ACTION_GET_NEWS_FLATS		= "ACTION_GET_NEWS_FLATS";
	public static final String	ACTION_GET_OPERATIONS_FLATS	= "ACTION_GET_OPERATIONS_FLATS";

	public static void createOperation(Flat flat, Operation operation) {
		// ParseObject operationObject = new ParseObject(OPERATION);

		// operationObject.put(Operation.LENDER, operation.getLender());
		// operationObject.put(Operation.TO, operation.getTo());
		// operationObject.put(Operation.AMOUNT, operation.getAmount());
		// operationObject.put(Operation.COMMENT, operation.getComment());
		// operationObject.put(Operation.IS_PAID, operation.getIsPaid());

		operation.put(Operation.FLAT, flat);

		// operationObject.put(Operation.FLAT, flat);

		operation.saveInBackground();
	}

	public static void createFlat(final MyUser user, final Flat flat) {

		flat.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				joinFlat(user, flat);
			}
		});
	}

	public static void joinFlat(MyUser user, Flat flat) {
		ParseObject object = new ParseObject(FILLING_TABLE);

		object.put(FillingTable.FLAT, flat);
		object.put(FillingTable.USER, user);

		object.saveInBackground();
	}

	public static void createNews(Flat flat, MyUser user, News news) {

		news.saveInBackground();
	}

	public static void getUserFlats(MyUser user) {
		EventBus.getDefault().post(new StartEvent(ACTION_GET_USER_FLATS));

		ParseQuery<ParseObject> query = ParseQuery.getQuery(FILLING_TABLE);

		// Where
		query.whereEqualTo(FillingTable.USER, user);

		// Select
		ArrayList<String> selectedKeys = new ArrayList<String>();
		selectedKeys.add(FillingTable.FLAT);
		query.selectKeys(selectedKeys);

		// Exec
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size()
							+ " flats of user");

					List<String> flatIds = new ArrayList<String>();

					for (ParseObject o : objectList) {
						Flat f = (Flat) o.get(FillingTable.FLAT);

						flatIds.add(f.getObjectId());
						Log.v(TAG, "# My Flat ID : " + f.getObjectId());
					}

					// Bundle extras = new Bundle();
					// extras.putParcelableArrayList("data",
					// (ArrayList<? extends Parcelable>) flats);
					//
					// EventBus.getDefault().post(
					// new FinishedEvent(true, FLAT, extras));

					ParseQuery<Flat> query2 = ParseQuery.getQuery(Flat.class);
					query2.whereContainedIn("objectId", flatIds);

					query2.findInBackground(new FindCallback<Flat>() {
						public void done(List<Flat> objectList, ParseException e) {
							if (e == null) {
								Log.d(TAG, "# Retrieved " + objectList.size()
										+ " flats");

								// ArrayList<Flat> flatsList = new
								// ArrayList<Flat>();
								//
								// for (ParseObject o : objectList) {
								// Flat flat = new Flat(
								// o.getString(Flat.NAME), o
								// .getString(Flat.ADDRESS), o
								// .getString(Flat.ADMIN_ID),
								// o.getDouble(Flat.RENT_AMOUNT));
								//
								// flatsList.add(flat);
								// }

								Bundle extras = new Bundle();
								extras.putParcelableArrayList(
										"data",
										(ArrayList<? extends Parcelable>) objectList);

								EventBus.getDefault().post(
										new FinishedEvent(true, ACTION_GET_USER_FLATS, extras));

							} else {
								Log.d(TAG, "Error: " + e.getMessage());
								EventBus.getDefault().post(
										new FinishedEvent(false, ACTION_GET_USER_FLATS, null));
							}
						}
					});

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_USER_FLATS, null));
				}
			}
		});
	}

	// public static void getFlatById(String flatId) {
	// EventBus.getDefault().post(new StartEvent());
	//
	// ParseQuery<Flat> query = ParseQuery.getQuery(Flat.class);
	//
	// query.whereEqualTo("objectId", flatId);
	//
	// query.getFirstInBackground(new GetCallback<Flat>() {
	// public void done(Flat object, ParseException e) {
	// if (e == null) {
	// // Flat flat = new Flat(object.getString(Flat.NAME), object
	// // .getString(Flat.ADDRESS), object
	// // .getString(Flat.ADMIN_ID), object
	// // .getDouble(Flat.RENT_AMOUNT));
	//
	// Bundle extras = new Bundle();
	// extras.putParcelable("data", object);
	//
	// EventBus.getDefault().post(
	// new FinishedEvent(true, FLAT, extras));
	// } else {
	// Log.d(TAG, "Error: " + e.getMessage());
	// e.printStackTrace();
	// EventBus.getDefault().post(
	// new FinishedEvent(false, FLAT, null));
	// }
	// }
	// });
	// }

	public static void getNewsByFlat(Flat flat) {
		EventBus.getDefault().post(new StartEvent(ACTION_GET_NEWS_FLATS));

		ParseQuery<News> query = ParseQuery.getQuery(News.class);

		query.whereEqualTo(News.FLAT, flat);

		query.findInBackground(new FindCallback<News>() {
			public void done(List<News> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size() + " news");

					// ArrayList<News> newsList = new ArrayList<News>();
					//
					// for (ParseObject o : objectList) {
					// News news = new News((Flat) o.get(News.FLAT), (User) o
					// .get(News.USER), o.getString(News.COMMENT));
					//
					// newsList.add(news);
					// }

					Bundle extras = new Bundle();
					extras.putParcelableArrayList("data",
							(ArrayList<? extends Parcelable>) objectList);

					EventBus.getDefault().post(
							new FinishedEvent(true, ACTION_GET_NEWS_FLATS, extras));

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_NEWS_FLATS, null));
				}
			}
		});
	}

	public static void getOperationsByFlat(Flat flat) {
		EventBus.getDefault().post(new StartEvent(ACTION_GET_OPERATIONS_FLATS));

		ParseQuery<Operation> query = ParseQuery.getQuery(Operation.class);

		query.whereEqualTo(Operation.FLAT, flat);

		query.findInBackground(new FindCallback<Operation>() {
			public void done(List<Operation> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size()
							+ " operations");

					// ArrayList<Operation> operationsList = new
					// ArrayList<Operation>();
					//
					// for (ParseObject o : objectList) {
					// Operation operation = new Operation((Flat) o
					// .get(Operation.FLAT), o
					// .getString(Operation.LENDER), o
					// .getString(Operation.TO), o
					// .getDouble(Operation.AMOUNT), o
					// .getString(Operation.DATE), o
					// .getString(Operation.COMMENT), o
					// .getBoolean(Operation.IS_PAID));
					//
					// operationsList.add(operation);
					// }

					Bundle extras = new Bundle();
					extras.putParcelableArrayList("data",
							(ArrayList<? extends Parcelable>) objectList);

					EventBus.getDefault().post(
							new FinishedEvent(true, ACTION_GET_OPERATIONS_FLATS, extras));

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_OPERATIONS_FLATS, null));
				}
			}
		});
	}
}
