package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class AuthClientTest
{
	@Test
	public void testIsLoggedIn() {
		MessageIO messageIO = mock(MessageIO.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		when(messageIO.getIn()).thenReturn(mis);
		when(messageIO.getOut()).thenReturn(mos);
		ClientUser clientUser = mock(ClientUser.class);
		boolean random = Math.random() * 10 % 2 == 0;
		AuthClient authClient = new AuthClient(messageIO, clientUser);
		authClient.getLoginTracker().setLoggedIn(random);

		Assert.assertEquals(authClient.getLoginTracker().isLoggedIn(), random);
	}

	@Test
	public void testLoggedInTime() {
		MessageIO messageIO = mock(MessageIO.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		when(messageIO.getIn()).thenReturn(mis);
		when(messageIO.getOut()).thenReturn(mos);
		ClientUser clientUser = mock(ClientUser.class);
		long random = (long) (Math.random() * 1000);
		AuthClient authClient = new AuthClient(messageIO, clientUser);
		authClient.getLoginTracker().setLoggedInTime(random);

		Assert.assertEquals(authClient.getLoginTracker().getLoggedInTime(), random);
	}

	@Test
	public void testGetUser() {
		MessageIO messageIO = mock(MessageIO.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		when(messageIO.getIn()).thenReturn(mis);
		when(messageIO.getOut()).thenReturn(mos);
		ClientUser clientUser = mock(ClientUser.class);
		AuthClient authClient = new AuthClient(messageIO, clientUser);

		Assert.assertEquals(authClient.getUser(), clientUser);
	}
}
