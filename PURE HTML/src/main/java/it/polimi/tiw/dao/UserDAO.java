package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT Username, FirstName, LastName, Address FROM users WHERE Username = ? AND Password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					Date currentDate = new Date();
					Timestamp loginTime = new Timestamp(currentDate.getTime());
					user.setUsername(result.getString("Username"));
					user.setFirstName(result.getString("FirstName"));
					user.setLastName(result.getString("LastName"));
					user.setAddress(result.getString("Address"));
					user.setLoginTime(loginTime);
					return user;
				}
			}
		}
	}

	public User findUserByUsername(String username) throws SQLException {
		User user = new User();
		String query = "SELECT * FROM users WHERE Username = ?";

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);

			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst()) // no results, user not found
					return null;
				else {
					result.next();
					user.setUsername(result.getString("Username"));
					user.setFirstName(result.getString("FirstName"));
					user.setLastName(result.getString("LastName"));
					user.setAddress(result.getString("Address"));
					return user;
				}
			}
		}
	}
}
