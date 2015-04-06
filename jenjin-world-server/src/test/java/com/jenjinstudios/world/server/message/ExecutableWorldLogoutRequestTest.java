package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.AuthenticationException;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.message.ServerMessageFactory;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@SuppressWarnings("unchecked")
@PrepareForTest(ServerMessageFactory.class)
public class ExecutableWorldLogoutRequestTest extends PowerMockTestCase
{
	private static final MessageRegistry messageRegistry = MessageRegistry.getGlobalRegistry();

	@Test
	public void testSuccessfulLogout() {
		PowerMockito.mockStatic(ServerMessageFactory.class);
		when(ServerMessageFactory.generateLogoutResponse(anyBoolean())).thenReturn(mock(Message.class));
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = spy(new World());
		Player player = mock(Player.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(context.getAuthenticator()).thenReturn(authenticator);
		when(context.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(context.getUser()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest, context);
		exec.execute();
		world.update();

		verify(worldObjectMap).remove(player.getId());
	}

	@Test
	public void testNullUser() throws Exception {
		PowerMockito.mockStatic(ServerMessageFactory.class);
		Message response = mock(Message.class);
		when(ServerMessageFactory.generateLogoutResponse(false)).thenReturn(response);
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Actor player = mock(Actor.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(context.getAuthenticator()).thenReturn(authenticator);
		when(context.getWorld()).thenReturn(world);
		when(handler.getServer()).thenReturn(worldServer);
		when(context.getUser()).thenReturn(null);
		when(player.getId()).thenReturn(0);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest, context);
		Message resp = exec.execute();

		Assert.assertEquals(resp, response, "Response mocks should be equal");
	}

	@Test
	public void testFailedLogout() throws Exception {
		PowerMockito.mockStatic(ServerMessageFactory.class);
		Message response = mock(Message.class);
		when(ServerMessageFactory.generateLogoutResponse(false)).thenReturn(response);
		Message logOutRequest = messageRegistry.createMessage("WorldLogoutRequest");

		World world = mock(World.class);
		Player player = new Player("Player");
		player.setLoggedIn(true);
		WorldServer worldServer = mock(WorldServer.class);
		WorldClientHandler handler = mock(WorldClientHandler.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(context.getAuthenticator()).thenReturn(authenticator);
		when(context.getWorld()).thenReturn(world);
		when(authenticator.logOutUser(any())).thenThrow(new AuthenticationException("Foo"));
		when(handler.getServer()).thenReturn(worldServer);
		when(context.getUser()).thenReturn(player);

		ExecutableWorldLogoutRequest exec = new ExecutableWorldLogoutRequest(handler, logOutRequest, context);
		Message resp = exec.execute();

		Assert.assertEquals(resp, response, "Response mocks should be equal");
	}
}
