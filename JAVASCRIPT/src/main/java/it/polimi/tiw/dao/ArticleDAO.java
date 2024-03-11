package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArticleDAO {
	private Connection con;

	public ArticleDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Article> findArticlesByAuctionID(int ID) throws SQLException {
		ArrayList<Article> articles = new ArrayList<>();
		String query = "SELECT * FROM articles WHERE AuctionId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, ID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						Article article = new Article();
						article.setCode(result.getInt("Code"));
						article.setName((result.getString("Name")));
						article.setDescription(result.getString("Description"));
						article.setImage(result.getString("Image"));
						article.setPrice(result.getInt("Price"));
						article.setArticleCreator(result.getString("ArticleCreator"));
						article.setAuctionId(result.getInt("AuctionId"));
						articles.add(article);
					}
					return articles;
				}
			}
		}
	}

	public ArrayList<Article> findUnsoldArticlesByUsername(String username) throws SQLException {
		ArrayList<Article> articles = new ArrayList<>();
		String query = "SELECT * FROM articles WHERE ArticleCreator = ? AND AuctionId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setInt(2, 0);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						Article article = new Article();
						article.setCode(result.getInt("Code"));
						article.setName((result.getString("Name")));
						article.setDescription(result.getString("Description"));
						article.setImage(result.getString("Image"));
						article.setPrice(result.getInt("Price"));
						article.setArticleCreator(result.getString("ArticleCreator"));
						article.setAuctionId(result.getInt("AuctionId"));
						articles.add(article);
					}
					return articles;
				}
			}
		}
	}

	public void updateArticleAuction(int auctionID, int code) throws SQLException {
		String query = "UPDATE articles SET AuctionId = ? WHERE Code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auctionID);
			pstatement.setInt(2, code);
			pstatement.executeUpdate();
		}
	}

	public int getArticlePrice(int code) throws SQLException {
		String query = "SELECT Price FROM articles WHERE Code = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, code);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				}
				result.next();
				return result.getInt("Price");
			}
		}
	}

	public void createArticle(Article article) throws SQLException {
		PreparedStatement pstatement = con.prepareStatement(
				"INSERT INTO articles (Code, Name, Description, Image, Price, ArticleCreator, AuctionId) VALUES (?, ?, ?, ?, ?, ?, ?)");
		pstatement.setInt(1, article.getCode());
		pstatement.setString(2, article.getName());
		pstatement.setString(3, article.getDescription());
		pstatement.setString(4, "x"); //TODO: MODIFY
		pstatement.setInt(5, article.getPrice());
		pstatement.setString(6, article.getArticleCreator());
		pstatement.setInt(7, article.getAuctionId());
		pstatement.executeUpdate();
	}

	public int findLastArticleCode() throws SQLException {
		String query = "SELECT MAX(Code) AS LastCode FROM articles";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				}
				result.next();
				return result.getInt("LastCode");
			}
		}
	}
}
