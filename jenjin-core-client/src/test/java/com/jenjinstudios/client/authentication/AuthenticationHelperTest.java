package com.jenjinstudios.client.authentication;

import com.jenjinstudios.core.io.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the AuthenticaionHelper class.
 *
 * @author Caleb Brinkman
 */
public class AuthenticationHelperTest
{

	/**
	 * Test the generateLogoutRequest method.
	 */
	@Test
	public void testGenerateLogoutRequest() {
		Message message = AuthenticationHelper.generateLogoutRequest();
		Assert.assertEquals(message.name, "LogoutRequest", "Expectet LogoutRequest message");
	}

	/**
	 * Test the generateLoginRequest method.
	 */
	@Test
	public void testGenerateLoginRequest() {
		User user = new ClientUser();
		user.setUsername("Foo");
		user.setPassword("Bar");
		Message message = AuthenticationHelper.generateLoginRequest(user);
		Assert.assertEquals(message.name, "LoginRequest", "Expected login request");
		Assert.assertEquals(message.getArgument("username"), "Foo", "Username was not expected.");
		Assert.assertEquals(message.getArgument("password"), "Bar", "Password was not expected.");
	}
}
