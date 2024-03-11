package it.polimi.tiw.messages;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;

import java.util.ArrayList;

public final class ViewOffersMessage {

    private final Auction auction;
    private final ArrayList<Offer> auctionOffers;
    private final String username;
    private final boolean done;
    private final Offer offer;

    public ViewOffersMessage(Auction auction, ArrayList<Offer> auctionOffers, String username, boolean done, Offer offer){
        
        this.auction = auction;
        this.auctionOffers = auctionOffers;
        this.username = username;
        this.done = done;
        this.offer = offer;
    }

    public Offer getOffer(){
    	return offer;
    }
    
    public Auction getAuction() {
        return auction;
    }

    public ArrayList<Offer> getAuctionOffers() {
        return auctionOffers;
    }

    public String getUsername() {
        return username;
    }

    public boolean isDone() {
        return done;
    }
}
