package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	public final static String	ID			= "userID";
	public final static String	FACEBOOK_ID	= "facebookId";
	public final static String	EMAIL		= "email";
	public final static String	LASTNAME	= "lastname";
	public final static String	FIRSTNAME	= "firstname";
	public final static String	AGE			= "age";

	private int					userId;
	private long				facebookId;
	private String				email;
	private String				lastname;
	private String				firstname;
	private int					age;

	public User() {
	}

	public User(long facebookId, String email, String lastname,
			String firstname, int age) {
		super();
		this.facebookId = facebookId;
		this.email = email;
		this.lastname = lastname;
		this.firstname = firstname;
		this.age = age;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(long facebookId) {
		this.facebookId = facebookId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public User(Parcel in) {
		this.facebookId = in.readLong();
		this.email = in.readString();
		this.lastname = in.readString();
		this.firstname = in.readString();
		this.age = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(facebookId);
		dest.writeString(email);
		dest.writeString(lastname);
		dest.writeString(firstname);
		dest.writeInt(age);
	}

	public static final Parcelable.Creator<User>	CREATOR	= new Parcelable.Creator<User>() {
																public User createFromParcel(
																		Parcel in) {
																	return new User(
																			in);
																}

																public User[] newArray(
																		int size) {
																	return new User[size];
																}
															};

	@Override
	public int describeContents() {
		return 0;
	}
}
