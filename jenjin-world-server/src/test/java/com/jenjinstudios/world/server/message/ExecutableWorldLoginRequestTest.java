package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.server.net.ServerMessageContext;
import com.jenjinstudios.server.net.ServerUpdateTask;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@PrepareForTest(WorldServerMessageFactory.class)
public class ExecutableWorldLoginRequestTest extends PowerMockTestCase
{
	@Test
	@SuppressWarnings("unchecked")
	public void testSuccessfulLogin() throws Exception {
		Map<String, Object> playerData = new HashMap<>();
		playerData.put("xCoord", "0.0");
		playerData.put("yCoord", "0.0");
		playerData.put("zoneID", "0");
		playerData.put("username", "Foo");

		PowerMockito.mockStatic(WorldServerMessageFactory.class);
		Message message = mock(Message.class);
		World world = spy(new World());
		User user = mock(BasicUser.class);
		Player player = mock(Player.class);
		Authenticator<Player> authenticator = mock(Authenticator.class);
		WorldServer server = mock(WorldServer.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);
		WorldClientHandler wch = new WorldClientHandler(server, messageStreamPair, new ServerMessageContext());

		ServerUpdateTask serverUpdateTask = mock(ServerUpdateTask.class);
		when(server.getServerUpdateTask()).thenReturn(serverUpdateTask);
		when(serverUpdateTask.getCycleStartTime()).thenReturn(0L);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(user.isLoggedIn()).thenReturn(true);
		when(authenticator.logInUser(Matchers.anyString(), Matchers.anyString())).thenReturn(player);
		when(server.getAuthenticator()).thenReturn(authenticator);
		when(server.getWorld()).thenReturn(world);
		when(WorldServerMessageFactory.generateWorldLoginResponse()).thenReturn(message);
		when(player.getVector2D()).thenReturn(Vector2D.ORIGIN);
		ExecutableWorldLoginRequest exec = new ExecutableWorldLoginRequest(wch, message, null);
		exec.execute();
		world.update();

		Mockito.verify(worldObjectMap).add(Matchers.anyObject());
	}
}
