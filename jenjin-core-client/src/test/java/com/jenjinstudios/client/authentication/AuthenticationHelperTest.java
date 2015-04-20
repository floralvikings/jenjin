package com.jenjinstudios.client.authentication;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

/**
 * Test the AuthenticaionHelper class.
 *
 * @author Caleb Brinkman
 */
public class AuthenticationHelperTest
{
	/**
	 * Register messages for testing.
	 */
	@BeforeClass
	public void registerMessages()
	{
		InputStream stream = getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Core Client/Server Messages", stream);
	}

	/**
	 * Clear the registry after tests run.
	 */
	@AfterClass
	public void clearRegistry()
	{
		MessageRegistry.getGlobalRegistry().clear();
	}

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
