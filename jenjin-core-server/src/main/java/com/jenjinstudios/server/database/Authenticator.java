package com.jenjinstudios.server.database;

import com.jenjinstudios.server.database.sql.UserTable;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.security.SHA256Hasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class Authenticator
{
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
	private static final String USER_TABLE = "jenjin_users";
	private static final String PROPERTIES_TABLE = "jenjin_user_properties";
	private static final String USER_COLUMN = "username";
	private static final String PROPERTY_NAME_COLUMN = "propertyName";
	private static final String PROPERTY_VALUE_COLUMN = "propertyValue";
	/** The connection used to communicate with the SQL database. */
	private final Connection dbConnection;
	private final String propertiesQuery;
	private final UserTable userTable;

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public Authenticator(Connection dbConnection) {
		propertiesQuery = "SELECT * FROM " + PROPERTIES_TABLE + " WHERE username = ?";
		this.dbConnection = dbConnection;
		userTable = new UserTable(this.dbConnection, USER_TABLE);
	}

	/**
	 * Attempt to log the given user with the given password into the database.  This method does not perform any sort
	 * of hashing or encryption on the password.  If the user is already logged in this method will return false.
	 * <p>
	 * This method should be overwritten by implementations, or called from super if they still wish to use the
	 * "loggedIn" column.
	 *
	 * @return true if the user was logged in successfully, false if the user was already logged in or the update to
	 * the
	 * database failed.
	 */
	public User logInUser(String username, String password) throws LoginException {
		User user = getUserWithValidPassword(username, password);
		if (user.isLoggedIn())
		{
			throw new LoginException("User already logged in.");
		}
		user.setLoggedIn(true);
		updateLoggedinColumn(username, user);
		return user;
	}

	private User getUserWithValidPassword(String username, String password) throws LoginException {
		User user = userTable.findUser(username);
		if (user == null)
			throw new LoginException("User " + username + " does not exist.");
		if (user.isLoggedIn())
			throw new LoginException("User " + username + " is already logged in.");
		String hashedPassword = SHA256Hasher.getSaltedSHA256String(password, user.getSalt());
		boolean passwordIncorrect = (hashedPassword == null) || !hashedPassword.equalsIgnoreCase(user.getPassword());
		if (passwordIncorrect)
			throw new LoginException("User " + username + " provided incorrect password.");
		return user;
	}

	public User lookUpUser(String username) throws LoginException {
		return userTable.findUser(username);
	}

	public Map<String, Object> lookUpUserProperties(String username) throws LoginException {
		Map<String, Object> properties = new HashMap<>(10);

		try (ResultSet results = makePropertiesQuery(username))
		{
			while (results.next())
			{
				String propertyName = results.getString(PROPERTY_NAME_COLUMN);
				Object propertyValue = results.getObject(PROPERTY_VALUE_COLUMN);
				properties.put(propertyName, propertyValue);
			}
		} catch (SQLException e)
		{
			throw new LoginException("Unable to retrieve" + username + " properties because of SQL Exception.", e);
		}

		return properties;
	}

	/**
	 * Attempt to log out the user with the given username.  Note that if a user is already logged out, this method
	 * will
	 * have no affect.
	 *
	 * @param username The username of the user to be logged out.
	 *
	 * @return The user that was logged out.
	 */
	public User logOutUser(String username) throws LoginException {
		User user = userTable.findUser(username);
		if ((user != null) && user.isLoggedIn())
		{
			user.setLoggedIn(false);
			updateLoggedinColumn(username, user);
		}
		return user;
	}

	protected ResultSet makePropertiesQuery(String username) throws SQLException {
		PreparedStatement statement;
		synchronized (dbConnection)
		{
			statement = dbConnection.prepareStatement(propertiesQuery, TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE);
			statement.setString(1, username);
		}
		return statement.executeQuery();
	}

	/**
	 * Update the loggedin column to reflect the supplied boolean.
	 *
	 * @param username The user being queried.
	 * @throws LoginException If there is a SQL error.
	 */
	protected void updateLoggedinColumn(String username, User user) throws LoginException {
		Map<String, Object> where = Collections.singletonMap("username", username);
		userTable.update(where, user);
	}

	public Object lookUpUserProperty(String username, String propertyName) throws SQLException {
		Object r = null;
		synchronized (dbConnection)
		{
			String propertyQuery = "SELECT * FROM " + PROPERTIES_TABLE + ' ' +
				  "WHERE " + USER_COLUMN + " = ? AND " + PROPERTY_NAME_COLUMN + " = ?";
			PreparedStatement statement = dbConnection.prepareStatement(propertyQuery);
			statement.setString(1, username);
			statement.setString(2, propertyName);
			ResultSet results = statement.executeQuery();
			if (results.next())
			{
				r = results.getObject(PROPERTY_VALUE_COLUMN);
			}
		}
		return r;
	}

	protected void updateUserProperties(User user) throws SQLException {
		Map<String, Object> properties = user.getProperties();
		for (Entry<String, Object> stringObjectEntry : properties.entrySet())
		{
			Object value = stringObjectEntry.getValue();
			if ((value == null) || isWrapperType(value.getClass()))
			{
				Object existing = lookUpUserProperty(user.getUsername(), stringObjectEntry.getKey());
				if (existing == null)
				{
					insertUserProperty(user.getUsername(), stringObjectEntry.getKey(), value);
				} else if (!existing.equals(value))
				{
					updateUserProperty(user.getUsername(), stringObjectEntry.getKey(), value);
				}
			}
		}
	}

	private static boolean isWrapperType(Class<?> clazz) { return WRAPPER_TYPES.contains(clazz); }

	private static Set<Class<?>> getWrapperTypes() {
		Set<Class<?>> ret = new HashSet<>(10);
		ret.add(String.class);
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);
		return ret;
	}

	private void insertUserProperty(String username, String propertyName, Object propertyValue) throws SQLException {
		synchronized (dbConnection)
		{
			String insertPropertyQuery = "INSERT INTO " + PROPERTIES_TABLE + ' ' +
				  "(`" + USER_COLUMN + "`, `" + PROPERTY_NAME_COLUMN + "`, `" + PROPERTY_VALUE_COLUMN + "`) VALUES " +
				  "(?, ?, ?)";
			PreparedStatement statement = dbConnection.prepareStatement(insertPropertyQuery);
			statement.setString(1, username);
			statement.setString(2, propertyName);
			statement.setObject(3, propertyValue);
			statement.executeUpdate();
			statement.close();
		}
	}

	private void updateUserProperty(String username, String propertyName, Object propertyValue) throws SQLException {
		synchronized (dbConnection)
		{
			String updatePropertyQuery = "UPDATE " + PROPERTIES_TABLE + " SET " + PROPERTY_VALUE_COLUMN + " = ? " +
				  "WHERE" + ' ' + USER_COLUMN + " = ? AND " + PROPERTY_NAME_COLUMN + " = ?";
			PreparedStatement statement = dbConnection.prepareStatement(updatePropertyQuery);
			statement.setObject(1, propertyValue);
			statement.setString(2, username);
			statement.setObject(3, propertyName);
			statement.executeUpdate();
			statement.close();
		}
	}
}
