package com.jenjinstudios.world.server.authentication;

import com.jenjinstudios.server.authentication.UserFactory;
import com.jenjinstudios.world.server.Player;

/**
 * A {@code UserFactory} which creates {@code Player} instances.
 *
 * @author Caleb Brinkman
 */
public class PlayerFactory implements UserFactory<Player>
{
	@Override
	public Player createUser(String username) { return new Player(username); }
}
