package com.jenjinstudios.server.database.sql;

import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.server.database.IUser;
import com.jenjinstudios.server.database.User;
import com.jenjinstudios.server.database.UserLookup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for looking up {@code User} objects from a backing SQL database.
 *
 * @author Caleb Brinkman
 */
public class UserTable extends SqlDbTable<IUser> implements UserLookup<IUser>
{
	private static final String USER_COLUMN = "username";
	private static final String USERNAME_COLUMN = "username";
	private static final String LOGGED_IN_COLUMN = "loggedin";
	private static final String PASSWORD_COLUMN = "password";
	private static final String SALT_COLUMN = "salt";
	private static final String USER_TABLE = "jenjin_users";

	/**
	 * Construct a new SqlDbTable with the given SQL {@code Connection} and table name.
	 *  @param connection The {@code Connection} used to communicate with the SQL database.
	 *
	 */
	public UserTable(Connection connection) {
		super(connection, USER_TABLE);
	}

	@Override
	protected IUser buildFromRow(ResultSet resultSet) throws SQLException {
		boolean loggedIn = resultSet.getBoolean(LOGGED_IN_COLUMN);
		String salt = resultSet.getString(SALT_COLUMN);
		String password = resultSet.getString(PASSWORD_COLUMN);
		String username = resultSet.getString(USERNAME_COLUMN);

		IUser user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setLoggedIn(loggedIn);
		user.setSalt(salt);
		return user;
	}

	@Override
	protected Map<String, Object> buildFromObject(IUser data) {
		Map<String, Object> map = new HashMap<>(10);
		map.put(LOGGED_IN_COLUMN, data.isLoggedIn());
		map.put(SALT_COLUMN, data.getSalt());
		map.put(PASSWORD_COLUMN, data.getPassword());
		map.put(USERNAME_COLUMN, data.getUsername());
		return map;
	}


	@Override
	public IUser findUser(String username) throws DbException {
		Map<String, Object> where = Collections.singletonMap(USER_COLUMN, username);
		List<IUser> users = lookup(where);
		return !users.isEmpty() ? users.get(0) : null;
	}

	@Override
	public boolean updateUser(IUser user) throws DbException {
		Map<String, Object> where = Collections.singletonMap(USER_COLUMN, user.getUsername());
		return update(where, user);
	}
}
