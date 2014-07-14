package com.jenjinstudios.world.server;

import com.jenjinstudios.world.Actor;

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
}
