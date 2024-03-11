package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

/**
 * Servlet implementation class OpenImage
 */
@WebServlet("/OpenImage")
@MultipartConfig
public class OpenImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OpenImage() {
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

			String img = request.getParameter("image");

			System.out.println(img);

			int integerImageCode;
			String imagePath = null;

			if (img != null && !img.isBlank() && ParamValidator.isPositiveInteger(img)) {
				integerImageCode = Integer.parseInt(img);
				System.out.println(integerImageCode);

				switch (integerImageCode) {

				case 1 -> imagePath = getServletContext().getContextPath() + "/images/chitarraAcustica.jpg";

				case 2 -> imagePath = getServletContext().getContextPath() + "/images/amplificatore.jpg";

				case 3 -> imagePath = getServletContext().getContextPath() + "/images/microfono.jpg";

				case 4 -> imagePath = getServletContext().getContextPath() + "/images/logiPebbleMouse.jpg";

				case 5 -> imagePath = getServletContext().getContextPath() + "/images/mousePad.jpg";

				case 6 -> imagePath = getServletContext().getContextPath() + "/images/macBookPro.jpg";

				case 7 -> imagePath = getServletContext().getContextPath() + "/images/palloneChampionsLeague.jpg";

				case 8 -> imagePath = getServletContext().getContextPath() + "/images/magliaMilan.jpg";

				case 9 -> imagePath = getServletContext().getContextPath() + "/images/pantalonciniMilan.jpg";

				case 10 -> imagePath = getServletContext().getContextPath() + "/images/parastinchi.jpg";

				case 11 -> imagePath = getServletContext().getContextPath() + "/images/cdBobMarley.jpg";

				case 12 -> imagePath = getServletContext().getContextPath() + "/images/lettoreCD.jpg";

				case 13 -> imagePath = getServletContext().getContextPath() + "/images/applePen.jpg";

				case 14 -> imagePath = getServletContext().getContextPath() + "/images/ipadPro2015.jpg";

				case 15 -> imagePath = getServletContext().getContextPath() + "/images/cavoUSBC.jpg";

				case 16 -> imagePath = getServletContext().getContextPath() + "/images/cavoAux.jpg";

				case 17 -> imagePath = getServletContext().getContextPath() + "/images/libroGigiDAlessio.jpg";

				case 18 -> imagePath = getServletContext().getContextPath() + "/images/cuffieBeats.jpg";

				case 19 -> imagePath = getServletContext().getContextPath() + "/images/xboxOne.jpg";

				case 20 -> imagePath = getServletContext().getContextPath() + "/images/controllerXbox.jpg";

				case 21 -> imagePath = getServletContext().getContextPath() + "/images/albumDiFigurine.jpg";

				case 22 -> imagePath = getServletContext().getContextPath() + "/images/biliardino.jpg";

				case 23 -> imagePath = getServletContext().getContextPath() + "/images/oboe.jpg";

				default -> {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Missing parameters or wrong parameters");
					return;
				}
				}

				response.setStatus(HttpServletResponse.SC_OK);
				String json = new Gson().toJson(imagePath);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(json);

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
