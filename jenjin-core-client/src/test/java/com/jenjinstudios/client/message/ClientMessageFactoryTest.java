package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientMessageFactoryTest
{
	@Test
	public void testGenerateLogoutRequest() {
		ClientMessageFactory messageFactory = new ClientMessageFactory(MessageRegistry.getInstance());
		Message message = messageFactory.generateLogoutRequest();
		Assert.assertEquals(message.name, "LogoutRequest");
	}

	@Test
	public void testGenerateLoginRequest() {
		ClientMessageFactory messageFactory = new ClientMessageFactory(MessageRegistry.getInstance());
		Message message = messageFactory.generateLoginRequest(new ClientUser("Foo", "Bar"));
		Assert.assertEquals(message.name, "LoginRequest");
		Assert.assertEquals(message.getArgument("username"), "Foo");
		Assert.assertEquals(message.getArgument("password"), "Bar");
	}
}
