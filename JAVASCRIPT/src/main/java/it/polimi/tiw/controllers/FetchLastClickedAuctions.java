package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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

import com.google.gson.Gson;

import it.polimi.tiw.utils.ParamValidator;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.messages.LastClickedMessage;
import it.polimi.tiw.messages.SellInformationMessage;

/**
 * Servlet implementation class AggiungiOfferta
 */
@WebServlet("/FetchLastClickedAuctions")
@MultipartConfig
public class FetchLastClickedAuctions extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	ArrayList<Auction> result;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FetchLastClickedAuctions() {
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
			String username = ((User) session.getAttribute("user")).getUsername();
			String arrayIdsString = request.getParameter("arrayIds");
			char[] usernameCharArray = username.toCharArray();

			if (arrayIdsString != null && !arrayIdsString.isEmpty()) {
				AuctionDAO auctionDao = new AuctionDAO(connection);
				try {
					Gson gson = new Gson();
					LastClickedMessage[] arrayIds = gson.fromJson(arrayIdsString, LastClickedMessage[].class);
					
					ArrayList<Integer> arrayIntegers = new ArrayList<Integer>();

					for (LastClickedMessage i : arrayIds) {
						int l = i.getUsername().length();
						char[] s = i.getUsername().toCharArray();
						
						if (usernameCharArray.length == l-2) {	
							boolean flag = true;
							for (int j = 0; j < l-2 && flag; j++) {
								if (s[j] != usernameCharArray[j]) {
									flag = false;
								}
							}
							if (flag) {
								arrayIntegers.add(i.getId());
							}
						}

					}
			
					ArrayList<Auction> result = auctionDao.findLastClickedAuctionsById(arrayIntegers);

					response.setStatus(HttpServletResponse.SC_OK);
					String json = gson.toJson(result);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().println(json);
				} catch (NumberFormatException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Invalid arrayIds format");
					return;
				} catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Internal server error");
					return;
				}
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Missing arrayIds parameter");
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
