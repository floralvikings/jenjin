package com.jenjinstudios.server.authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

/**
 * Used to make database lookups against SQL databases.  An attempt has been made to keep this class as database-
 * agnostic as possible;  assuming the supplied {@code Connection} is a valid SQL connection, this should "just work".
 *
 * @author Caleb Brinkman
 */
public abstract class SqlDbTable<T extends User> implements DbTable<T>
{
	private static final Logger LOGGER = Logger.getLogger(SqlDbTable.class.getName());
	private final Connection connection;
	private final String tableName;

	/**
	 * Construct a new SqlDbTable with the given SQL {@code Connection} and table name.
	 *
	 * @param connection The {@code Connection} used to communicate with the SQL database.
	 * @param tableName The name of the table to query.
	 */
	public SqlDbTable(Connection connection, String tableName) {
		this.connection = connection;
		this.tableName = tableName;
	}

	/**
	 * Given a result set from the backing database, build out the type-correct return value from that result.  This
	 * method is used by the {@code lookup} method, and must be implemented.
	 *
	 * @param resultSet The results from the backing database.
	 *
	 * @return A {@code T} built from the result set.
	 *
	 * @throws java.sql.SQLException If there is an exception when querying the result set.
	 */
	protected abstract T buildFromRow(ResultSet resultSet) throws SQLException;

	@Override
	public T lookup(String username) throws DbException {
		T lookup = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try
		{
			statement = getLookupStatement(username);
			resultSet = statement.executeQuery();
			if (resultSet.next())
			{
				lookup = buildFromRow(resultSet);
				// Should only be one result; will throw a DbException if multiples show up
				boolean multipleResults = resultSet.next();
				resultSet.close();
				if (multipleResults)
				{
					throw new DbException("Multiple users with this username exist: " + username);
				}
			}
		} catch (SQLException e)
		{
			throw new DbException("SQL Exception when querying database: ", e);
		} finally
		{
			if (statement != null)
			{
				try
				{
					statement.close();
				} catch (SQLException ex)
				{
					LOGGER.log(Level.WARNING, "SQL Exception when closing statement: ", ex);
				}
			}
			if (resultSet != null)
			{
				try
				{
					resultSet.close();
				} catch (SQLException ex)
				{
					LOGGER.log(Level.WARNING, "SQL Exception when closing ResultSet: ", ex);
				}
			}
		}
		return lookup;
	}

	@Override
	public boolean update(T user) throws DbException {
		PreparedStatement statement = null;
		boolean success = false;
		try
		{
			statement = getUpdateStatement(user);
			success = statement.executeUpdate() > 0;
		} catch (SQLException e)
		{
			throw new DbException("SQL Exception when querying database", e);
		} finally
		{
			if (statement != null)
			{
				try
				{
					statement.close();
				} catch (SQLException ex)
				{
					LOGGER.log(Level.WARNING, "SQL Exception when closing statement: ", ex);
				}
			}
		}

		return success;
	}

	private PreparedStatement getLookupStatement(String username) throws SQLException {
		String query = "SELECT * FROM " + tableName + " WHERE username = ?";
		PreparedStatement statement;
		synchronized (connection)
		{
			statement = connection.prepareStatement(query, TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE);
			statement.setObject(1, username);
		}
		return statement;
	}

	private PreparedStatement getUpdateStatement(T user)
		  throws SQLException
	{
		String query = "UPDATE " + tableName + " SET loggedin = ? WHERE username = ?";


		PreparedStatement statement;
		synchronized (connection)
		{
			statement = connection.prepareStatement(query, TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE);
			statement.setObject(1, user.isLoggedIn());
			statement.setObject(2, user.getUsername());
		}
		return statement;
	}
}
