package it.polimi.tiw.messages;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;

import java.util.ArrayList;

public final class OpenAuctionDetailsMessage {

    private final String username;
    private final Auction auction;
    private final ArrayList<Article> auctionArticles;
    private final ArrayList<Offer> auctionOffers;
    private final long difference;

    public OpenAuctionDetailsMessage(String username, Auction auction, ArrayList<Article> auctionArticles, ArrayList<Offer> auctionOffers, long difference) {
        this.username = username;
        this.auction = auction;
        this.auctionArticles = auctionArticles;
        this.auctionOffers = auctionOffers;
        this.difference = difference;
    }

    public String getUsername() {
        return username;
    }

    public Auction getAuction() {
        return auction;
    }

    public ArrayList<Article> getAuctionArticles() {
        return auctionArticles;
    }

    public ArrayList<Offer> getAuctionOffers() {
        return auctionOffers;
    }

    public long getDifference() {
        return difference;
    }
}
