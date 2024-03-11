package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Article;
import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.messages.BuyInformationMessage;

/**
 * Servlet implementation class AcquistoServlet
 */
@WebServlet("/GoToBuy")
@MultipartConfig
public class GoToBuy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	private AuctionDAO auctionDAO;
	private ArticleDAO articleDAO;
	private ArrayList<Auction> foundAuctions = null;
	private ArrayList<Article> tmpArticles = new ArrayList<>();
	private String keyword = null;
	private Timestamp sentTime;
	private ArrayList<Integer> auctionIds;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			// Connessione
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
	 * @see HttpServlet#HttpServlet()
	 */
	public GoToBuy() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			String username = ((User) session.getAttribute("user")).getUsername();

			keyword = request.getParameter("keyword");
			// USED FOR THE OFFERS PAGE
			request.getSession().setAttribute("done", true);

			Date currentDate = new Date();
			sentTime = new Timestamp(currentDate.getTime());

			auctionDAO = new AuctionDAO(connection);
			articleDAO = new ArticleDAO(connection);
			
			ArrayList<Auction> wonAuctions = new ArrayList<Auction>();

			if (keyword != null && !keyword.isBlank()) {
				try {
					foundAuctions = auctionDAO.findAuctionsByArticleKeyword(keyword, sentTime);
					if (foundAuctions != null) {
						for (Auction auction : foundAuctions) {
							tmpArticles = articleDAO.findArticlesByAuctionID(auction.getAuctionId());
							auction.copyArticles(tmpArticles);
							auction.calculateRemainingTime(sentTime);
						}
					}

				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
					response.getWriter().println("Failure in accessing auctions or articles containing the key word");
					return;
				}
			}

			try {
				auctionIds = auctionDAO.findAuctionIdsByWinner(username);
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
				response.getWriter().println("Failure in accessing auctions won by the user");
				return;
			}

			if (auctionIds != null) {
				
				for (Integer auctionId : auctionIds) {
					Auction auction = new Auction();
					try {
						auction = auctionDAO.findAuctionWithArticlesById(auctionId);
					} catch (SQLException e) {
						response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
						response.getWriter().println("Failure in accessing auctions and articles won by the user");
						return;
					}

					wonAuctions.add(auction);
				}

			}

			response.setStatus(HttpServletResponse.SC_OK);
			BuyInformationMessage message = new BuyInformationMessage(keyword, foundAuctions, wonAuctions);
			String json = new Gson().toJson(message);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json);
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
