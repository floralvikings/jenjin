package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import com.jenjinstudios.server.database.DatabaseUpdate;
import com.jenjinstudios.server.security.SHA256Hasher;

/**
 * Superclass for all Authenticator classes; used to attempt logins and logouts, and reports success accordingly.
 *
 * @author Caleb Brinkman
 */
public class Authenticator<T extends User>
{
	private final DatabaseLookup<T> userLookup;
	private final DatabaseUpdate<T> userUpdate;

	/**
	 * Construct a new Authenticator using the given UserLookup and UserUpdate to find and update users.
	 * @param lookup The user lookup.
	 * @param update The user update.
	 */
	public Authenticator(DatabaseLookup<T> lookup, DatabaseUpdate<T> update) {
		this.userLookup = lookup;
		this.userUpdate = update;
	}

	/**
	 * Attempt to retrieve the User from the database with the given Username and valid password.
	 *
	 * @param username The username of the user to retrieve.
	 * @param clearTextPassword The cleartext password of the user to retrieve.
	 *
	 * @return The User represented by the backing database with the given username and password.  Returns null if the
	 * user does not exist or the password is invalid.
	 *
	 * @throws AuthenticationException If the user is already logged in.
	 */
	public T logInUser(String username, String clearTextPassword) throws AuthenticationException {
		T user;
		try
		{
			user = getUserWithValidPassword(username, clearTextPassword);
		} catch (DatabaseException e)
		{
			throw new AuthenticationException("Database Exception when looking up user.", e);
		}
		if (user != null)
		{
			if (user.isLoggedIn())
			{
				throw new AuthenticationException("User already logged in.");
			}
			user.setLoggedIn(true);
			try
			{
				if (!userUpdate.update(user))
				{
					user.setLoggedIn(false);
				}
			} catch (DatabaseException e)
			{
				throw new AuthenticationException("Database Exception when updating user.", e);
			}
			if (!user.isLoggedIn())
			{
				throw new AuthenticationException("Database was not updated.");
			}
		}
		return user;
	}

	/**
	 * Attempt to log out the user with the given username.  Note that if a user is already logged out, this method has
	 * no affect.
	 *
	 * @param user The user to be logged out.
	 *
	 * @throws AuthenticationException If there is an exception when updating the database.
	 */
	public void logOutUser(T user) throws AuthenticationException {
		if ((user != null) && user.isLoggedIn())
		{
			user.setLoggedIn(false);
			try
			{
				if (!userUpdate.update(user))
				{
					user.setLoggedIn(true);
				}
			} catch (DatabaseException e)
			{
				user.setLoggedIn(true);
				throw new AuthenticationException("Unable to log out user", e);
			}
			if (user.isLoggedIn())
			{
				throw new AuthenticationException("Database was not updated.");
			}
		}
	}

	private T getUserWithValidPassword(String username, String password) throws DatabaseException {
		T user = userLookup.lookup(username);
		if (user != null)
		{
			String hashedPass = SHA256Hasher.getSaltedSHA256String(password, user.getSalt());
			boolean passwordIncorrect = (hashedPass == null) || !hashedPass.equalsIgnoreCase(user.getPassword());

			if (passwordIncorrect)
				user = null;
		}
		return user;
	}

}
