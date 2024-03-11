package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.utils.ParamValidator;

/**
 * Servlet implementation class AddArticle
 */
@WebServlet("/AddArticle")
@MultipartConfig
public class AddArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private ArticleDAO articleDAO;

	private String username;
	private String articleName;
	private String articleDescription;
	private String articlePrice;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddArticle() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {
			username = ((User) session.getAttribute("user")).getUsername();
			articleName = request.getParameter("articleName");
			articleDescription = request.getParameter("articleDescription");
			articlePrice = request.getParameter("articlePrice");

			if (username != null && !username.isBlank() && articleName != null && !articleName.isBlank()
					&& articleName.length() < 30 && articleDescription != null && !articleDescription.isBlank()
					&& articleDescription.length() < 200 && articlePrice != null && !articlePrice.isBlank()
					&& ParamValidator.isPositiveInteger(articlePrice)) {

				articleDAO = new ArticleDAO(connection);
				int code = 0;
				try {
					code = articleDAO.findLastArticleCode() + 1;
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
					response.getWriter().println("Failure in accessing ID of last article in the database");
					return;
				}

				Article article = new Article(code, articleName, articleDescription, Integer.parseInt(articlePrice),
						username, 0);
				try {
					articleDAO.createArticle(article);
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
					response.getWriter().println("Failure in adding article to database");
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
