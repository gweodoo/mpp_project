package dk.aau.mpp_project.model;

import java.util.Calendar;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import android.os.Parcel;
import android.os.Parcelable;

@ParseClassName("Operation")
public class Operation extends ParseObject implements Parcelable {

	public static final String	ID		= "operationId";
	public static final String	LENDER	= "lender";
	public static final String	FLAT	= "flat";
	public static final String	TO		= "to";
	public static final String	COMMENT	= "comment";
	public static final String	IS_PAID	= "isPaid";
	public static final String	AMOUNT	= "amount";
	public static final String	DATE	= "date";

	private Flat				flat;
	private String				lender;
	private String				to;
	private String				comment;
	private Boolean				isPaid;
	private double				amount;
	private String				date;

	public Operation() {
	}

	public Operation(Flat flat, String lender, String to, double amount,
			String createdAt, String comment, boolean isPaid) {

		this.flat = flat;
		this.lender = lender;
		this.to = to;
		this.amount = amount;
		this.isPaid = isPaid;
		this.comment = comment;

		Calendar cal = Calendar.getInstance();

		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String month = Integer.toString(cal.get(Calendar.MONTH));
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(cal.get(Calendar.MINUTE));

		this.date = day + "/" + month + "/" + year + ", " + hour + ":" + minute;
	}

	public String getLender() {
		return getString(LENDER);
	}

	public void setLender(String lender) {
		this.lender = lender;
		put(LENDER, lender);
	}

	public String getDate() {
		return getString(DATE);
	}

	public void setDate(String date) {
		this.date = date;
		put(DATE, lender);
	}

	public Flat getFlat() {
		return (Flat) get(FLAT);
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
		put(FLAT, flat);
	}

	public String getTo() {
		return getString(TO);
	}

	public void setTo(String to) {
		this.to = to;
		put(TO, to);
	}

	public String getComment() {
		return getString(COMMENT);
	}

	public void setComment(String comment) {
		this.comment = comment;
		put(COMMENT, comment);
	}

	public Boolean getIsPaid() {
		return getBoolean(IS_PAID);
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
		put(IS_PAID, isPaid);
	}

	public double getAmount() {
		return getDouble(AMOUNT);
	}

	public void setAmount(double amount) {
		this.amount = amount;
		put(AMOUNT, amount);
	}

	public Operation(Parcel in) {
		this.flat = in.readParcelable(Flat.class.getClassLoader());
		this.lender = in.readString();
		this.to = in.readString();
		this.amount = in.readDouble();
		this.isPaid = in.readByte() != 0;
		this.comment = in.readString();
		this.date = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(flat, flags);
		dest.writeString(lender);
		dest.writeString(to);
		dest.writeDouble(amount);
		dest.writeByte((byte) (isPaid ? 1 : 0));
		dest.writeString(comment);
		dest.writeString(date);
	}

	public static final Parcelable.Creator<Operation>	CREATOR	= new Parcelable.Creator<Operation>() {
																	public Operation createFromParcel(
																			Parcel in) {
																		return new Operation(
																				in);
																	}

																	public Operation[] newArray(
																			int size) {
																		return new Operation[size];
																	}
																};

	@Override
	public int describeContents() {
		return 0;
	}
}
