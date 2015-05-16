package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.BasicUser;
import com.jenjinstudios.server.authentication.User;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;
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
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);
		Geometry2D geometry2D = new Geometry2D();

		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(user.isLoggedIn()).thenReturn(true);
		when(authenticator.logInUser(Matchers.anyString(), Matchers.anyString())).thenReturn(player);
		when(context.getAuthenticator()).thenReturn(authenticator);
		when(context.getUser()).thenReturn(player);
		when(context.getWorld()).thenReturn(world);
		when(WorldServerMessageFactory.generateWorldLoginResponse()).thenReturn(message);
		when(player.getGeometry2D()).thenReturn(geometry2D);
		ExecutableWorldLoginRequest exec = new ExecutableWorldLoginRequest(message, context);
		exec.execute();
		world.update();

		Mockito.verify(worldObjectMap).add(Matchers.anyObject());
	}
}
