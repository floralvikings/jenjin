package com.jenjinstudios.server.authentication;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used for looking up {@code User} objects from a backing SQL database.
 *
 * @author Caleb Brinkman
 */
public class UserTable extends SqlDbTable<BasicUser> implements UserLookup<BasicUser>
{
	private static final String USERNAME_COLUMN = "username";
	private static final String LOGGED_IN_COLUMN = "loggedin";
	private static final String PASSWORD_COLUMN = "password";
	private static final String SALT_COLUMN = "salt";
	private static final String USER_TABLE = "jenjin_users";

	/**
	 * Construct a new SqlDbTable with the given SQL {@code Connection} and table name.
	 *
	 * @param connection The {@code Connection} used to communicate with the SQL database.
	 */
	public UserTable(Connection connection) {
		super(connection, USER_TABLE);
	}

	@Override
	protected BasicUser buildFromRow(ResultSet resultSet) throws SQLException {
		boolean loggedIn = resultSet.getBoolean(LOGGED_IN_COLUMN);
		String salt = resultSet.getString(SALT_COLUMN);
		String password = resultSet.getString(PASSWORD_COLUMN);
		String username = resultSet.getString(USERNAME_COLUMN);

		BasicUser user = new BasicUser(username);
		user.setPassword(password);
		user.setLoggedIn(loggedIn);
		user.setSalt(salt);
		return user;
	}


	@Override
	public BasicUser findUser(String username) throws DbException {
		return lookup(username);
	}

	@Override
	public boolean updateUser(BasicUser user) throws DbException {
		return update(user);
	}
}
