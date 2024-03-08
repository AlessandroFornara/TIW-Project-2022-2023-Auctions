package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.TimeZone;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.ParamValidator;

/**
 * Servlet implementation class AggiungiAsta
 */
@WebServlet("/AddAuction")
public class AddAuction extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private long clickTime;
	private ArticleDAO articleDAO;
	private AuctionDAO auctionDAO;

	public AddAuction() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if(session != null) {

			String username = ((User) session.getAttribute("user")).getUsername();
			String minimumBidIncrement = request.getParameter("minimumBidIncrement");
			String date = request.getParameter("expirationDate");
			System.out.println(date);
			String[] articlesToAdd = request.getParameterValues("articlesToAdd");

			Date currentDate = new Date();
			clickTime = currentDate.getTime();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
			dateFormat.setLenient(false);
			Date parsedDate = null;
			try {
				parsedDate = dateFormat.parse(date);
				System.out.println(parsedDate);
			} catch (ParseException e2) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format. Please provide the date in the format yyyy-MM-dd HH.");
				return;
			}

			if(parsedDate.getTime() < clickTime) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The date you've entered is not valid. You can't create an auction that has already expired.");
				return;
			}

			if(username != null && !username.isBlank() && minimumBidIncrement != null && !minimumBidIncrement.isBlank() && ParamValidator.isPositiveInteger(minimumBidIncrement) && date != null && !date.isBlank() && articlesToAdd != null) {

				int minimumBidIncrementInt = Integer.parseInt(minimumBidIncrement);
				int expired = 0;
				int highestBid = 0;
				int initialPrice = 0;
				int auctionId = 0;
				int finalPrice = 0;
				String winner = null;
				ArrayList<Integer> articleIdsToAdd = new ArrayList<>();
				ArrayList<Article> articles = new ArrayList<>();
				Timestamp expirationDate = new Timestamp(parsedDate.getTime());
				System.out.println(expirationDate);
				Auction auction;
				articleDAO = new ArticleDAO(connection);
				auctionDAO = new AuctionDAO(connection);

				for(String s: articlesToAdd) {
					try {
						articleIdsToAdd.add(Integer.parseInt(s));
					} catch (NumberFormatException e) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in converting String to Integer");
						return;
					}
				}

				try {
					auctionId = auctionDAO.findLastAuctionId() + 1;
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in accessing ID of last auction in the database");
					return;
				}

				for(Integer articleId: articleIdsToAdd) {
					try {
						articleDAO.updateArticleAuction(auctionId, articleId);
						initialPrice += articleDAO.getArticlePrice(articleId);
					} catch (SQLException e) {
						response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in updating item's auction ID");
						return;
					}
				}

				try {
					articles = articleDAO.findArticlesByAuctionID(auctionId);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Not possible to retrieve articles");
					return;
				}

				auction = new Auction(auctionId, username, expired, highestBid, expirationDate, minimumBidIncrementInt, finalPrice, initialPrice, articles, winner);

				try {
					auctionDAO.createAuction(auction);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failed to add auction to the database");
					return;
				}

				RequestDispatcher dispatcher = request.getRequestDispatcher("/GoToSell");
				dispatcher.forward(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters or wrong parameters");
				return;
			}
		} else {
			String path = getServletContext().getContextPath() + "/Login.html";
			response.sendRedirect(path);
			return;
		}
	}
}