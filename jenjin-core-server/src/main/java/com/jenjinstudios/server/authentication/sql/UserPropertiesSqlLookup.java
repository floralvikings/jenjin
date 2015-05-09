package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to lookup name-value pairs in the JenjinUserProperties table.
 *
 * @author Caleb Brinkman
 */
public class UserPropertiesSqlLookup implements DatabaseLookup<Map<String, String>>
{
	private static final Logger LOGGER = Logger.getLogger(UserPropertiesSqlLookup.class.getName());
	private final Connection connection;

	/**
	 * Construct a new UserPropertiesLookup that utilizes the given SQL connection, searching for properties with the
	 * for the given username.
	 *
	 * @param connection The SQL connection.
	 */
	public UserPropertiesSqlLookup(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Map<String, String> lookup(String key) throws DatabaseException {
		Map<String, String> properties = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			String query = "SELECT * FROM JenjinUserProperties WHERE username = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setObject(1, key);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				if (properties == null) {
					properties = new HashMap<>(5);
				}
				properties.put(resultSet.getString("propertyName"), resultSet.getString("value"));
			}
		} catch (SQLException ex) {
			throw new DatabaseException("Exception when retrieving properties data from SQL Database: ", ex);
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
		if (properties == null) {
			LOGGER.log(Level.FINEST, "Attempted login with nonexistant properties {0}", key);
		}
		return properties;
	}
}
