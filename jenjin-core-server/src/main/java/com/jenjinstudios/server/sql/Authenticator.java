package com.jenjinstudios.server.sql;

import com.jenjinstudios.server.net.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.sql.ResultSet.*;

/**
 * The SQLHandler class is responsible for connecting to and querying the SQL database associated with a given Server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SameParameterValue")
public class Authenticator
{
    public static final int HEX_CONVERSION_CONSTANT = 0xff;
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    private static final String USER_TABLE = "jenjin_users";
    private static final String PROPERTIES_TABLE = "jenjin_user_properties";
    private static final String SALT = "salt";
    private static final String PASSWORD = "password";
    private static final String USER = "username";
    private static final String PROPERTY_NAME = "propertyName";
    private static final String PROPERTY_VALUE = "propertyValue";
    /** The name of the column in the user table specifying whether the user is currently logged in. */
    private static final String LOGGED_IN = "loggedin";
    /** The connection used to communicate with the SQL database. */
    private final Connection dbConnection;
    /** The string used to get all information about the user. */
    private final String USER_QUERY;
    private final String PROPERTIES_QUERY;

    /**
     * Create a new SQLHandler with the given database information, and connect to the database.
     */
    public Authenticator(Connection dbConnection) {
        USER_QUERY = "SELECT * FROM " + USER_TABLE + " WHERE username = ?";
        PROPERTIES_QUERY = "SELECT * FROM " + PROPERTIES_TABLE + " WHERE username = ?";
        this.dbConnection = dbConnection;
    }

    private static String getSHA256String(String input) {
        try
        {
            //Convert the pass to an md5 hash string
            return getFullHexString(getSHA256Hash(input));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
        {
			throw new RuntimeException("Unable to find SHA-256 Algorithm", ex);
		}
    }

    private static byte[] getSHA256Hash(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] passBytes = input.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(passBytes);
    }

    private static String getFullHexString(byte[] bytes) {
        String hashedString;
        StringBuilder hexString = new StringBuilder();
        for (byte anEncryption : bytes)
        { // Convert back to a string, making sure to include leading zeros.
            String hex = Integer.toHexString(HEX_CONVERSION_CONSTANT & anEncryption);
            if (hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        hashedString = hexString.toString();
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
    public static String getSaltedSHA256String(String input, String salt) { return getSHA256String(salt + input); }

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
		boolean passwordCorrect = (hashedPassword != null) && hashedPassword.equalsIgnoreCase(user.getPassword());
		if (!passwordCorrect)
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
            String salt = results.getString(SALT);
            String dbPass = results.getString(PASSWORD);
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
                String propertyName = results.getString(PROPERTY_NAME);
                Object propertyValue = results.getObject(PROPERTY_VALUE);
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
            statement = dbConnection.prepareStatement(USER_QUERY,
                  TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE);
            statement.setString(1, username);

        }
        return statement.executeQuery();
    }

    protected ResultSet makePropertiesQuery(String username) throws SQLException {
        PreparedStatement statement;
        synchronized (dbConnection)
        {
            statement = dbConnection.prepareStatement(PROPERTIES_QUERY, TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE);
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
        String updateQuery = "UPDATE " + USER_TABLE + " SET " + LOGGED_IN + "=" + s + " WHERE " + USER + " = ?";
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
                      "column.");
            }
        }
    }

    public Object lookUpUserProperty(String username, String propertyName) throws SQLException {
        String propertyQuery = "SELECT * FROM " + PROPERTIES_TABLE + " " +
              "WHERE " + USER + " = ? AND " + PROPERTY_NAME + " = ?";
        Object r = null;
        synchronized (dbConnection)
        {
            PreparedStatement statement = dbConnection.prepareStatement(propertyQuery);
            statement.setString(1, username);
            statement.setString(2, propertyName);
            ResultSet results = statement.executeQuery();
            if (results.next())
            {
                r = results.getObject(PROPERTY_VALUE);
            }
        }
        return r;
    }

    protected void updateUserProperties(User user) throws SQLException {
		Map<String, Object> properties = user.getProperties();
		for (String name : properties.keySet())
        {
            Object value = properties.get(name);
			if ((value == null) || isWrapperType(value.getClass()))
			{
				Object existing = lookUpUserProperty(user.getUsername(), name);
				if (existing == null)
				{
					insertUserProperty(user.getUsername(), name, value);
				} else if (!existing.equals(value))
				{
					updateUserProperty(user.getUsername(), name, value);
				}
			}
		}
	}

    public static boolean isWrapperType(Class<?> clazz) { return WRAPPER_TYPES.contains(clazz); }

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
        String insertPropertyQuery = "INSERT INTO " + PROPERTIES_TABLE + " " +
              "(`" + USER + "`, `" + PROPERTY_NAME + "`, `" + PROPERTY_VALUE + "`) VALUES " +
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
        String updatePropertyQuery = "UPDATE " + PROPERTIES_TABLE + " SET " + PROPERTY_VALUE + " = ? WHERE " +
              USER + " = ? AND " + PROPERTY_NAME + " = ?";
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
