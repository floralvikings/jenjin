package com.jenjinstudios.server.database.sql;

import com.jenjinstudios.server.database.SqlDbTable;
import com.jenjinstudios.server.net.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used for looking up {@code User} objects from a backing SQL database.
 *
 * @author Caleb Brinkman
 */
public class UserTable extends SqlDbTable<User>
{
	private static final String USERNAME_COLUMN = "username";
	private static final String LOGGED_IN_COLUMN = "loggedin";
	private static final String PASSWORD_COLUMN = "password";
	private static final String SALT_COLUMN = "salt";

	/**
	 * Construct a new SqlDbTable with the given SQL {@code Connection} and table name.
	 *
	 * @param connection The {@code Connection} used to communicate with the SQL database.
	 * @param tableName The name of the table to query.
	 */
	public UserTable(Connection connection, String tableName) {
		super(connection, tableName);
	}

	@Override
	public String getPrimaryKeyColumn() { return USERNAME_COLUMN; }

	@Override
	protected User buildFromRow(ResultSet resultSet) throws SQLException {
		boolean loggedIn = resultSet.getBoolean(LOGGED_IN_COLUMN);
		String salt = resultSet.getString(SALT_COLUMN);
		String password = resultSet.getString(PASSWORD_COLUMN);
		String username = resultSet.getString(USERNAME_COLUMN);

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setLoggedIn(loggedIn);
		user.setSalt(salt);
		return user;
	}
}
