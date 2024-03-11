package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.ParamValidator;

@WebServlet("/CloseAuction")
@MultipartConfig
public class CloseAuction extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private Auction auction;
	private ArrayList<Offer> auctionOffers;
	private String username;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			// Connection
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			username = ((User) session.getAttribute("user")).getUsername();
			String auctionIdString = request.getParameter("auctionID");

			if (username != null && !username.isBlank() && auctionIdString != null && !auctionIdString.isBlank()
					&& ParamValidator.isPositiveInteger(auctionIdString)) {

				try {
					int auctionId = Integer.parseInt(auctionIdString);
					AuctionDAO auctionDAO = new AuctionDAO(connection);

					this.auction = auctionDAO.findAuctionWithArticlesById(auctionId);
					if (auction.getEndDate().after(new Timestamp(new Date().getTime()))
							|| !auction.getUsername().equals(username)) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("You can't close that auction");
						return;
					}

					auctionDAO.updateAuction(auctionId);

				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Unable to close the auction");
					return;
				}

				RequestDispatcher dispatcher = request.getRequestDispatcher("/GoToSell");
				dispatcher.forward(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing parameters or wrong parameters");
				return;
			}
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Session is not valid");
			return;
		}
	}
}
