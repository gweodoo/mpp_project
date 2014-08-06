package dk.aau.mpp_project.model;

/**
 * Created by adamj on 06/08/14.
 */
public class LoanItem {
    private String image;
    private String info;
    private float amount;
    private boolean isPaid;

    public LoanItem(String image, String info, float amount, boolean isPaid){
        this.image = image;
        this.info = info;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public String getImage() {
        return image;
    }

    public String getInfo() {
        return info;
    }

    public float getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
