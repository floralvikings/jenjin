package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.server.database.Authenticator;
import com.jenjinstudios.server.database.LoginException;
import com.jenjinstudios.server.database.User;
import com.jenjinstudios.server.database.sql.UserTable;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.server.WorldClientHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends Authenticator
{
	/** The column name of the X coordinate. */
	private static final String X_COORD = "xCoord";
	/** The column name of the Y coordinate. */
	private static final String Y_COORD = "yCoord";
	/** The column name of the zone ID. */
	private static final String ZONE_ID = "zoneID";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldAuthenticator.class.getName());
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
	private static final String PROPERTIES_TABLE = "jenjin_user_properties";
	private static final String USER_COLUMN = "username";
	private static final String PROPERTY_NAME_COLUMN = "propertyName";
	private static final String PROPERTY_VALUE_COLUMN = "propertyValue";
	private final Connection dbConnection;
	private final String propertiesQuery;

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public WorldAuthenticator(Connection connection) {
		super(new UserTable(connection));
		this.dbConnection = connection;
		propertiesQuery = "SELECT * FROM " + PROPERTIES_TABLE + " WHERE username = ?";
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

	public void updatePlayer(WorldClientHandler worldClientHandler) {
		Actor player = worldClientHandler.getPlayer();
		User user = worldClientHandler.getUser();

		user.getProperties().put(X_COORD, player.getVector2D().getXCoordinate());
		user.getProperties().put(Y_COORD, player.getVector2D().getYCoordinate());
		user.getProperties().put(ZONE_ID, player.getZoneID());

		try
		{
			updateUserProperties(user);
		} catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to update player information in database!", e);
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

	protected ResultSet makePropertiesQuery(String username) throws SQLException {
		PreparedStatement statement;
		synchronized (dbConnection)
		{
			statement = dbConnection.prepareStatement(propertiesQuery, TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE);
			statement.setString(1, username);
		}
		return statement.executeQuery();
	}

	protected void updateUserProperties(User user) throws SQLException {
		Map<String, Object> properties = user.getProperties();
		for (Map.Entry<String, Object> stringObjectEntry : properties.entrySet())
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
