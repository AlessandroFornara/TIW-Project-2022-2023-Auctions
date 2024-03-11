package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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

import com.google.gson.Gson;

import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.messages.BuyInformationMessage;
import it.polimi.tiw.messages.ViewOffersMessage;
import it.polimi.tiw.utils.ParamValidator;
import it.polimi.tiw.beans.User;

/**
 * Servlet implementation class VisualizzaOfferte
 */
@WebServlet("/ViewOffers")
@MultipartConfig
public class ViewOffers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	AuctionDAO auctionDAO;
	OfferDAO offerDAO;
	private Auction auction;
	private ArrayList<Offer> auctionOffers;
	private String username;
	private boolean done;
	private Offer offer;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ViewOffers() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			username = ((User) session.getAttribute("user")).getUsername();
			String auctionIdString = request.getParameter("auctionID");

			if (username != null && !username.isBlank() && auctionIdString != null && !auctionIdString.isBlank()
					&& ParamValidator.isPositiveInteger(auctionIdString)) {

				try {
					int auctionId = Integer.parseInt(auctionIdString);

					Boolean doneValue = (Boolean) request.getSession().getAttribute("done");

					if (doneValue != null) {
						this.done = doneValue;
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Missing parameters or wrong parameters");
						return;
					}

					this.auctionDAO = new AuctionDAO(connection);
					this.auction = auctionDAO.findAuctionWithArticlesById(auctionId);

					if (auction == null) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("That auction doesn't exist");
						return;
					}

					if (auction.getEndDate().before(new Timestamp(new Date().getTime()))) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("That auction doesn't have any time left for offering");
						return;
					}

					this.offerDAO = new OfferDAO(connection);
					this.auctionOffers = offerDAO.findOffersByAuctionId(auctionId);
					if (auctionOffers != null) {
						offer = auctionOffers.get(0);
					}
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Unable to access Offers");
					return;
				}

				response.setStatus(HttpServletResponse.SC_OK);
				ViewOffersMessage message = new ViewOffersMessage(auction, auctionOffers, username, done, offer);
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
			String path = getServletContext().getContextPath() + "/Login.html";
			response.sendRedirect(path);
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
