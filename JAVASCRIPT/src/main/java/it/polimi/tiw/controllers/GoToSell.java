package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.google.gson.Gson;

import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.messages.SellInformationMessage;
import it.polimi.tiw.beans.User;

/**
 * Servlet implementation class GoToSell
 */
@WebServlet("/GoToSell")
@MultipartConfig
public class GoToSell extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private ArrayList<Auction> openAuctions = new ArrayList<>();
	private ArrayList<Auction> closedAuctions = new ArrayList<>();
	private ArrayList<Article> unsoldArticles = new ArrayList<>();
	private ArrayList<Article> tmp = new ArrayList<>();
	private AuctionDAO auctionDAO;
	private ArticleDAO articleDAO;
	private String username;
	private Timestamp loginTime;

	public GoToSell() {
		super();
	}

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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			username = ((User) session.getAttribute("user")).getUsername();
			loginTime = ((User) session.getAttribute("user")).getLoginTime();

			if (username != null && !username.isBlank() && loginTime != null) {

				try {

					// SHOW AUCTIONS
					auctionDAO = new AuctionDAO(connection);
					articleDAO = new ArticleDAO(connection);
					this.openAuctions = auctionDAO.findAuctionsByUsername(username, 0);
					this.closedAuctions = auctionDAO.findAuctionsByUsername(username, 1);

					if (openAuctions != null) {
						for (Auction a : openAuctions) {
							tmp = articleDAO.findArticlesByAuctionID(a.getAuctionId());
							a.copyArticles(tmp);
							a.calculateRemainingTime(loginTime);
						}
					}
					if (closedAuctions != null) {
						for (Auction a : closedAuctions) {
							tmp = articleDAO.findArticlesByAuctionID(a.getAuctionId());
							a.copyArticles(tmp);
						}
					}

					// SHOW UNSOLD ARTICLES
					unsoldArticles = articleDAO.findUnsoldArticlesByUsername(username);

				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Not possible to recover auctions");
					return;
				}

				response.setStatus(HttpServletResponse.SC_OK);
				SellInformationMessage message = new SellInformationMessage(username, openAuctions, closedAuctions, unsoldArticles);
				String json = new Gson().toJson(message);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);
			
			} else {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Session is not valid");
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
