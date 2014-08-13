package dk.aau.mpp_project.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("Operation")
public class Operation extends ParseObject implements Parcelable {

	public static final String	ID		= "operationId";
	public static final String	LENDER	= "lender";
	public static final String	FLAT	= "flat";
	public static final String	TO		= "to";
	public static final String	COMMENT	= "comment";
	public static final String	IS_PAID	= "isPaid";
	public static final String	AMOUNT	= "amount";
	public static final String	DATE	= "createdAt";

	private Flat				flat;
	private MyUser				lender;
	private MyUser				to;
	private String				comment;
	private boolean				isPaid;
	private double				amount;
	private String				date;

	public Operation() {
	}

	public Operation(Flat flat, MyUser lender, MyUser to, double amount,
			String date, String comment, boolean isPaid) {

		this.flat = flat;
		this.lender = lender;
		this.to = to;
		this.amount = amount;
		this.isPaid = isPaid;
		this.comment = comment;
		this.date = date;
		if (this.date.equals(""))
			this.date = (new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date()));

		setComment(comment);
		//setDate(date);
		setFlat(flat);
		setIsPaid(isPaid);
		setLender(lender);
		setTo(to);
		setAmount(amount);
	}

	public MyUser getLender() {
		return (MyUser) get(LENDER);
	}

	public void setLender(MyUser lender) {
		this.lender = lender;
		put(LENDER, lender);
	}

	public String getDate() {
		return getString(DATE);
	}

	public void setDate(String date) {
		this.date = date;
		put(DATE, this.date);
	}

	public Flat getFlat() {

		return (Flat) get(FLAT);
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
		put(FLAT, flat);
	}

	public MyUser getTo() {
		return (MyUser) get(TO);
	}

	public void setTo(MyUser to) {
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
		this.lender = in.readParcelable(MyUser.class.getClassLoader());
		this.to = in.readParcelable(MyUser.class.getClassLoader());
		this.amount = in.readDouble();
		this.isPaid = in.readByte() != 0;
		this.comment = in.readString();
		this.date = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(flat, flags);
		dest.writeParcelable(lender, flags);
		dest.writeParcelable(to, flags);
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
