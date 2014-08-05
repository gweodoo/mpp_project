package dk.aau.mpp_project.database;

import com.parse.ParseObject;

import dk.aau.mpp_project.model.Flat;
import dk.aau.mpp_project.model.News;

public class DatabaseHelper {

	public static final String	FLAT			= "Flat";
	public static final String	OPERATION		= "Operation";
	public static final String	NEWS			= "News";
	public static final String	USER			= "User";
	public static final String	FILLING_TABLE	= "FillingTable";

	public static void createOperation(int lenderID, int toID, float amount) {
		ParseObject operation = new ParseObject(OPERATION);

		operation.put("score", 1337);

		operation.saveInBackground();
	}

	public static void createFlat(String name, String address,
			float rentAmount, int adminID) {
		ParseObject flat = new ParseObject(FLAT);

		flat.put(Flat.NAME, name);
		flat.put(Flat.ADDRESS, address);
		flat.put(Flat.RENT_AMOUNT, rentAmount);
		flat.put(Flat.ADMIN_ID, adminID);

		flat.saveInBackground();
	}

	public static void createNews(int flatID, int userID, int operationID,
			String comment) {
		ParseObject news = new ParseObject(NEWS);

		news.put(News.FLAT_ID, flatID);
		news.put(News.USER_ID, userID);
		news.put(News.OPERATION_ID, operationID);
		news.put(News.COMMENT, comment);

		news.saveInBackground();
	}
}
