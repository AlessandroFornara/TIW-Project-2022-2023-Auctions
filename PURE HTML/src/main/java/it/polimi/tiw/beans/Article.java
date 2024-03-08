package it.polimi.tiw.beans;

public class Article {
    private int code;
    private String name;
    private String description;
    private String image;
    private int price;
    private String articleCreator;
    private int auctionId;

    public Article() {
        this.code = 0;
        this.name = null;
        this.description = null;
        this.image = null;
        this.price = 0;
        this.articleCreator = null;
        this.auctionId = 0;
        this.image = null;
    }

    public Article(int code, String name, String description, int price, String articleCreator, int auctionId, String image) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.price = price;
        this.articleCreator = articleCreator;
        this.auctionId = auctionId;
        this.image = image;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getArticleCreator() {
        return articleCreator;
    }

    public void setArticleCreator(String articleCreator) {
        this.articleCreator = articleCreator;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
