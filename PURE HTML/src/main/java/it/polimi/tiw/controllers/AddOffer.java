package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.utils.ParamValidator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;

/**
 * Servlet implementation class AggiungiOfferta
 */
@WebServlet("/AddOffer")
public class AddOffer extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	private Timestamp sentTime;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddOffer() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			// Template Engine
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
			templateResolver.setTemplateMode(TemplateMode.HTML);
			this.templateEngine = new TemplateEngine();
			this.templateEngine.setTemplateResolver(templateResolver);
			templateResolver.setSuffix(".html");
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
			Date currentDate = new Date();
			sentTime = new Timestamp(currentDate.getTime());
			String auctionIdString = request.getParameter("auctionID");
			String bidderUsername = request.getParameter("bidderUsername");
			String offerString = request.getParameter("offer");

			if (auctionIdString != null && !auctionIdString.isBlank()
					&& ParamValidator.isPositiveInteger(auctionIdString) && bidderUsername != null
					&& !bidderUsername.isBlank() && offerString != null && !offerString.isBlank()
					&& ParamValidator.isPositiveInteger(offerString)
					&& bidderUsername.equals(((User) session.getAttribute("user")).getUsername())) {

				int auctionId = Integer.parseInt(auctionIdString);
				int offer = Integer.parseInt(offerString);
				OfferDAO offerDAO = new OfferDAO(connection);
				AuctionDAO auctionDAO = new AuctionDAO(connection);

				Auction auction;
				try {
					auction = auctionDAO.findAuctionWithArticlesById(auctionId);
				} catch (SQLException e1) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters");
					return;
				}

				if (auction == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That auction doesn't exist");
					return;
				}

				if (auction.getEndDate().before(new Timestamp(new Date().getTime()))) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"That auction doesn't have any time left for offering");
					return;
				}

				try {
					if (offer >= offerDAO.getMinimumOffer(auctionId)) {
						offerDAO.addOffer(offer, bidderUsername, sentTime, auctionId);
						request.getSession().setAttribute("done", true);
					} else {
						request.getSession().setAttribute("done", false);
					}
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in adding offer to database");
					return;
				}

				RequestDispatcher dispatcher = request.getRequestDispatcher("/ViewOffers");
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
