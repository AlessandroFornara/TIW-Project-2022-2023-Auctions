package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

/**
 * Servlet implementation class CheckLogin
 */

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	UserDAO usr;
	User u = null;
	
	public CheckLogin() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");

		if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty() || usrn.length() > 16 || pwd.length() > 12) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		usr = new UserDAO(connection);
		
		try {
			u = usr.checkCredentials(usrn, pwd);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		
		if (u == null) {
			String path = getServletContext().getContextPath() + "/Login.html";
			response.sendRedirect(path);
			return;
		} else {
			request.getSession().setAttribute("user", u);
			String path = getServletContext().getContextPath() + "/GoToHomePage";
			response.sendRedirect(path);
			return;
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {

		}
	}
}