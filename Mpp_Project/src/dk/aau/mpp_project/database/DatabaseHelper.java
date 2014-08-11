package dk.aau.mpp_project.database;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.parse.*;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

	private static final String	TAG							= "DatabaseHelper";

	public static final String	FLAT						= "Flat";
	public static final String	OPERATION					= "Operation";
	public static final String	NEWS						= "News";
	public static final String	USER						= "MyUser";
	public static final String	FILLING_TABLE				= "FillingTable";

	public static final String	ACTION_LOGIN				= "ACTION_LOGIN";
	public static final String	ACTION_CREATE_FLAT			= "ACTION_CREATE_FLAT";
	public static final String	ACTION_JOIN_FLAT			= "ACTION_JOIN_FLAT";
	public static final String	ACTION_UPDATE_FLAT			= "ACTION_UPDATE_FLAT";
	public static final String	ACTION_GET_USER_FLATS		= "ACTION_GET_USER_FLATS";
	public static final String	ACTION_GET_FLAT_BY_ID		= "ACTION_GET_FLAT_BY_ID";
	public static final String	ACTION_GET_NEWS_FLATS		= "ACTION_GET_NEWS_FLATS";
	public static final String	ACTION_GET_OPERATIONS_FLATS	= "ACTION_GET_OPERATIONS_FLATS";
    public static final String	ACTION_GET_USERS_IN_FLAT	= "ACTION_GET_USERS_IN_FLAT";
	public static final String	ACTION_LEAVE_FLAT			= "ACTION_LEAVE_FLAT";

	public static void createOperation(Flat flat, Operation operation) {
		// ParseObject operationObject = new ParseObject(OPERATION);
		// operationObject.put(Operation.LENDER, operation.getLender());
		// operationObject.put(Operation.TO, operation.getTo());
		// operationObject.put(Operation.AMOUNT, operation.getAmount());
		// operationObject.put(Operation.COMMENT, operation.getComment());
		// operationObject.put(Operation.IS_PAID, operation.getIsPaid());
		// operationObject.put(Operation.FLAT, flat);

		operation.put(Operation.FLAT, flat);

		operation.saveInBackground();
	}

	public static void createFlat(final MyUser user, final Flat flat,
			final String password) {
		EventBus.getDefault().post(new StartEvent(ACTION_CREATE_FLAT));

		flat.put(Flat.PASSWORD, password);

		flat.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				joinFlat(user, flat, password);
			}
		});
	}

	/**
	 * 
	 * @param flat
	 *            to update to the databse
	 * @param password
	 *            (optionnal) : can be null if don't want to update the password
	 */
	public static void updateFlat(Flat flat, String password) {
		EventBus.getDefault().post(new StartEvent(ACTION_UPDATE_FLAT));

		if (password != null) {
			flat.put(Flat.PASSWORD, password);
		}

		flat.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					EventBus.getDefault().post(
							new FinishedEvent(true, ACTION_UPDATE_FLAT, null));
				} else {
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_UPDATE_FLAT, null));
				}
			}
		});
	}

	public static void leaveFlat(MyUser user, Flat flat) {
		EventBus.getDefault().post(new StartEvent(ACTION_LEAVE_FLAT));

		ParseQuery<ParseObject> query = ParseQuery.getQuery(FILLING_TABLE);

		query.whereEqualTo(FillingTable.USER, user);
		query.whereEqualTo(FillingTable.FLAT, flat);

		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject o, ParseException ex) {
				o.deleteInBackground(new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							EventBus.getDefault().post(
									new FinishedEvent(true, ACTION_LEAVE_FLAT,
											null));
						} else {
							EventBus.getDefault().post(
									new FinishedEvent(false, ACTION_LEAVE_FLAT,
											null));
						}
					}
				});
			}
		});
	}

	public static void joinFlat(MyUser user, Flat flat, final String password) {
		EventBus.getDefault().post(new StartEvent(ACTION_JOIN_FLAT));

		final ParseObject object = new ParseObject(FILLING_TABLE);

		object.put(FillingTable.FLAT, flat);
		object.put(FillingTable.USER, user);

		ParseQuery<Flat> query = ParseQuery.getQuery(Flat.class);

		query.whereEqualTo("objectId", flat.getObjectId());

		query.getFirstInBackground(new GetCallback<Flat>() {
			public void done(Flat flat, ParseException e) {
				if (e == null) {
					if (flat.getString(Flat.PASSWORD).equals(password)) {

						object.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException e) {
								if (e == null) {
									EventBus.getDefault().post(
											new FinishedEvent(true,
													ACTION_JOIN_FLAT, null));
								} else {
									EventBus.getDefault().post(
											new FinishedEvent(false,
													ACTION_JOIN_FLAT, null));
								}
							}
						});
					} else {
						EventBus.getDefault()
								.post(new FinishedEvent(false,
										ACTION_JOIN_FLAT, null));
					}
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_JOIN_FLAT, null));
				}
			}
		});

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

					ParseQuery<Flat> query2 = ParseQuery.getQuery(Flat.class);
					query2.whereContainedIn("objectId", flatIds);

					query2.findInBackground(new FindCallback<Flat>() {
						public void done(List<Flat> objectList, ParseException e) {
							if (e == null) {
								Log.d(TAG, "# Retrieved " + objectList.size()
										+ " flats");

								Bundle extras = new Bundle();
								extras.putParcelableArrayList(
										"data",
										(ArrayList<? extends Parcelable>) objectList);

								EventBus.getDefault().post(
										new FinishedEvent(true,
												ACTION_GET_USER_FLATS, extras));

							} else {
								Log.d(TAG, "Error: " + e.getMessage());
								EventBus.getDefault().post(
										new FinishedEvent(false,
												ACTION_GET_USER_FLATS, null));
							}
						}
					});

				} else {
					Log.d(TAG, "Error: " + e.getMessage() + " : " + e.getCode()
							+ " => " + ParseException.OBJECT_NOT_FOUND);
					e.printStackTrace();

					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_USER_FLATS,
									null));
				}
			}
		});
	}

	public static void getFlatById(String id) {
		EventBus.getDefault().post(new StartEvent(ACTION_GET_FLAT_BY_ID));

		ParseQuery<Flat> query = ParseQuery.getQuery(Flat.class);

		query.whereEqualTo("objectId", id);

		query.getFirstInBackground(new GetCallback<Flat>() {
			public void done(Flat object, ParseException e) {
				if (e == null) {
					Bundle extras = new Bundle();
					extras.putParcelable("data", object);

					EventBus.getDefault().post(
							new FinishedEvent(true, ACTION_GET_FLAT_BY_ID,
									extras));
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_FLAT_BY_ID,
									null));
				}
			}
		});
	}

	public static void getNewsByFlat(Flat flat) {
		EventBus.getDefault().post(new StartEvent(ACTION_GET_NEWS_FLATS));

		ParseQuery<News> query = ParseQuery.getQuery(News.class);

		query.whereEqualTo(News.FLAT, flat);

		query.findInBackground(new FindCallback<News>() {
			public void done(List<News> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size() + " news");

					Bundle extras = new Bundle();
					extras.putParcelableArrayList("data",
							(ArrayList<? extends Parcelable>) objectList);

					EventBus.getDefault().post(
							new FinishedEvent(true, ACTION_GET_NEWS_FLATS,
									extras));

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false, ACTION_GET_NEWS_FLATS,
									null));
				}
			}
		});
	}

    public static void getUsersByFlat(Flat flat) {
        EventBus.getDefault().post(new StartEvent(ACTION_GET_USERS_IN_FLAT));

        ParseQuery<ParseObject> query = ParseQuery.getQuery(FILLING_TABLE);

        query.whereEqualTo(MyUser.FLAT, flat);

        ArrayList<String> selectedKeys = new ArrayList<String>();
        selectedKeys.add(FillingTable.USER);
        query.selectKeys(selectedKeys);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "# Retrieved " + objectList.size()
                            + " user for this flat");

                    List<String> userIds = new ArrayList<String>();

                    for (ParseObject o : objectList) {
                        MyUser f = (MyUser) o.get(FillingTable.USER);

                        userIds.add(f.getObjectId());
                        Log.v(TAG, "# My User ID : " + f.getObjectId());
                    }

                    ParseQuery<MyUser> query2 = ParseQuery.getQuery(MyUser.class);
                    query2.whereContainedIn("objectId", userIds);

                    query2.findInBackground(new FindCallback<MyUser>() {
                        public void done(List<MyUser> objectList, ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "# Retrieved " + objectList.size()
                                        + " users");

                                Bundle extras = new Bundle();
                                extras.putParcelableArrayList(
                                        "data",
                                        (ArrayList<? extends Parcelable>) objectList);

                                EventBus.getDefault().post(
                                        new FinishedEvent(true,
                                                ACTION_GET_USERS_IN_FLAT, extras)
                                );

                            } else {
                                Log.d(TAG, "Error: " + e.getMessage());
                                EventBus.getDefault().post(
                                        new FinishedEvent(false,
                                                ACTION_GET_USERS_IN_FLAT, null)
                                );
                            }
                        }
                    });
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

					Bundle extras = new Bundle();
					extras.putParcelableArrayList("data",
							(ArrayList<? extends Parcelable>) objectList);

					EventBus.getDefault().post(
							new FinishedEvent(true,
									ACTION_GET_OPERATIONS_FLATS, extras));

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
					e.printStackTrace();
					EventBus.getDefault().post(
							new FinishedEvent(false,
									ACTION_GET_OPERATIONS_FLATS, null));
				}
			}
		});
	}
}
