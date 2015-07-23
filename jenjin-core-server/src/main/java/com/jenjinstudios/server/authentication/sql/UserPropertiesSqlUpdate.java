package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Used to update name-value pairs in the JenjinUserProperties table.
 *
 * @author Caleb Brinkman
 */
public class UserPropertiesSqlUpdate implements DatabaseUpdate<Map<String, String>>
{
	private final Connection connection;

    /** Used by Gson. */
    private UserPropertiesSqlUpdate() { this(null); }

	/**
	 * Construct a new UserPropertiesSqlUpdate instance which will make updates to the given SQL connection.
	 *
	 * @param connection The connection to the SQL database that will be updated by this instance.
	 */
	public UserPropertiesSqlUpdate(Connection connection) { this.connection = connection; }

	@Override
	public boolean update(Map<String, String> object) throws DatabaseException {
		throw new DatabaseException("Username not provided for properties lookup, use the overloaded form of this " +
			  "method instead.");
	}

	@Override
	public boolean update(Map<String, String> object, String... secondaryKeys) throws DatabaseException {
		if (secondaryKeys.length == 0) {
			throw new DatabaseException("Username not provided for properties lookup");
		}
		String username = secondaryKeys[0];

		boolean changesMade = false;
		String query = "UPDATE JenjinUserProperties SET value = ? WHERE username = ? AND propertyName = ?";
		for (Entry<String, String> entry : object.entrySet()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setObject(1, entry.getValue());
				preparedStatement.setObject(2, username);
				preparedStatement.setObject(3, entry.getKey());
				changesMade |= preparedStatement.executeUpdate() > 0;
			} catch (SQLException ex) {
				throw new DatabaseException(ex);
			}
		}

		return changesMade;
	}

}
