package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class MyUser extends ParseUser implements Parcelable {

	public final static String	ID			= "userId";
	public final static String	FACEBOOK_ID	= "facebookId";
	public final static String	EMAIL		= "email";
	public final static String	NAME		= "name";
	public final static String	AGE			= "age";

	private String				facebookId;
	private String				email;
	private String				name;
	private int					age;

	public MyUser() {
	}

	public MyUser(String facebookId, String email, String name, int age) {
		super();

		this.facebookId = facebookId;
		this.email = email;
		this.name = name;
		this.age = age;
		
		setAge(age);
		setEmail(email);
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

	public String getEmail() {
		return getString(EMAIL);
	}

	public void setEmail(String email) {
		this.email = email;
		put(EMAIL, email);
	}

	public String getName() {
		return getString(NAME);
	}

	public void setName(String name) {
		this.name = name;
		put(NAME, name);
	}

	public int getAge() {
		return getInt(AGE);
	}

	public void setAge(int age) {
		this.age = age;
		put(AGE, age);
	}

	public MyUser(Parcel in) {
		this.facebookId = in.readString();
		this.email = in.readString();
		this.name = in.readString();
		this.age = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(facebookId);
		dest.writeString(email);
		dest.writeString(name);
		dest.writeInt(age);
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
}
