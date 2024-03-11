package it.polimi.tiw.beans;
import java.sql.Timestamp;

public class Offer {
    private int offerPrice;
    private String bidder;
    private Timestamp dateTime;
    private int auction;

    public int getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(int offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public int getAuction() {
        return auction;
    }

    public void setAuction(int auction) {
        this.auction = auction;
    }
}
