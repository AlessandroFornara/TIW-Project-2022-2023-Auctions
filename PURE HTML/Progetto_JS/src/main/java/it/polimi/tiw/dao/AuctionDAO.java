package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class AuctionDAO {
	private Connection con;

	public AuctionDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Auction> findAuctionsByUsername(String username, int expired) throws SQLException {
		ArrayList<Auction> auctions = new ArrayList<>();
		String query = "SELECT * FROM auctions WHERE Username = ? AND Expired = ? ORDER BY Deadline ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setInt(2, expired);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						Auction auction = new Auction();
						auction.setUsername(result.getString("Username"));
						auction.setAuctionId(result.getInt("ID"));
						auction.setExpired(result.getInt("Expired"));
						auction.setMaxBid(result.getInt("MaxBid"));
						auction.setEndDate(result.getTimestamp("Deadline"));
						auction.setMinimumRaise(result.getInt("MinimumIncrement"));
						auction.setFinalPrice(result.getInt("FinalPrice"));
						auction.setInitialPrice(result.getInt("InitialPrice"));
						auction.setWinner(result.getString("Winner"));
						auctions.add(auction);
					}
					return auctions;
				}
			}
		}
	}

	public void createAuction(Auction auction) throws SQLException {
		PreparedStatement pstatement = con.prepareStatement(
				"INSERT INTO auctions (ID, Username, Expired, MaxBid, Deadline, MinimumIncrement, FinalPrice, InitialPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		pstatement.setInt(1, auction.getAuctionId());
		pstatement.setString(2, auction.getUsername());
		pstatement.setInt(3, auction.isExpired());
		pstatement.setInt(4, auction.getMaxBid());
		pstatement.setTimestamp(5, new Timestamp(auction.getEndDate().getTime()));
		pstatement.setInt(6, auction.getMinimumRaise());
		pstatement.setInt(7, auction.getFinalPrice());
		pstatement.setInt(8, auction.getInitialPrice());
		pstatement.executeUpdate();
	}

	public int findLastAuctionId() throws SQLException {
		String query = "SELECT MAX(ID) AS LastId FROM auctions";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				}
				result.next();
				return result.getInt("LastId");
			}
		}
	}

	public ArrayList<Auction> findAuctionsByArticleKeyword(String keyword, Timestamp sentTime) throws SQLException {
		ArrayList<Auction> auctions = new ArrayList<>();
		String query = "SELECT DISTINCT a.* FROM auctions a JOIN articles ar ON a.ID = ar.AuctionId WHERE (LOWER(ar.Name) LIKE CONCAT('%', LOWER(?), '%') OR LOWER(ar.Description) LIKE CONCAT('%', LOWER(?), '%')) AND a.Deadline > ? ORDER BY Deadline DESC";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, keyword);
			pstatement.setString(2, keyword);
			pstatement.setTimestamp(3, sentTime);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						Auction auction = new Auction();
						auction.setUsername(result.getString("Username"));
						auction.setAuctionId(result.getInt("ID"));
						auction.setExpired(result.getInt("Expired"));
						auction.setMaxBid(result.getInt("MaxBid"));
						auction.setEndDate(result.getTimestamp("Deadline"));
						auction.setMinimumRaise(result.getInt("MinimumIncrement"));
						auction.setFinalPrice(result.getInt("FinalPrice"));
						auction.setInitialPrice(result.getInt("InitialPrice"));
						auctions.add(auction);
					}
					return auctions;
				}
			}
		}
	}

	public void updateAuction(int id) throws SQLException {
		String query = "UPDATE auctions SET Expired = ?, FinalPrice = MaxBid, Winner = (SELECT Bidder FROM offers WHERE Auction = ? AND OfferPrice = (SELECT MAX(OfferPrice) FROM offers WHERE Auction = ?)) WHERE ID = ?";
		try(PreparedStatement pstmt = con.prepareStatement(query)){
			pstmt.setInt(1, 1);
			pstmt.setInt(2, id);
			pstmt.setInt(3, id);
			pstmt.setInt(4, id);
			pstmt.executeUpdate();
		}
	}

	public Auction findAuctionWithArticlesById(int auctionId) throws SQLException {
		Auction auction = new Auction();
		String query = "SELECT * FROM auctions WHERE ID = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auctionId);

			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					result.next();
					auction.setUsername(result.getString("Username"));
					auction.setAuctionId(result.getInt("ID"));
					auction.setExpired(result.getInt("Expired"));
					auction.setMaxBid(result.getInt("MaxBid"));
					auction.setEndDate(result.getTimestamp("Deadline"));
					auction.setMinimumRaise(result.getInt("MinimumIncrement"));
					auction.setFinalPrice(result.getInt("FinalPrice"));
					auction.setInitialPrice(result.getInt("InitialPrice"));
					auction.setWinner(result.getString("Winner"));

					ArrayList<Article> articles = new ArrayList<>();
					ArticleDAO articleDAO = new ArticleDAO(con);
					articles = articleDAO.findArticlesByAuctionID(auctionId);
					auction.copyArticles(articles);

					return auction;
				}
			}
		}
	}

	public ArrayList<Auction> findLastClickedAuctionsById(ArrayList<Integer> auctions) throws SQLException {
		ArrayList<Auction> result = new ArrayList<Auction>();
		for (Integer id : auctions) {
			Auction auction = findAuctionWithArticlesById(id);
			if (auction.isExpired() == 0) {
				result.add(auction);
			}
		}

		return result;
	}

	public ArrayList<Integer> findAuctionIdsByWinner(String winner) throws SQLException {
		ArrayList<Integer> auctionIds = new ArrayList<>();
		String query = "SELECT ID FROM auctions WHERE Winner = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, winner);

			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						auctionIds.add(result.getInt("ID"));
					}
					return auctionIds;
				}
			}
		}
	}
}
