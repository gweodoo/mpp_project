package dk.aau.mpp_project.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("News")
public class News extends ParseObject implements Parcelable {

	public static final String	ID		= "newsId";
	public static final String	USER	= "user";
	public static final String	FLAT	= "flat";
	public static final String	COMMENT	= "comment";
	private static final String	DATE	= "createdAt";

	private Flat				flat;
	private MyUser				user;
	private String				comment;

	public News() {
	}

	@SuppressLint("SimpleDateFormat")
	public News(Flat flat, MyUser user, String comment) {
		super();
		this.flat = flat;
		this.user = user;
		this.comment = comment;

		setComment(comment);
		setFlat(flat);
		setUser(user);

	}

	public String getDate() {
		Date date = getCreatedAt();

		String dateStr = new SimpleDateFormat("dd-MM-yy HH:mm").format(date);
		return dateStr;
	}

	public Flat getFlat() {
		return (Flat) get(FLAT);
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
		put(FLAT, flat);
	}

	public MyUser getUser() {
		return (MyUser) get(USER);
	}

	public void setUser(MyUser user) {
		this.user = user;
		put(USER, user);
	}

	public String getComment() {
		String data = getString(COMMENT);
		return data;
	}

	public void setComment(String comment) {
		this.comment = comment;
		put(COMMENT, comment);
	}

	public News(Parcel in) {
		this.flat = in.readParcelable(Flat.class.getClassLoader());
		this.user = in.readParcelable(MyUser.class.getClassLoader());
		this.comment = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.flat, flags);
		dest.writeParcelable(this.user, flags);
		dest.writeString(this.comment);
	}

	public static final Parcelable.Creator<News>	CREATOR	= new Parcelable.Creator<News>() {
																public News createFromParcel(
																		Parcel in) {
																	return new News(
																			in);
																}

																public News[] newArray(
																		int size) {
																	return new News[size];
																}
															};

	@Override
	public int describeContents() {
		return 0;
	}
}
