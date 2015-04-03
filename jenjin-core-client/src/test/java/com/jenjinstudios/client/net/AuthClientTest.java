package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Test the AuthClient class.
 *
 * @author Caleb Brinkman
 */
public class AuthClientTest
{
    /**
     * Test the isLoggedIn method.
     */
    @Test
    public void testIsLoggedIn() {
        MessageInputStream mis = mock(MessageInputStream.class);
        MessageOutputStream mos = mock(MessageOutputStream.class);
        MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
        boolean random = ((Math.random() * 10) % 2) == 0;
		Client client = new AuthClient(messageStreamPair, clientUser);
		client.getLoginTracker().setLoggedIn(random);

		Assert.assertEquals(client.getLoginTracker().isLoggedIn(), random, "Login status was not expected.");
	}

    /**
     * Test the getLoggedInTime method.
     */
    @Test
    public void testLoggedInTime() {
        MessageInputStream mis = mock(MessageInputStream.class);
        MessageOutputStream mos = mock(MessageOutputStream.class);
        MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
        long random = (long) (Math.random() * 1000);
		Client client = new AuthClient(messageStreamPair, clientUser);
		client.getLoginTracker().setLoggedInTime(random);

		Assert.assertEquals(client.getLoginTracker().getLoggedInTime(), random, "Login time was incorrect.");
	}

    /**
     * Test the getUser method.
     */
    @Test
    public void testGetUser() {
        MessageInputStream mis = mock(MessageInputStream.class);
        MessageOutputStream mos = mock(MessageOutputStream.class);
        MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
		Client client = new AuthClient(messageStreamPair, clientUser);

		Assert.assertEquals(client.getUser(), clientUser, "User was incorrect.");
	}
}
