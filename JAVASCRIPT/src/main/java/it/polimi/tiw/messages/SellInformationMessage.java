package it.polimi.tiw.messages;

import java.util.ArrayList;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;

public final class SellInformationMessage {
    private final String username;
    private final ArrayList<Auction> openAuctions;
    private final ArrayList<Auction> closedAuctions;
    private final ArrayList<Article> unsoldArticles;

    public SellInformationMessage(String username, ArrayList<Auction> openAuctions, ArrayList<Auction> closedAuctions, ArrayList<Article> unsoldArticles) {
        this.username = username;
        this.openAuctions = openAuctions;
        this.closedAuctions = closedAuctions;
        this.unsoldArticles = unsoldArticles;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Auction> getOpenAuctions() {
        return openAuctions;
    }

    public ArrayList<Auction> getClosedAuctions() {
        return closedAuctions;
    }

    public ArrayList<Article> getUnsoldArticles() {
        return unsoldArticles;
    }
}
