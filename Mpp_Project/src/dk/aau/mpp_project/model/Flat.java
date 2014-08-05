package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Flat implements Parcelable {
	public static final String	ID			= "flatID";
	public static final String	NAME		= "name";
	public static final String	ADDRESS		= "address";
	public static final String	ADMIN_ID	= "adminID";
	public static final String	RENT_AMOUNT	= "rentAmount";

	private int					flatId;
	private String				name;
	private String				address;
	private int					adminId;
	private double				rentAmount;

	public Flat() {
	}

	public Flat(String name, String address, int adminId, double rent) {
		super();
		this.name = name;
		this.address = address;
		this.adminId = adminId;
		this.rentAmount = rent;
	}

	public int getFlatId() {
		return flatId;
	}

	public void setFlatId(int flatId) {
		this.flatId = flatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public double getRentAmount() {
		return rentAmount;
	}

	public void setRentAmount(double rentAmount) {
		this.rentAmount = rentAmount;
	}

	public Flat(Parcel in) {
		this.name = in.readString();
		this.address = in.readString();
		this.adminId = in.readInt();
		this.rentAmount = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.address);
		dest.writeInt(this.adminId);
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
