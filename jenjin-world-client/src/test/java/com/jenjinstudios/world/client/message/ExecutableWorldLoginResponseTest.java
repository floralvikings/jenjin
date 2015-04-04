package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.authentication.User;
import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.client.net.LoginTracker;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.collections.WorldObjectList;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponseTest
{
	@Test
	public void testMessageExecution() {
		Message message = mock(Message.class);
		when(message.getArgument("id")).thenReturn(0);
		when(message.getArgument("success")).thenReturn(true);
		when(message.getArgument("loginTime")).thenReturn(0L);
		when(message.getArgument("xCoordinate")).thenReturn(0.0);
		when(message.getArgument("yCoordinate")).thenReturn(0.0);
		when(message.getArgument("zoneNumber")).thenReturn(0);

		WorldClient worldClient = mock(WorldClient.class);
		World world = spy(new World());
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		ClientMessageContext context = mock(ClientMessageContext.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		LoginTracker loginTracker = mock(LoginTracker.class);
		when(loginTracker.isLoggedIn()).thenReturn(true);
		when(worldClient.getWorld()).thenReturn(world);
		when(context.getLoginTracker()).thenReturn(loginTracker);
		when(context.getUser()).thenReturn(mock(User.class));

		ExecutableWorldLoginResponse response = new ExecutableWorldLoginResponse(worldClient, message, context);
		response.execute();
		world.update();

		verify(loginTracker).setLoggedIn(true);
		verify(loginTracker).setLoggedInTime(0l);
		verify(worldClient).setPlayer(any());
		verify(worldObjectMap).set(eq(0), any());
	}
}
