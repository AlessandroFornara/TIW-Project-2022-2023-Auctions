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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Auction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.utils.ParamValidator;
import it.polimi.tiw.beans.User;

/**
 * Servlet implementation class VisualizzaOfferte
 */
@WebServlet("/ViewOffers")
public class ViewOffers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	AuctionDAO auctionDAO;
	OfferDAO offerDAO;
	private Auction auction;
	private ArrayList<Offer> auctionOffers;
	private String username;
	private boolean done;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ViewOffers() {
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
						response.sendError(HttpServletResponse.SC_BAD_REQUEST,
								"Missing parameters or wrong parameters");
						return;
					}

					this.auctionDAO = new AuctionDAO(connection);
					this.auction = auctionDAO.findAuctionWithArticlesById(auctionId);

					if (auction == null) {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That auction doesn't exist");
						return;
					}

					if (auction.getEndDate().before(new Timestamp(new Date().getTime()))) {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST,
								"That auction doesn't have any time left for offering");
						return;
					}

					this.offerDAO = new OfferDAO(connection);
					this.auctionOffers = offerDAO.findOffersByAuctionId(auctionId);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to access Offers");
					return;
				}

				String path = "/WEB-INF/ViewOffers.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("auction", auction);
				ctx.setVariable("auctionOffers", auctionOffers);
				ctx.setVariable("username", username);
				ctx.setVariable("done", done);
				templateEngine.process(path, ctx, response.getWriter());
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
