package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
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
public class UserPropertiesSqlLookup implements DatabaseLookup<Map<String, String>, ResultSet>
{
	private static final Logger LOGGER = Logger.getLogger(UserPropertiesSqlLookup.class.getName());
	private final Connection connection;

    /** Used by Gson. */
    private UserPropertiesSqlLookup() { this(null); }

	/**
	 * Construct a new UserPropertiesLookup that utilizes the given SQL connection, searching for properties with the
	 * for the given username.
	 *
	 * @param connection The SQL connection string.
	 */
	public UserPropertiesSqlLookup(Connection connection) {
		this.connection = connection;
	}

	@Override
	public ResultSet getDbResults(String key) throws DatabaseException {
		CachedRowSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			String query = "SELECT * FROM JenjinUserProperties WHERE username = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setObject(1, key);
			ResultSet raw = preparedStatement.executeQuery();
            resultSet = RowSetProvider.newFactory().createCachedRowSet();
            resultSet.populate(raw);
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
		}
		return resultSet;
	}

	@Override
	public Map<String, String> create(ResultSet dbResults) throws DatabaseException {
		Map<String, String> properties = null;
		try {
			while (dbResults.next()) {
				if (properties == null) {
					properties = new HashMap<>(5);
				}
				properties.put(dbResults.getString("propertyName"), dbResults.getString("value"));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
		return properties;
	}

}
