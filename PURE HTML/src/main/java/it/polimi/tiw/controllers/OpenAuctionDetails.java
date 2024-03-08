package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

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
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.utils.ParamValidator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/OpenAuctionDetails")
public class OpenAuctionDetails extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	private String username;
	private Auction auction;
	private ArrayList<Offer> auctionOffers;
	private ArrayList<Article> auctionArticles;
	private long clickTime;
	private long difference;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {

			username = ((User) session.getAttribute("user")).getUsername();
			String auctionIdString = request.getParameter("auctionID");

			Date currentDate = new Date();
			clickTime = currentDate.getTime();

			if (username != null && !username.isBlank() && auctionIdString != null && !auctionIdString.isBlank()
					&& ParamValidator.isPositiveInteger(auctionIdString)) {

				try {

					int auctionId = Integer.parseInt(auctionIdString);

					AuctionDAO auctionDAO = new AuctionDAO(connection);
					auction = auctionDAO.findAuctionWithArticlesById(auctionId);

					if (auction != null) {
						if (auction.getUsername().equals(username) && auction.isExpired() == 0) {
							auctionArticles = auction.getArticles();

							difference = auction.getEndDate().getTime() - clickTime;

							OfferDAO offerDAO = new OfferDAO(connection);
							auctionOffers = offerDAO.findOffersByAuctionId(auctionId);
						} else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST,
									"You can'open the details of that auction");
							return;
						}
					} else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That auction doesn't exist");
						return;
					}

				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Unable to access Auction or Offers");
					return;
				}

				String path = "/WEB-INF/OpenAuctionDetails.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("username", username);
				ctx.setVariable("auction", auction);
				ctx.setVariable("auctionArticles", auctionArticles);
				ctx.setVariable("auctionOffers", auctionOffers);
				ctx.setVariable("difference", difference);
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
