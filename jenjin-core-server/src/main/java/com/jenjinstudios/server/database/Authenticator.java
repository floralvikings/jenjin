package com.jenjinstudios.server.database;

import com.jenjinstudios.server.security.SHA256Hasher;

/**
 * The {@code Authenticator} class is responsible for retrieving and updating users in the database specified by a
 * supplied JDBC {@code Connection}.
 */
public class Authenticator
{
	private final UserLookup userLookup;

	/**
	 * Construct a new Authenticator with the given database Connection and UserLookup.
	 *
	 * @param userLookup The UserLookup used to find and update users.
	 */
	public Authenticator(UserLookup userLookup) {
		this.userLookup = userLookup;
	}

	/**
	 * Attempt to retrieve the User from the database with the given Username and valid password.
	 *
	 * @param username The username of the user to retrieve.
	 * @param password The cleartext password of the user to retrieve.
	 *
	 * @return The User represented by the backing database with the given username and password.  Returns null if the
	 * user does not exist or the password is invalid.
	 *
	 * @throws LoginException If the user is already logged in.
	 * @throws DbException If there is an error during the database transaction.
	 */
	public User logInUser(String username, String password) throws DbException {
		User user = getUserWithValidPassword(username, password);
		if (user != null)
		{
			if (user.isLoggedIn())
			{
				throw new LoginException("User already logged in.");
			}
			user.setLoggedIn(true);
			userLookup.updateUser(user);
		}
		return user;
	}

	/**
	 * Get the UserTable used by this Authenticator to make queries.
	 *
	 * @return The UserTable.
	 */
	public UserLookup getUserLookup() { return userLookup; }

	private User getUserWithValidPassword(String username, String password) throws DbException {
		User user = userLookup.findUser(username);
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
	public IUser logOutUser(String username) throws DbException {
		IUser user = userLookup.findUser(username);
		if ((user != null) && user.isLoggedIn())
		{
			user.setLoggedIn(false);
			userLookup.updateUser(user);
		}
		return user;
	}

}
