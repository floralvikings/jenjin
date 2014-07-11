package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.client.WorldClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLoginResponseTest
{
	@Test
	public void testMessageExecution() {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message message = messageRegistry.createMessage("WorldLoginResponse");
		message.setArgument("id", 0);
		message.setArgument("success", true);
		message.setArgument("loginTime", 0l);
		message.setArgument("xCoordinate", 0.0);
		message.setArgument("yCoordinate", 0.0);
		message.setArgument("zoneNumber", 0);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		when(worldClient.getWorld()).thenReturn(world);
		when(worldClient.getUser()).thenReturn(new ClientUser("Foo", "Bar"));
		when(worldClient.isLoggedIn()).thenReturn(true);

		ExecutableWorldLoginResponse response = new ExecutableWorldLoginResponse(worldClient, message);
		response.runImmediate();
		response.runDelayed();

		verify(worldClient).setWaitingForLoginResponse(false);
		verify(worldClient).setLoggedIn(true);
		verify(worldClient).setLoggedInTime(0l);
		verify(worldClient).setPlayer((ClientPlayer) any());
		verify(world).addObject((WorldObject) any(), eq(0));
	}
}
