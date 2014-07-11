package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.User;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequestTest
{
	private static MessageRegistry messageRegistry = new MessageRegistry();

	@Test
	public void testSuccessfulLogin() {
		Message loginRequest = messageRegistry.createMessage("WorldLoginRequest");
		loginRequest.setArgument("username", "foo");
		loginRequest.setArgument("password", "bar");
		Message loginResponse = messageRegistry.createMessage("WorldLoginResponse");

		World world = mock(World.class);
		Player player = mock(Player.class);
		WorldServerMessageFactory messageFactory = mock(WorldServerMessageFactory.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		WorldAuthenticator authenticator = mock(WorldAuthenticator.class);
		when(messageFactory.generateWorldLoginResponse()).thenReturn(loginResponse);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getMessageFactory()).thenReturn(messageFactory);
		when(authenticator.logInPlayer(any(User.class))).thenReturn(player);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLoginRequest exec = new ExecutableWorldLoginRequest(handler, loginRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(world).addObject(player);
		verify(handler).queueOutgoingMessage(loginResponse);
	}

	@Test
	public void testFailedLogin() {
		Message loginRequest = messageRegistry.createMessage("WorldLoginRequest");
		loginRequest.setArgument("username", "foo");
		loginRequest.setArgument("password", "bar");
		Message loginResponse = messageRegistry.createMessage("WorldLoginResponse");

		World world = mock(World.class);
		WorldServerMessageFactory messageFactory = mock(WorldServerMessageFactory.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		WorldAuthenticator authenticator = mock(WorldAuthenticator.class);
		when(messageFactory.generateWorldLoginResponse()).thenReturn(loginResponse);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getMessageFactory()).thenReturn(messageFactory);
		when(authenticator.logInPlayer(any(User.class))).thenReturn(null);

		ExecutableWorldLoginRequest exec = new ExecutableWorldLoginRequest(handler, loginRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).queueOutgoingMessage(loginResponse);
	}
}
