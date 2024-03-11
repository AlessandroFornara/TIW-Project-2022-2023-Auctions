package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Offer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class OfferDAO {

	private Connection con;

	public OfferDAO(Connection connection) {
		this.con = connection;
	}

	public ArrayList<Offer> findOffersByAuctionId(int auctionId) throws SQLException {

		ArrayList<Offer> offers = new ArrayList<>();
		String query = "SELECT * FROM offers WHERE Auction = ? ORDER BY DateTime DESC";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auctionId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return null;
				} else {
					while (result.next()) {
						Offer offer = new Offer();

						offer.setAuction(auctionId);
						offer.setBidder(result.getString("Bidder"));
						offer.setOfferPrice(result.getInt("OfferPrice"));
						offer.setDateTime(result.getTimestamp("DateTime"));

						offers.add(offer);
					}
					return offers;
				}
			}
		}
	}

	public void addOffer(int offerPrice, String bidder, Timestamp dateTime, int auction) throws SQLException {

		PreparedStatement pstatement1 = con.prepareStatement("INSERT INTO offers (OfferPrice, Bidder, DateTime, Auction) VALUES (?, ?, ?, ?)");
		pstatement1.setInt(1, offerPrice);
		pstatement1.setString(2, bidder);
		pstatement1.setTimestamp(3, dateTime);
		pstatement1.setInt(4, auction);

		PreparedStatement pstatement2 = con.prepareStatement("UPDATE auctions SET MaxBid = ? WHERE ID = ?");
		pstatement2.setInt(1, offerPrice);
		pstatement2.setInt(2, auction);

		pstatement1.executeUpdate();
		pstatement2.executeUpdate();
	}

	public int getMinimumOffer(int auction) throws SQLException {
		int currentOffer, increment, initialPrice;

		currentOffer = getCurrentOffer(auction);
		increment = getMinimumIncrement(auction);
		initialPrice = getInitialPrice(auction);

		if (currentOffer == 0) {
			return increment + initialPrice;
		} else {
			return currentOffer + increment;
		}
	}

	private int getCurrentOffer(int auction) throws SQLException {

		String query = "SELECT MAX(OfferPrice) AS HighestOffer FROM offers WHERE Auction = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auction);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				} else {
					result.next();
					return result.getInt("HighestOffer");
				}
			}
		}
	}

	private int getMinimumIncrement(int auction) throws SQLException {

		String query = "SELECT MinimumIncrement FROM auctions WHERE ID = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auction);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				} else {
					result.next();
					return result.getInt("MinimumIncrement");
				}
			}
		}
	}

	private int getInitialPrice(int auction) throws SQLException {

		String query = "SELECT InitialPrice FROM auctions WHERE ID = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, auction);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) {
					return 0;
				} else {
					result.next();
					return result.getInt("InitialPrice");
				}
			}
		}
	}
}
