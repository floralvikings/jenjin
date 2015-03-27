package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
public class ExecutableWorldLogoutRequestTest
{
	private static final MessageRegistry messageRegistry = MessageRegistry.getGlobalRegistry();

	@Test
	public void testSuccessfulLogout() {
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = spy(new World());
		Player player = mock(Player.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.execute();
		world.update();

		verify(handler).sendLogoutStatus(true);
		verify(worldObjectMap).remove(player.getId());
	}

	@Test
	public void testNullUser() throws Exception {
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Actor player = mock(Actor.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(null);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.execute();

		verify(handler).sendLogoutStatus(false);
	}

	@Test
	public void testFailedLogout() throws Exception {
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Player player = new Player("Player");
		player.setLoggedIn(true);
		WorldServer worldServer = mock(WorldServer.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		when(worldServer.getAuthenticator()).thenReturn(authenticator);
		when(worldServer.getWorld()).thenReturn(world);
		when(authenticator.logOutUser(any())).thenThrow(new AuthenticationException("Foo"));
		when(handler.getServer()).thenReturn(worldServer);
		when(handler.getUser()).thenReturn(player);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest);
		exec.execute();

		verify(handler).sendLogoutStatus(false);
	}
}
