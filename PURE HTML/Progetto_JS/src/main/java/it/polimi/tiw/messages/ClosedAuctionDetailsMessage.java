package it.polimi.tiw.messages;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;

import java.util.ArrayList;

public final class ClosedAuctionDetailsMessage {
    private final String username;
    private final Auction auction;
    private final ArrayList<Article> auctionArticle;
    private final User user;

    public ClosedAuctionDetailsMessage(String username, Auction auction, ArrayList<Article> auctionArticle, User user) {
        this.username = username;
        this.auction = auction;
        this.auctionArticle = auctionArticle;
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public Auction getAuction() {
        return auction;
    }

    public ArrayList<Article> getAuctionArticle() {
        return auctionArticle;
    }

    public User getUser() {
        return user;
    }
}
