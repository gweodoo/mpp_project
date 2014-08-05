package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable {

	public static final String	ID		= "newsID";
	public static final String	FLAT_ID	= Flat.ID;
	public static final String	USER_ID	= User.ID;
	public static final String	COMMENT	= "comment";

	private int					newsId;
	private int					flatId;
	private int					userId;
	private String				comment;

	public News() {
		super();
	}

	public News(int flatId, int userId, String comment) {
		super();
		this.flatId = flatId;
		this.userId = userId;
		this.comment = comment;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public int getFlatId() {
		return flatId;
	}

	public void setFlatId(int flatId) {
		this.flatId = flatId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public News(Parcel in) {

		this.flatId = in.readInt();
		this.userId = in.readInt();
		this.comment = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.flatId);
		dest.writeInt(this.userId);
		dest.writeString(this.comment);
	}

	public static final Parcelable.Creator<News>	CREATOR	= new Parcelable.Creator<News>() {
														public News createFromParcel(
																Parcel in) {
															return new News(in);
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
