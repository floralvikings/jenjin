package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
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
        MessageIO messageIO = new MessageIO(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
        boolean random = ((Math.random() * 10) % 2) == 0;
        AuthClient authClient = new AuthClient(messageIO, clientUser);
        authClient.getLoginTracker().setLoggedIn(random);

        Assert.assertEquals(authClient.getLoginTracker().isLoggedIn(), random, "Login status was not expected.");
    }

    /**
     * Test the getLoggedInTime method.
     */
    @Test
    public void testLoggedInTime() {
        MessageInputStream mis = mock(MessageInputStream.class);
        MessageOutputStream mos = mock(MessageOutputStream.class);
        MessageIO messageIO = new MessageIO(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
        long random = (long) (Math.random() * 1000);
        AuthClient authClient = new AuthClient(messageIO, clientUser);
        authClient.getLoginTracker().setLoggedInTime(random);

        Assert.assertEquals(authClient.getLoginTracker().getLoggedInTime(), random);
    }

    @Test
    public void testGetUser() {
        MessageInputStream mis = mock(MessageInputStream.class);
        MessageOutputStream mos = mock(MessageOutputStream.class);
        MessageIO messageIO = new MessageIO(mis, mos);
        ClientUser clientUser = mock(ClientUser.class);
        AuthClient authClient = new AuthClient(messageIO, clientUser);

        Assert.assertEquals(authClient.getUser(), clientUser);
    }
}
