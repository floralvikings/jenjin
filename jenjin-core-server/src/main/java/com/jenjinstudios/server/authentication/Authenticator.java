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
	 * @throws LoginException If the user is already logged in.
	 * @throws com.jenjinstudios.server.database.DbException If there is an error during the database transaction.
	 */
	public T logInUser(String username, String clearTextPassword) throws DbException {
		T user = getUserWithValidPassword(username, clearTextPassword);
		if (user != null)
		{
			if (user.isLoggedIn())
			{
				throw new LoginException("User already logged in.");
			}
			user.setLoggedIn(true);
			if (!userLookup.updateUser(user))
			{
				throw new LoginException("Update was not made to database");
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
	 * @throws DbException If there is an exception when updating the database.
	 */
	public T logOutUser(String username) throws DbException {
		T user = userLookup.findUser(username);
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
				throw new LoginException("Unable to log out user", e);
			}
			if (user.isLoggedIn())
			{
				throw new LoginException("Database was not updated.");
			}
		}
		return user;
	}

	private T getUserWithValidPassword(String username, String password) throws DbException {
		T user = userLookup.findUser(username);
		if (user != null)
		{
			String hashedPassword = SHA256Hasher.getSaltedSHA256String(password, user.getSalt());
			boolean passwordIncorrect = (hashedPassword == null) || !hashedPassword.equalsIgnoreCase(user.getPassword
				  ());

			if (passwordIncorrect)
				user = null;
		}
		return user;
	}

}
