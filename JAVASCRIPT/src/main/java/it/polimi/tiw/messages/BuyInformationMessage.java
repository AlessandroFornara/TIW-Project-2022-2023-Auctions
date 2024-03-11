package it.polimi.tiw.messages;

import java.util.ArrayList;

import it.polimi.tiw.beans.Auction;

public final class BuyInformationMessage {
	
	private final String keyword;
	private final ArrayList<Auction> foundAuctions;
	private final ArrayList<Auction> wonAuctions;
	
	public BuyInformationMessage(String keyword, ArrayList<Auction> foundAuctions, ArrayList<Auction> wonAuctions) {
		this.keyword = keyword;
		this.foundAuctions = foundAuctions;
		this.wonAuctions = wonAuctions;
	}
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public ArrayList<Auction> getFoundAuctions() {
		return this.foundAuctions;
	}
	
	public ArrayList<Auction> getWonAuctions() {
		return this.wonAuctions;
	}
}