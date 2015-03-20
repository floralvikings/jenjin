package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DbException;
import com.jenjinstudios.server.security.SHA256Hasher;

/**
 * Superclass for all Authenticator classes; used to attempt logins and logouts, and reports success accordingly.
 *
 * @author Caleb Brinkman
 */
public class Authenticator<T extends User>
{
	private final UserLookup<T> userLookup;

	/**
	 * Construct an Authenticator using the given UserLookup to find and update users.
	 *
	 * @param userLookup The UserLookup used to find and update users.
	 */
	public Authenticator(UserLookup<T> userLookup) { this.userLookup = userLookup; }

	/**
	 * Get the UserLookup used to find and update users.
	 *
	 * @return The UserLookup used to find and update users.
	 */
	public UserLookup<T> getUserLookup() { return userLookup; }

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
		} catch (DbException e)
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
				if (!userLookup.updateUser(user))
				{
					user.setLoggedIn(false);
				}
			} catch (DbException e)
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
	 * @param username The username of the user to be logged out.
	 *
	 * @return The user that was logged out.
	 *
	 * @throws AuthenticationException If there is an exception when updating the database.
	 */
	public T logOutUser(String username) throws AuthenticationException {
		T user;
		try
		{
			user = userLookup.findUser(username);
		} catch (DbException ex)
		{
			throw new AuthenticationException("Exception when accessing authentication database", ex);
		}
		if ((user != null) && user.isLoggedIn())
		{
			user.setLoggedIn(false);
			try
			{
				if (!userLookup.updateUser(user))
				{
					user.setLoggedIn(true);
				}
			} catch (DbException e)
			{
				user.setLoggedIn(true);
				throw new AuthenticationException("Unable to log out user", e);
			}
			if (user.isLoggedIn())
			{
				throw new AuthenticationException("Database was not updated.");
			}
		}
		return user;
	}

	private T getUserWithValidPassword(String username, String password) throws DbException {
		T user = userLookup.findUser(username);
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
