package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.world.World;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class WorldClientTest
{
	@Test//(timeOut = 2000)
	public void testSendBlockingWorldFileRequest() throws Exception {
		MessageIO messageIO = mock(MessageIO.class);
		ServerWorldFileTracker serverWorldFileTracker = mock(ServerWorldFileTracker.class);
		World world = mock(World.class);
		when(serverWorldFileTracker.readWorldFromServer()).thenReturn(world);
		WorldClient worldClient = new WorldClient(messageIO, new ClientUser("Foo", "Bar"), null);
		worldClient.setServerWorldFileTracker(serverWorldFileTracker);

		worldClient.sendBlockingWorldFileRequest();

		Assert.assertEquals(worldClient.getWorld(), world);
	}
}
