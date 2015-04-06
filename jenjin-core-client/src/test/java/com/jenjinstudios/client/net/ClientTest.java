package com.jenjinstudios.client.net;

import com.jenjinstudios.client.authentication.AuthenticationHelper;
import com.jenjinstudios.client.authentication.ClientUser;
import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageStreamPair;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

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
		Client client = new Client<>(messageStreamPair, mock(ClientMessageContext.class));
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		verify(r).run();
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

	/**
	 * Test the isLoggedIn method.
	 */
	@Test
	public void testIsLoggedIn() {
		LoginTracker loginTracker = new LoginTracker();
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		ClientMessageContext messageContext = mock(ClientMessageContext.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		User user = mock(ClientUser.class);
		when(messageContext.getLoginTracker()).thenReturn(loginTracker);
		boolean random = ((Math.random() * 10) % 2) == 0;
		Client client = new Client<>(messageStreamPair, messageContext);
		client.getLoginTracker().setLoggedIn(random);

		Assert.assertEquals(client.getLoginTracker().isLoggedIn(), random, "Login status was not expected.");
	}

	/**
	 * Test the getLoggedInTime method.
	 */
	@Test
	public void testLoggedInTime() {
		LoginTracker loginTracker = new LoginTracker();
		ClientMessageContext messageContext = mock(ClientMessageContext.class);
		MessageInputStream mis = mock(MessageInputStream.class);
		MessageOutputStream mos = mock(MessageOutputStream.class);
		MessageStreamPair messageStreamPair = new MessageStreamPair(mis, mos);
		User user = mock(ClientUser.class);
		when(messageContext.getLoginTracker()).thenReturn(loginTracker);
		long random = (long) (Math.random() * 1000);
		Client client = new Client<>(messageStreamPair, messageContext);
		client.getLoginTracker().setLoggedInTime(random);

		Assert.assertEquals(client.getLoginTracker().getLoggedInTime(), random, "Login time was incorrect.");
	}

}
