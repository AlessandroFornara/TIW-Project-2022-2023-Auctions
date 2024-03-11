package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.messages.ClosedAuctionDetailsMessage;
import it.polimi.tiw.utils.ParamValidator;

import com.google.gson.Gson;

/**
 * Servlet implementation class DettaglioAstaChiusaServlet
 */
@WebServlet("/ClosedAuctionDetails")
@MultipartConfig
public class ClosedAuctionDetails extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private Auction auction;
	private User user;
	private String username;

	private ArrayList<Article> auctionArticles;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			username = ((User) session.getAttribute("user")).getUsername();
			String auctionIdString = request.getParameter("auctionID");
			System.out.println(auctionIdString);

			if (username != null && !username.isBlank() && auctionIdString != null && !auctionIdString.isBlank()
					&& ParamValidator.isPositiveInteger(auctionIdString)) {

				try {

					int auctionId = Integer.parseInt(auctionIdString);

					AuctionDAO auctionDAO = new AuctionDAO(connection);
					auction = auctionDAO.findAuctionWithArticlesById(auctionId);
					if (auction != null) {
						if (auction.getUsername().equals(username) && auction.isExpired() == 1) {
							auctionArticles = auction.getArticles();

							if (auction.getWinner() == null) {
								auction.setWinner("This auction does not have a winner");
							}

							UserDAO userDAO = new UserDAO(connection);
							user = userDAO.findUserByUsername(username);
						} else {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.getWriter().println("You can'open the details of that auction");
							return;

						}
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("That auction doesn't exist");
						return;
					}
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Unable to access Auction or Offers");
					return;
				}

				response.setStatus(HttpServletResponse.SC_OK);
				ClosedAuctionDetailsMessage message = new ClosedAuctionDetailsMessage(username, auction,
						auctionArticles, user);
				String json = new Gson().toJson(message);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
