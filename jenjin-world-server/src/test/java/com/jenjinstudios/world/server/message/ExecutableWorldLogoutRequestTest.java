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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutRequestTest
{
	@Test
	public void testSuccessfulLogout() {
		MessageRegistry messageRegistry = new MessageRegistry();
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Player player = mock(Player.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		WorldAuthenticator authenticator = mock(WorldAuthenticator.class);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(new User());
		when(authenticator.logOutPlayer(any(Player.class))).thenReturn(true);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(true);
	}

	@Test
	public void testNullUser() {
		MessageRegistry messageRegistry = new MessageRegistry();
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Player player = mock(Player.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		WorldAuthenticator authenticator = mock(WorldAuthenticator.class);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(null);
		when(authenticator.logOutPlayer(any(Player.class))).thenReturn(true);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(false);
	}

	@Test
	public void testFailedLogout() {
		MessageRegistry messageRegistry = new MessageRegistry();
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Player player = mock(Player.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		WorldAuthenticator authenticator = mock(WorldAuthenticator.class);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(new User());
		when(authenticator.logOutPlayer(any(Player.class))).thenReturn(false);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(false);
	}
}
