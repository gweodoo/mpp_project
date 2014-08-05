package dk.aau.mpp_project.model;

public class Operation {
	private int operationId, lender, flatId, to;
	private String comment;
	private Boolean isPaid;
	
	/* labels of atributs*/
	public static final String OPERATION_ID = "operationId";
	public static final String LENDER = "lender";
	public static final String FLAT_ID = "flatId";
	public static final String TO = "to";
	public static final String COMMENT = "comment";
	public static final String ISPAID = "isPaid";
	
	public Operation (int lender, int to, int flatId)
	{
		this.lender = lender;
		this.to = to;
		this.flatId = flatId;
	}
}
