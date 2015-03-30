package com.jenjinstudios.client.net;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Used to test the Client class.
 * @author Caleb Brinkman
 */
public class ClientTest
{
    /**
     * Test the AddRepeatedTask method.
     */
    @Test
	public void testAddRepeatedTask() {
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		Runnable r = mock(Runnable.class);
		Client client = new Client(messageStreamPair);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		verify(r).run();
	}

    /**
     * Test the generateLogoutRequest method.
     */
    @Test
    public void testGenerateLogoutRequest() {
        Message message = AuthClient.generateLogoutRequest();
        Assert.assertEquals(message.name, "LogoutRequest", "Expectet LogoutRequest message");
    }

    /**
     * Test the generateLoginRequest method.
     */
    @Test
    public void testGenerateLoginRequest() {
        Message message = AuthClient.generateLoginRequest(new ClientUser("Foo", "Bar"));
        Assert.assertEquals(message.name, "LoginRequest", "Expected login request");
        Assert.assertEquals(message.getArgument("username"), "Foo", "Username was not expected.");
        Assert.assertEquals(message.getArgument("password"), "Bar", "Password was not expected.");
    }

}
