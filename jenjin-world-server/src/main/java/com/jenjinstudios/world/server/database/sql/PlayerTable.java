package com.jenjinstudios.world.server.database.sql;

import com.jenjinstudios.server.authentication.UserLookup;
import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.server.database.sql.SqlDbTable;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to look up and update players in a database.
 *
 * @author Caleb Brinkman
 */
public class PlayerTable extends SqlDbTable<Player> implements UserLookup<Player>
{
	private static final String USER_COLUMN = "username";
	private static final String USERNAME_COLUMN = "username";
	private static final String LOGGED_IN_COLUMN = "loggedin";
	private static final String PASSWORD_COLUMN = "password";
	private static final String X_COORD_COLUMN = "xCoord";
	private static final String Y_COORD_COLUMN = "yCoord";
	private static final String ZONE_ID_COLUMN = "zoneId";
	private static final String SALT_COLUMN = "salt";
	private static final String USER_TABLE = "jenjin_users";

	/**
	 * Construct a new SqlDbTable with the given SQL {@code Connection} and table name.
	 *
	 * @param connection The {@code Connection} used to communicate with the SQL database.
	 */
	public PlayerTable(Connection connection) {
		super(connection, USER_TABLE);
	}

	@Override
	protected Player buildFromRow(ResultSet resultSet) throws SQLException {
		boolean loggedIn = resultSet.getBoolean(LOGGED_IN_COLUMN);
		String salt = resultSet.getString(SALT_COLUMN);
		String password = resultSet.getString(PASSWORD_COLUMN);
		String username = resultSet.getString(USERNAME_COLUMN);
		double xCoord = resultSet.getDouble(X_COORD_COLUMN);
		double yCoord = resultSet.getDouble(Y_COORD_COLUMN);
		int zoneId = resultSet.getInt(ZONE_ID_COLUMN);
		Vector2D vector2D = new Vector2D(xCoord, yCoord);

		Player player = new Player(username);
		player.setUsername(username);
		player.setPassword(password);
		player.setLoggedIn(loggedIn);
		player.setSalt(salt);
		player.setVector2D(vector2D);
		player.setZoneID(zoneId);
		return player;
	}

	@Override
	protected Map<String, Object> buildFromObject(Player data) {
		Map<String, Object> map = new HashMap<>(10);
		map.put(LOGGED_IN_COLUMN, data.isLoggedIn());
		map.put(SALT_COLUMN, data.getSalt());
		map.put(PASSWORD_COLUMN, data.getPassword());
		map.put(USERNAME_COLUMN, data.getUsername());
		map.put(X_COORD_COLUMN, data.getVector2D().getXCoordinate());
		map.put(Y_COORD_COLUMN, data.getVector2D().getYCoordinate());
		map.put(ZONE_ID_COLUMN, data.getZoneID());
		return map;
	}

	@Override
	public Player findUser(String username) throws DbException {
		Map<String, Object> where = Collections.singletonMap(USER_COLUMN, username);
		List<Player> users = lookup(where);
		return !users.isEmpty() ? users.get(0) : null;
	}

	@Override
	public boolean updateUser(Player user) throws DbException {
		Map<String, Object> where = Collections.singletonMap(USER_COLUMN, user.getUsername());
		return update(where, user);
	}
}
