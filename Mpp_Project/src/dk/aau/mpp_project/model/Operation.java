package dk.aau.mpp_project.model;

public class Operation {
	private int operationId, lender, flatId, to;
	private String comment;
	private Boolean isPaid;
	
	/* labels of atributs*/
	public static final String OPERATION_ID = "operationId";
	public static final String LENDER = "lender";
	public static final String FLAT_ID = Flat.ID;
	public static final String TO = "to";
	public static final String COMMENT = "comment";
	public static final String ISPAID = "isPaid";
	
	public Operation (int lender, int to, int flatId)
	{
		this.lender = lender;
		this.to = to;
		this.flatId = flatId;
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
	
}
