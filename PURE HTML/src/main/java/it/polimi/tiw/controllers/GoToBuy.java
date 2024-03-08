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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Article;
import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.dao.AuctionDAO;

/**
 * Servlet implementation class AcquistoServlet
 */
@WebServlet("/GoToBuy")
public class GoToBuy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

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
			// Template Engine
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
			templateResolver.setTemplateMode(TemplateMode.HTML);
			this.templateEngine = new TemplateEngine();
			this.templateEngine.setTemplateResolver(templateResolver);
			templateResolver.setSuffix(".html");
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

			// USED FOR THE OFFERS PAGE
			keyword = request.getParameter("keyword");
			request.getSession().setAttribute("done", true);

			Date currentDate = new Date();
			sentTime = new Timestamp(currentDate.getTime());

			auctionDAO = new AuctionDAO(connection);
			articleDAO = new ArticleDAO(connection);
			
			ArrayList<Auction> wonAuctions = new ArrayList<>();

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
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
							"Failure in accessing auctions or articles containing the keyword");
					return;
				}
			}

			try {
				auctionIds = auctionDAO.findAuctionIdsByWinner(username);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in accessing auctions won by the user");
				return;
			}

			if (auctionIds != null) {

				for (Integer auctionId : auctionIds) {
					Auction auction = new Auction();
					try {
						auction = auctionDAO.findAuctionWithArticlesById(auctionId);
					} catch (SQLException e) {
						response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
								"Failure in accessing auctions and articles won by the user");
						return;
					}

					wonAuctions.add(auction);
				}

			}

			String path = "/WEB-INF/Buy.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("username", username);
			ctx.setVariable("keyword", keyword);
			ctx.setVariable("foundAuctions", foundAuctions);
			ctx.setVariable("wonAuctions", wonAuctions);
			templateEngine.process(path, ctx, response.getWriter());
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
