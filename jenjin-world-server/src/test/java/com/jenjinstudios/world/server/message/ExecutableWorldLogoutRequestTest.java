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
@SuppressWarnings("unchecked")
public class ExecutableWorldLogoutRequestTest
{
	private static final MessageRegistry messageRegistry = MessageRegistry.getInstance();

	@Test
	public void testSuccessfulLogout() {
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
		when(handler.getPlayer()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(true);
		verify(world).scheduleForRemoval(player);
	}

	@Test
	public void testNullUser() {
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
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(false);
	}

	@Test
	public void testFailedLogout() {
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
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.runImmediate();
		exec.runDelayed();

		verify(handler).sendLogoutStatus(false);
	}
}
