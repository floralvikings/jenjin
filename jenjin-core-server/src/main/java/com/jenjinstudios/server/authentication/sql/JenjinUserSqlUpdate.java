package com.jenjinstudios.server.authentication.sql;

import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used to update users in SQL database; specifically, from a table called <b>JenjinUsers</b>. This table must have (at
 * minimum) the following columns: <ol> <li> {@code username} - The column used to store usernames.  This should be a
 * unique key, stored as a text type. </li> <li> {@code password} - The column used to store passwords.  These should be
 * the user's password prepended with the value in the {@code salt} column and then hashed with a SHA-256 hashing
 * scheme. Should be a text type. </li> <li> {@code salt} - The value prepended to the user's password before it is
 * hashed with a SHA-256 hashing scheme.  Should be a text type.</li> <li> {@code loggedin} - The value that indicated
 * whether a user is logged in; should be a boolean type. </li> </ol> <p> If you are unable or unwilling to use this
 * table, you may refer to the source of this class to understand how to create a subclass that will better suit your
 * needs. </p>
 */
public class JenjinUserSqlUpdate<T extends User> implements DatabaseUpdate<T>
{
    private final Connection connection;

    /** Used by Gson. */
    private JenjinUserSqlUpdate() { this(null); }

	/**
	 * Construct a new {@code JenjinUserSqlUpdate} instance which will make updates to the SQL database on the supplied
	 * database connection.
	 *
     * @param connection The {@link Connection} that will be used to make updates to the database.
     */
    public JenjinUserSqlUpdate(Connection connection) {
        this.connection = connection;
    }

	@Override
	public boolean update(T object) throws DatabaseException {
		String query = "UPDATE JenjinUsers SET loggedin = ?, password = ?, salt = ? WHERE username = ?";
		boolean changesMade;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, object.isLoggedIn());
            preparedStatement.setObject(2, object.getPassword());
			preparedStatement.setObject(3, object.getSalt());
			preparedStatement.setObject(4, object.getUsername());
			changesMade = preparedStatement.executeUpdate() > 0;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
		return changesMade;
	}
}
