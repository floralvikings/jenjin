package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.authentication.UserFactory;
import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to lookup users in SQL database; specifically, from a table called <b>JenjinUsers</b>. This table must have (at
 * minimum) the following columns: <ol> <li> {@code username} - The column used to store usernames.  This should be a
 * unique key, stored as a text type. </li> <li> {@code password} - The column used to store passwords.  These should be
 * the user's password prepended with the value in the {@code salt} column and then hashed with a SHA-256 hashing
 * scheme. Should be a text type. </li> <li> {@code salt} - The value prepended to the user's password before it is
 * hashed with a SHA-256 hashing scheme.  Should be a text type.</li> <li> {@code loggedin} - The value that indicated
 * whether a user is logged in; should be a boolean type. </li> </ol> <p> If you are unable or unwilling to use this
 * table, you may refer to the source of this class to understand how to create a subclass that will better suit your
 * needs. </p>
 *
 * @author Caleb Brinkman
 */
public class JenjinUserSqlLookup<T extends User> implements DatabaseLookup<T>
{
	private static final String PASSWORD_COL = "password";
	private static final String USERNAME_COL = "username";
	private static final String LOGGEDIN_COL = "loggedin";
	private static final String SALT_COL = "salt";
	private static final Logger LOGGER = Logger.getLogger(JenjinUserSqlLookup.class.getName());
	private final UserFactory<T> userFactory;
	private final Connection sqlConnection;

	/**
	 * Construct a new JenjinUserSqlLookup, using the specified {@code UserFactory} to create User instances, and the
	 * specified {@code Connection} to connect to and retrieve data from a SQL database.
	 *
	 * @param userFactory The {@code UserFactory} that will be utilized to get instances of the desired User type.
	 * @param sqlConnection The connection to the SQL database, from which this will retrieve user data.
	 */
	public JenjinUserSqlLookup(UserFactory<T> userFactory, Connection sqlConnection) {
		this.userFactory = userFactory;
		this.sqlConnection = sqlConnection;
	}

	@Override
	public T lookup(String key) throws DatabaseException {
		T user = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			String query = "SELECT * FROM JenjinUsers WHERE username = ?";
			preparedStatement = sqlConnection.prepareStatement(query);
			preparedStatement.setObject(1, key);
			resultSet = preparedStatement.executeQuery();

			user = buildUser(resultSet);
		} catch (SQLException ex) {
			throw new DatabaseException("Exception when retrieving user data from SQL Database: ", ex);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException ex) {
					LOGGER.log(Level.WARNING, "Unable to close prepared statement", ex);
				}
			}
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException ex) {
					LOGGER.log(Level.WARNING, "Unable to close result set", ex);
				}
			}
		}
		if (user == null) {
			LOGGER.log(Level.FINEST, "Attempted to retrieve nonexistant user {0}", key);
		}
		return user;
	}

	private T buildUser(ResultSet resultSet) throws SQLException, DatabaseException {
		T retrievedUser = null;
		if (resultSet.next()) {
			String username = resultSet.getString(USERNAME_COL);
			String password = resultSet.getString(PASSWORD_COL);
			boolean loggedIn = resultSet.getBoolean(LOGGEDIN_COL);
			String salt = resultSet.getString(SALT_COL);

			if (resultSet.next()) {
				throw new DatabaseException("Multiple users with this username detected!");
			}

			retrievedUser = userFactory.createUser(username);
			retrievedUser.setPassword(password);
			retrievedUser.setSalt(salt);
			retrievedUser.setLoggedIn(loggedIn);
		}
		return retrievedUser;
	}
}
