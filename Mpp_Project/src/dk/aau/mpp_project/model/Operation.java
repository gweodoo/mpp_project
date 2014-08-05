package dk.aau.mpp_project.model;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Operation implements Parcelable {
	private int					operationId;
	private int					flatId;
	private int					lender;
	private int					to;
	private String				comment;
	private Boolean				isPaid;
	private double				amount;
	private String				date;

	/* labels of atributs */
	public static final String	OPERATION_ID	= "operationId";
	public static final String	LENDER			= "lender";
	public static final String	FLAT_ID			= Flat.ID;
	public static final String	TO				= "to";
	public static final String	COMMENT			= "comment";
	public static final String	IS_PAID			= "isPaid";
	public static final String	AMOUNT			= "amount";
	public static final String	DATE			= "date";

	public Operation(int flatId, int lender, int to, double amount,
			String createdAt, String comment) {
		this.flatId = flatId;
		this.lender = lender;
		this.to = to;
		this.amount = amount;
		this.isPaid = false;
		this.comment = comment;

		Calendar cal = Calendar.getInstance();

		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String month = Integer.toString(cal.get(Calendar.MONTH));
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(cal.get(Calendar.MINUTE));

		this.date = day + "/" + month + "/" + year + ", " + hour + ":" + minute;
	}

	public Operation() {
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}

	public int getLender() {
		return lender;
	}

	public void setLender(int lender) {
		this.lender = lender;
	}

	public int getFlatId() {
		return flatId;
	}

	public void setFlatId(int flatId) {
		this.flatId = flatId;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Operation(Parcel in) {
		this.flatId = in.readInt();
		this.lender = in.readInt();
		this.to = in.readInt();
		this.amount = in.readDouble();
		this.isPaid = in.readByte() != 0;
		this.comment = in.readString();
		this.date = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(flatId);
		dest.writeInt(lender);
		dest.writeInt(to);
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
