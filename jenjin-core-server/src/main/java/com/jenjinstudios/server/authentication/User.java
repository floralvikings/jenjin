package com.jenjinstudios.server.authentication;

/**
 * Interface for all objects representing a user in the database.
 *
 * @author Caleb Brinkman
 */
public interface User
{
	/**
	 * Get the <b>unique</b> username associated with this User.
	 *
	 * @return The <b>unique</b> username associated with this User.
	 */
	String getUsername();

	/**
	 * Set the username associated with this user.  This username must be unique; duplicate usernames can restult in
	 * user data being overwritten in the database.
	 *
	 * @param username The uesrname associated with this user.  <b>Must be unique</b>.
	 */
	void setUsername(String username);

	/**
	 * Get the hashed, salted password of this user.
	 *
	 * @return The hashed, salted password of this user.
	 */
	String getPassword();

	/**
	 * Set the hashed, salted password of this user.
	 *
	 * @param password The hashed, salted password of this user.
	 */
	void setPassword(String password);

	/**
	 * Get the salt used in securing this user's password in the database.
	 *
	 * @return The salt used in securing this user's password in the database.
	 */
	String getSalt();

	/**
	 * Set the salt used in securing this user's password in the database.
	 *
	 * @param salt The salt.
	 */
	void setSalt(String salt);

	/**
	 * Get whether this user is currently logged in.
	 *
	 * @return Whether this user is currently logged in.
	 */
	boolean isLoggedIn();

	/**
	 * Set whether this user is currently logged in.
	 *
	 * @param loggedIn Whether this user is currently logged in.
	 */
	void setLoggedIn(boolean loggedIn);
}
