package com.jenjinstudios.world;

/**
 * The player class represents a player in the server-side world.
 * @author Caleb Brinkman
 */
public class Player extends Actor
{
	/**
	 * Construct a player with the given username.
	 * @param username The username.
	 */
	public Player(String username) {
		super(username);
	}

	/**
	 * Construct a player with the given username and id.
	 * @param username The username.
	 * @param id The id.
	 */
	public Player(String username, int id) {
		super(username, id);
	}
}
