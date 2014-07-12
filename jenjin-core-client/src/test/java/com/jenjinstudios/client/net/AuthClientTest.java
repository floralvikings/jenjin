package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Caleb Brinkman
 */
public class AuthClientTest
{
	@Test
	public void testIsLoggedIn() {
		MessageIO messageIO = mock(MessageIO.class);
		ClientUser clientUser = mock(ClientUser.class);
		boolean random = Math.random() * 10 % 2 == 0;
		AuthClient authClient = new AuthClient(messageIO, clientUser);
		authClient.setLoggedIn(random);

		Assert.assertEquals(authClient.isLoggedIn(), random);
	}

	@Test
	public void testLoggedInTime() {
		MessageIO messageIO = mock(MessageIO.class);
		ClientUser clientUser = mock(ClientUser.class);
		long random = (long) (Math.random() * 1000);
		AuthClient authClient = new AuthClient(messageIO, clientUser);
		authClient.setLoggedInTime(random);

		Assert.assertEquals(authClient.getLoggedInTime(), random);
	}

	@Test
	public void testGetUser() {
		MessageIO messageIO = mock(MessageIO.class);
		ClientUser clientUser = mock(ClientUser.class);
		AuthClient authClient = new AuthClient(messageIO, clientUser);

		Assert.assertEquals(authClient.getUser(), clientUser);
	}
}
