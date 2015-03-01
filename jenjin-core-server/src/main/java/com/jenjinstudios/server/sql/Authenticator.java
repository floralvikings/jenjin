package com.jenjinstudios.server.sql;

import com.jenjinstudios.server.net.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.*;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class Authenticator
{
	private static final int HEX_CONVERSION_CONSTANT = 0xff;
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    private static final String USER_TABLE = "jenjin_users";
    private static final String PROPERTIES_TABLE = "jenjin_user_properties";
	private static final String SALT_COLUMN = "salt";
	private static final String PASSWORD_COLUMN = "password";
	private static final String USER_COLUMN = "username";
	private static final String PROPERTY_NAME_COLUMN = "propertyName";
	private static final String PROPERTY_VALUE_COLUMN = "propertyValue";
	/** The name of the column in the user table specifying whether the user is currently logged in. */
    private static final String LOGGED_IN = "loggedin";
	private static final int SHA256_STRING_LENGTH = 64;
	private static final Logger LOGGER = Logger.getLogger(Authenticator.class.getName());
	/** The connection used to communicate with the SQL database. */
	private final Connection dbConnection;
    /** The string used to get all information about the user. */
	private final String userQuery;
	private final String propertiesQuery;

    /**
     * Create a new SQLHandler with the given database information, and connect to the database.
     */
    public Authenticator(Connection dbConnection) {
		userQuery = "SELECT * FROM " + USER_TABLE + " WHERE username = ?";
		propertiesQuery = "SELECT * FROM " + PROPERTIES_TABLE + " WHERE username = ?";
		this.dbConnection = dbConnection;
    }

    private static String getSHA256String(String input) {
            return getFullHexString(getSHA256Hash(input));
    }

	private static byte[] getSHA256Hash(String input) {
		byte[] passBytes = input.getBytes();
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			passBytes = md.digest(passBytes);
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "No SHA-256 algorithm found; are you using a valid Java implementation?");
			// FIXME This could be a pretty big security issue for non-compliant JVMs
			// maybe fall back to a custom SHA-256 implementation?
		}
		return passBytes;
	}

	private static String getFullHexString(byte... bytes) {
		StringBuilder hexString = new StringBuilder(SHA256_STRING_LENGTH);
		for (byte anEncryption : bytes)
        { // Convert back to a string, making sure to include leading zeros.
            String hex = Integer.toHexString(HEX_CONVERSION_CONSTANT & anEncryption);
            if (hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
		String hashedString = hexString.toString();
		return hashedString;
	}

    /**
     * Get a SHA-256 hash of the {@code String} created by combining {@code salt} and {@code hash}.
     *
     * @param input The string to be salted and hashed.
     * @param salt The salt to prepend to the string before hashing.
     *
     * @return The hashed, salted string.
     */
	private static String getSaltedSHA256String(String input, String salt) { return getSHA256String(salt + input); }

    /**
     * Attempt to log the given user with the given password into the database.  This method does not perform any sort
     * of hashing or encryption on the password.  If the user is already logged in this method will return false.
     * <p>
     * This method should be overwritten by implementations, or called from super if they still wish to use the
     * "loggedIn" column.
     *
     * @return true if the user was logged in successfully, false if the user was already logged in or the update to the
     * database failed.
     */
    public User logInUser(String username, String password) throws LoginException {
        User user = getUserWithValidPassword(username, password);
        updateLoggedinColumn(username, true);
        user.setLoggedIn(true);
        return user;
    }

    private User getUserWithValidPassword(String username, String password) throws LoginException {
        User user = lookUpUser(username);
        if (user.isLoggedIn())
            throw new LoginException("User " + username + " is already logged in.");
        String hashedPassword = getSaltedSHA256String(password, user.getSalt());
		boolean passwordIncorrect = (hashedPassword == null) || !hashedPassword.equalsIgnoreCase(user.getPassword());
		if (passwordIncorrect)
			throw new LoginException("User " + username + " provided incorrect password.");
        return user;
    }

    public User lookUpUser(String username) throws LoginException {
        try (ResultSet results = makeUserQuery(username))
        {
            if (!results.next())
            {
                throw new LoginException("User " + username + " does not exist.");
            }
            boolean loggedIn = results.getBoolean(LOGGED_IN);
			String salt = results.getString(SALT_COLUMN);
			String dbPass = results.getString(PASSWORD_COLUMN);
			User user = new User();
            user.setUsername(username);
            user.setPassword(dbPass);
            user.setSalt(salt);
            user.setLoggedIn(loggedIn);
            return user;
        } catch (SQLException e)
        {
            throw new LoginException("Unable to retrieve user " + username + " because of SQL Exception.", e);
        }
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
     * Attempt to log out the user with the given username.  Note that if a user is already logged out, this method will
     * have no affect.
     *
     * @param username The username of the user to be logged out.
     *
     * @return The user that was logged out.
     */
    public User logOutUser(String username) throws LoginException {
        User user = lookUpUser(username);
        if (user.isLoggedIn())
        {
            user.setLoggedIn(false);
            updateLoggedinColumn(username, false);
        }
        return user;
    }

    /**
     * Query the database for user info.
     *
     * @param username The username of the user we're looking for.
     *
     * @return The ResultSet returned by the query.
     *
     * @throws SQLException If there is a SQL error.
     */
    protected ResultSet makeUserQuery(String username) throws SQLException {
        PreparedStatement statement;
        synchronized (dbConnection)
        {
			statement = dbConnection.prepareStatement(userQuery,
				  TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
            statement.setString(1, username);

        }
        return statement.executeQuery();
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
     * @param status The new status of the loggedin column.
     *
     * @throws com.jenjinstudios.server.sql.LoginException If there is a SQL error.
     */
    protected void updateLoggedinColumn(String username, boolean status) throws LoginException {
        String s = status ? "1" : "0";
		String updateQuery = "UPDATE " + USER_TABLE + " SET " + LOGGED_IN + '=' + s + " WHERE " + USER_COLUMN + " = ?";
		synchronized (dbConnection)
        {
            try (PreparedStatement updateLoggedIn = dbConnection.prepareStatement(updateQuery))
            {
                updateLoggedIn.setString(1, username);
                updateLoggedIn.executeUpdate();
                updateLoggedIn.close();
            } catch (SQLException e)
            {
                throw new LoginException("Unable to update " + username + "; SQLException when updating loggedin " +
					  "column.", e);
			}
        }
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
		String insertPropertyQuery = "INSERT INTO " + PROPERTIES_TABLE + ' ' +
			  "(`" + USER_COLUMN + "`, `" + PROPERTY_NAME_COLUMN + "`, `" + PROPERTY_VALUE_COLUMN + "`) VALUES " +
			  "(?, ?, ?)";
        synchronized (dbConnection)
        {
            PreparedStatement statement = dbConnection.prepareStatement(insertPropertyQuery);
            statement.setString(1, username);
            statement.setString(2, propertyName);
            statement.setObject(3, propertyValue);
            statement.executeUpdate();
            statement.close();
        }
    }

    private void updateUserProperty(String username, String propertyName, Object propertyValue) throws SQLException {
		String updatePropertyQuery = "UPDATE " + PROPERTIES_TABLE + " SET " + PROPERTY_VALUE_COLUMN + " = ? WHERE " +
			  USER_COLUMN + " = ? AND " + PROPERTY_NAME_COLUMN + " = ?";
		synchronized (dbConnection)
        {
            PreparedStatement statement = dbConnection.prepareStatement(updatePropertyQuery);
            statement.setObject(1, propertyValue);
            statement.setString(2, username);
            statement.setObject(3, propertyName);
            statement.executeUpdate();
            statement.close();
        }
    }
}
