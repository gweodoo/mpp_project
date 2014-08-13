package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class MyUser extends ParseUser implements Parcelable {

	public final static String	ID			= "userId";
	public final static String	FACEBOOK_ID	= "facebookId";
	public final static String	BIRTHDAY	= "birthday";
	public final static String	NAME		= "name";
    public final static String  FLAT        = "flat";
	public final static String  PHOTO       = "picture";
	
	private ImageView			photoView;

	private String				facebookId;
	private String				birthday;
	private String				name;

	public MyUser() {
	}

	public MyUser(String facebookId, String name, String birthday) {
		super();

		this.facebookId = facebookId;
		this.name = name;
		this.birthday = birthday;
		setBirthday(birthday);
		setFacebookId(facebookId);
		setName(name);
	}

	public String getFacebookId() {
		return getString(FACEBOOK_ID);
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
		put(FACEBOOK_ID, facebookId);
	}

	public String getBirthday() {
		return getString(BIRTHDAY);
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
		put(BIRTHDAY, birthday);
	}

	public String getName() {
		return getString(NAME);
	}

	public void setName(String name) {
		this.name = name;
		put(NAME, name);
	}

	// public int getAge() {
	// return getInt(AGE);
	// }
	//
	// public void setAge(int age) {
	// this.age = age;
	// put(AGE, age);
	// }

	public MyUser(Parcel in) {
		this.facebookId = in.readString();
		this.name = in.readString();
		this.birthday = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(facebookId);
		dest.writeString(name);
		dest.writeString(birthday);
	}

	public static final Parcelable.Creator<MyUser>	CREATOR	= new Parcelable.Creator<MyUser>() {
																public MyUser createFromParcel(
																		Parcel in) {
																	return new MyUser(
																			in);
																}

																public MyUser[] newArray(
																		int size) {
																	return new MyUser[size];
																}
															};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		MyUser myUser = (MyUser) o;

		if (getObjectId() != null ? !getObjectId().equals(myUser.getObjectId()) : myUser.getObjectId() != null) return false;

		return true;
	}
	
    public ImageView getPhotoView() {
		return photoView;
	}

	public void setPhotoView(ImageView photoView) {
		this.photoView = photoView;
	}
}
