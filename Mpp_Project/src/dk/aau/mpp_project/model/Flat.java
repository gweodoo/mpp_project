package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Flat")
public class Flat extends ParseObject implements Parcelable {

	public static final String	ID			= "flatId";
	public static final String	NAME		= "name";
	public static final String	ADDRESS		= "address";
	public static final String	ADMIN_ID	= "adminID";
	public static final String	RENT_AMOUNT	= "rentAmount";
	public static final String	PASSWORD	= "password";

	private String				name;
	private String				address;
	private String				adminId;
	private double				rentAmount;

	public Flat() {
	}

	public Flat(String name, String address, String adminId, double rent) {
		super();
		this.name = name;
		this.address = address;
		this.adminId = adminId;
		this.rentAmount = rent;

		setAddress(address);
		setAdminId(adminId);
		setName(name);
		setRentAmount(rentAmount);
	}

	public String getName() {
		return getString(NAME);
	}

	public void setName(String name) {
		// this.name = name;
		put(NAME, name);
	}

	public String getAddress() {
		return getString(ADDRESS);
	}

	public void setAddress(String address) {
		// this.address = address;
		put(ADDRESS, address);
	}

	public String getAdminId() {
		return getString(ADMIN_ID);
	}

	public void setAdminId(String adminId) {
		// this.adminId = adminId;
		put(ADMIN_ID, adminId);
	}

	public double getRentAmount() {
		return getDouble(RENT_AMOUNT);
	}

	public void setRentAmount(double rentAmount) {
		// this.rentAmount = rentAmount;
		put(RENT_AMOUNT, rentAmount);
	}

	public Flat(Parcel in) {
		this.name = in.readString();
		this.address = in.readString();
		this.adminId = in.readString();
		this.rentAmount = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.address);
		dest.writeString(this.adminId);
		dest.writeDouble(this.rentAmount);
	}

	public static final Parcelable.Creator<Flat>	CREATOR	= new Parcelable.Creator<Flat>() {
																public Flat createFromParcel(
																		Parcel in) {
																	return new Flat(
																			in);
																}

																public Flat[] newArray(
																		int size) {
																	return new Flat[size];
																}
															};

	@Override
	public int describeContents() {
		return 0;
	}
}
