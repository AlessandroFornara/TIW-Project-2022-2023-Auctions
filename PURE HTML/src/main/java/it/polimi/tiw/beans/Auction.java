package it.polimi.tiw.beans;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Auction {

    public Auction() {
        this.auctionId = 0;
        this.username = null;
        this.expired = 0;
        this.maxBid = 0;
        this.endDate = null;
        this.minimumRaise = 0;
        this.finalPrice = 0;
        this.initialPrice = 0;
        this.remainingTimeMillis = 0;
        this.articles = new ArrayList<>();
        this.winner = null;
    }

    public Auction(int auctionId, String username, int expired, int maxBid, Timestamp endDate, int minimumRaise, int finalPrice, int initialPrice, ArrayList<Article> articles, String winner) {
        this.auctionId = auctionId;
        this.username = username;
        this.expired = expired;
        this.maxBid = maxBid;
        this.endDate = endDate;
        this.minimumRaise = minimumRaise;
        this.finalPrice = finalPrice;
        this.initialPrice = initialPrice;
        this.articles = articles;
        this.winner = null;
    }

    private int auctionId;
    private String username;
    private int expired;
    private int maxBid;
    private Timestamp endDate;
    private int minimumRaise;
    private int finalPrice;
    private int initialPrice;
    private long remainingTimeMillis;
    private String winner;

    private ArrayList<Article> articles;

    public void copyArticles(ArrayList<Article> articles) {
        for(Article article : articles) {
            this.articles.add(article);
        }
    }

    public ArrayList<Article> getArticles() {
        ArrayList<Article> copy = new ArrayList<>();
        for(Article article : articles) {
            copy.add(article);
        }
        return copy;
    }

    public void calculateRemainingTime(Timestamp loginTime) {
        long millis = loginTime.getTime();
        long deadline = endDate.getTime();
        remainingTimeMillis = deadline - millis;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int isExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public int getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(int maxBid) {
        this.maxBid = maxBid;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public int getMinimumRaise() {
        return minimumRaise;
    }

    public void setMinimumRaise(int minimumRaise) {
        this.minimumRaise = minimumRaise;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getRemainingTime() {
        long days = remainingTimeMillis / (1000 * 60 * 60 * 24);
        long hours = (remainingTimeMillis / (1000 * 60 * 60)) % 24;

        return String.format("%d days and %d hours", days, hours);
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}
