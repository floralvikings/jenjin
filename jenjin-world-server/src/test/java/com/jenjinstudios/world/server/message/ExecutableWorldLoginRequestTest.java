package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.IUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.database.WorldAuthenticator;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginRequestTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void testSuccessfulLogin() throws Exception {
		Map<String, Object> playerData = new HashMap<>();
		playerData.put("xCoord", "0.0");
		playerData.put("yCoord", "0.0");
		playerData.put("zoneID", "0");
		playerData.put("username", "Foo");
		Message message = mock(Message.class);
		WorldServerMessageFactory messageFactory = mock(WorldServerMessageFactory.class);
		World world = mock(World.class);
		IUser user = Mockito.mock(User.class);
		Player player = Mockito.mock(Player.class);
		WorldAuthenticator authenticator = Mockito.mock(WorldAuthenticator.class);
		WorldServer server = Mockito.mock(WorldServer.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		MessageIO messageIO = Mockito.mock(MessageIO.class);
		WorldClientHandler wch = new WorldClientHandler(server, messageIO);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		Mockito.when(user.isLoggedIn()).thenReturn(true);
		Mockito.when(authenticator.logInUser(Mockito.anyString(), Mockito.anyString())).thenReturn(player);
		Mockito.when(server.getAuthenticator()).thenReturn(authenticator);
		Mockito.when(server.getWorld()).thenReturn(world);
		Mockito.when(messageFactory.generateWorldLoginResponse()).thenReturn(message);
		Mockito.when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);
		ExecutableWorldLoginRequest exec = new ExecutableWorldLoginRequest(wch, message);
		exec.runImmediate();
		exec.runDelayed();

		Mockito.verify(worldObjectMap).add(Mockito.anyObject());
	}
}
