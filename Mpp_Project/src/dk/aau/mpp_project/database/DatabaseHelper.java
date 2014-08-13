package dk.aau.mpp_project.database;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import com.parse.*;

import de.greenrobot.event.EventBus;
import dk.aau.mpp_project.event.FinishedEvent;
import dk.aau.mpp_project.event.StartEvent;
import dk.aau.mpp_project.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

	private static final String TAG = "DatabaseHelper";

	public static final String FLAT = "Flat";
	public static final String OPERATION = "Operation";
	public static final String NEWS = "News";
	public static final String USER = "MyUser";
	public static final String FILLING_TABLE = "FillingTable";

	public static final String ACTION_LOGIN = "ACTION_LOGIN";
	public static final String ACTION_CREATE_OPERATION = "ACTION_CREATE_OPERATION";
	public static final String ACTION_CREATE_FLAT = "ACTION_CREATE_FLAT";
	public static final String ACTION_JOIN_FLAT = "ACTION_JOIN_FLAT";
	public static final String ACTION_UPDATE_FLAT = "ACTION_UPDATE_FLAT";
	public static final String ACTION_GET_USER_FLATS = "ACTION_GET_USER_FLATS";
	public static final String ACTION_GET_FLAT_BY_ID = "ACTION_GET_FLAT_BY_ID";
	public static final String ACTION_GET_NEWS_FLATS = "ACTION_GET_NEWS_FLATS";
	public static final String ACTION_GET_OPERATIONS_FLATS = "ACTION_GET_OPERATIONS_FLATS";
	public static final String ACTION_GET_USERS_IN_FLAT = "ACTION_GET_USERS_IN_FLAT";
	public static final String ACTION_LEAVE_FLAT = "ACTION_LEAVE_FLAT";

	public static ArrayList<MyUser> users;

	public static void createOperation(final Operation operation) {
		// ParseObject operationObject = new ParseObject(OPERATION);
		// operationObject.put(Operation.LENDER, operation.getLender());
		// operationObject.put(Operation.TO, operation.getTo());
		// operationObject.put(Operation.AMOUNT, operation.getAmount());
		// operationObject.put(Operation.COMMENT, operation.getComment());
		// operationObject.put(Operation.IS_PAID, operation.getIsPaid());
		// operationObject.put(Operation.FLAT, flat);

		// operation.put(Operation.FLAT, flat);

		operation.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				Bundle data = new Bundle();
				data.putParcelable("data", operation);
				EventBus.getDefault().post(new FinishedEvent(true, ACTION_CREATE_OPERATION, data));
			}
		});
	}

	public static void createFlat(final MyUser user, final Flat flat,
			final String password) {
		EventBus.getDefault().post(new StartEvent(ACTION_CREATE_FLAT));

		final String pwdCrypted = SHA1(password);

		flat.put(Flat.PASSWORD, pwdCrypted);

		flat.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {

				joinFlat(user, flat, password);
			}
		});
	}

	public static String SHA1(String text) {
		MessageDigest md;
		byte[] sha1hash = null;
		try {
			md = MessageDigest.getInstance("SHA-1");

			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(sha1hash, Base64.DEFAULT);
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
			final String pwdCrypted = SHA1(password);

			flat.put(Flat.PASSWORD, pwdCrypted);
		}

		Log.v(TAG, "# Update Flat ID : " + flat.getObjectId());

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
		System.out.println("joining flat....");
		final String pwdCrypted = SHA1(password);

		final ParseObject object = new ParseObject(FILLING_TABLE);

		object.put(FillingTable.FLAT, flat);
		object.put(FillingTable.USER, user);

		ParseQuery<Flat> query = ParseQuery.getQuery(Flat.class);

		query.whereEqualTo("objectId", flat.getObjectId());

		query.getFirstInBackground(new GetCallback<Flat>() {
			public void done(final Flat flat, ParseException e) {
				if (e == null) {
					if (flat.getString(Flat.PASSWORD).equals(pwdCrypted)) {

						object.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException e) {
								if (e == null) {
									Bundle data = new Bundle();
									data.putParcelable("data", flat);

									EventBus.getDefault().post(
											new FinishedEvent(true,
													ACTION_JOIN_FLAT, data));
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

	public static void createNews(Flat flat, MyUser user, String comment) {

		News news = new News(flat, user, comment);
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
			public void done(final List<News> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size() + " news");

					for (int i = 0; i < objectList.size(); i++) {
						final News n = objectList.get(i);

						final int j = i;
						n.getUser().fetchIfNeededInBackground(
								new GetCallback<MyUser>() {

									@Override
									public void done(MyUser u,
											ParseException arg1) {
										n.setUser(u);
										if (j == objectList.size() - 1) {
											Bundle extras = new Bundle();
											extras.putParcelableArrayList(
													"data",
													(ArrayList<? extends Parcelable>) objectList);

											EventBus.getDefault()
													.post(new FinishedEvent(
															true,
															ACTION_GET_NEWS_FLATS,
															extras));
										}
									}
								});

					}

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

		query.whereEqualTo(FillingTable.FLAT, flat);

		ArrayList<String> selectedKeys = new ArrayList<String>();
		selectedKeys.add(FillingTable.USER);
		query.selectKeys(selectedKeys);

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objectList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "# Retrieved " + objectList.size()
							+ " user for this flat");

					List<String> userIds = new ArrayList<String>();
					// final ArrayList<MyUser> userList = new
					// ArrayList<MyUser>();
					for (ParseObject o : objectList) {
						MyUser u = (MyUser) o.get(FillingTable.USER);

						userIds.add(u.getObjectId());
					}

					ParseQuery<MyUser> query2 = ParseQuery
							.getQuery(MyUser.class);
					query2.whereContainedIn("objectId", userIds);

					query2.findInBackground(new FindCallback<MyUser>() {
						public void done(List<MyUser> objectList,
								ParseException e) {
							if (e == null) {
								Log.d(TAG, "# Retrieved " + objectList.size()
										+ " users");

								Bundle extras = new Bundle();
								extras.putParcelableArrayList(
										"data",
										(ArrayList<? extends Parcelable>) objectList);

								EventBus.getDefault().post(
										new FinishedEvent(true,
												ACTION_GET_USERS_IN_FLAT,
												extras));

							} else {
								Log.d(TAG, "Error: " + e.getMessage());
								EventBus.getDefault()
										.post(new FinishedEvent(false,
												ACTION_GET_USERS_IN_FLAT, null));
							}
						}
					});
				}
			}
		});
	}

	// Log.v(TAG, "# My User ID : " + f.getObjectId());
	// }
	// }
	// });

	// ParseQuery<MyUser> query2 = ParseQuery
	// .getQuery(MyUser.class);
	// query2.whereContainedIn("objectId", userIds);

	// query2.findInBackground(new FindCallback<MyUser>() {
	// public void done(List<MyUser> objectList,
	// ParseException e) {
	// if (e == null) {
	// Log.d(TAG, "# Retrieved " + objectList.size()
	// + " users");
	//
	// Bundle extras = new Bundle();
	// extras.putParcelableArrayList(
	// "data",
	// (ArrayList<? extends Parcelable>) objectList);
	//
	// EventBus.getDefault().post(
	// new FinishedEvent(true,
	// ACTION_GET_USERS_IN_FLAT,
	// extras));
	//
	// } else {
	// Log.d(TAG, "Error: " + e.getMessage());
	// EventBus.getDefault()
	// .post(new FinishedEvent(false,
	// ACTION_GET_USERS_IN_FLAT, null));
	// }
	// }
	// });
	// // }
	// // }
	// // });
	// }

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

	public static void parseSendPush(String channel, String facebookId,
			String msg) throws JSONException {

		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo("channels", channel);
		pushQuery.whereEqualTo("facebookId", facebookId);

		JSONObject data = new JSONObject(
				"{\"action\":\"colloc.action.push\",\"msg\": \"" + msg + "\" }");

		ParsePush push = new ParsePush();
		push.setQuery(pushQuery);
		push.setData(data);
		push.sendInBackground();
	}

	public static void parseSendPushToAll(String channel, String msg)
			throws JSONException {
		MyUser currentUser = (MyUser) ParseUser.getCurrentUser();

		for (MyUser u : users) {

			if (u.getObjectId().equals(currentUser.getObjectId()))
				continue;

			ParseQuery<ParseInstallation> pushQuery = ParseInstallation
					.getQuery();
			pushQuery.whereEqualTo("channels", channel);
			pushQuery.whereEqualTo("facebookId", u.getFacebookId());

			JSONObject data = new JSONObject(
					"{\"action\":\"colloc.action.push\",\"msg\": \"" + msg
							+ "\" }");

			ParsePush push = new ParsePush();
			push.setQuery(pushQuery);
			push.setData(data);
			push.sendInBackground();
		}
	}
}
