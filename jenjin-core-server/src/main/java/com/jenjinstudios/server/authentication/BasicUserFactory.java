package com.jenjinstudios.server.authentication;

/**
 * A {@code UserFactory} which will create new {@code BasicUser} instances.
 *
 * @author Caleb Brinkman
 */
public class BasicUserFactory implements UserFactory<BasicUser>
{
	@Override
	public BasicUser createUser(String username) { return new BasicUser(username); }
}
