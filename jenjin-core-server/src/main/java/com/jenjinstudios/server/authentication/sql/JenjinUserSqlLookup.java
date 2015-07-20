package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.authentication.UserFactory;
import com.jenjinstudios.server.authentication.UserLookup;
import com.jenjinstudios.server.database.DatabaseException;
import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
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
public class JenjinUserSqlLookup<T extends User> implements UserLookup<T, ResultSet>

{
	private static final String PASSWORD_COL = "password";
	private static final String USERNAME_COL = "username";
	private static final String LOGGEDIN_COL = "loggedin";
	private static final String SALT_COL = "salt";
	private static final Logger LOGGER = Logger.getLogger(JenjinUserSqlLookup.class.getName());
	private final UserFactory<T> userFactory;
	private final Connection connection;

	/**
	 * Construct a new JenjinUserSqlLookup, using the specified {@code UserFactory} to create User instances, and the
	 * specified {@code Connection} to connect to and retrieve data from a SQL database.
	 *
	 * @param userFactory The {@code UserFactory} that will be utilized to get instances of the desired User type.
	 * @param sqlConnection The connection to the SQL database, from which this will retrieve user data.
	 */
	public JenjinUserSqlLookup(UserFactory<T> userFactory, Connection sqlConnection) {
		this.connection = sqlConnection;
		this.userFactory = userFactory;
	}

	@Override
	public ResultSet getDbResults(String key) throws DatabaseException {
		CachedRowSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			String query = "SELECT * FROM JenjinUsers WHERE username = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setObject(1, key);
			ResultSet raw = preparedStatement.executeQuery();
			resultSet = new CachedRowSetImpl();
			resultSet.populate(raw);
		} catch (SQLException ex) {
			throw new DatabaseException("Exception when retrieving user data from SQL Database: ", ex);
		} finally {
			if (preparedStatement != null) {
				try {
					// Closing the statement closes result set.
					preparedStatement.close();
				} catch (SQLException ex) {
					LOGGER.log(Level.WARNING, "Unable to close prepared statement", ex);
				}
			}
		}
		return resultSet;
	}

	@Override
	public T create(ResultSet dbResults) throws DatabaseException {
		T retrievedUser = null;
		try {
			if (dbResults.next()) {
				String username = dbResults.getString(USERNAME_COL);
				String password = dbResults.getString(PASSWORD_COL);
				boolean loggedIn = dbResults.getBoolean(LOGGEDIN_COL);
				String salt = dbResults.getString(SALT_COL);

				if (dbResults.next()) {
					throw new DatabaseException("Multiple users with this username detected!");
				}

				retrievedUser = userFactory.createUser(username);
				retrievedUser.setPassword(password);
				retrievedUser.setSalt(salt);
				retrievedUser.setLoggedIn(loggedIn);
			}
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
		return retrievedUser;
	}

	@Override
	public UserFactory<T> getUserFactory() { return userFactory; }

}
