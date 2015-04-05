package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.world.client.message.ExecutableWorldChecksumResponse;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Caleb Brinkman
 */
public class WorldFileTrackerTest
{
	@Test(timeOut = 5000)
	public void testRequestWorldServerFileChecksum() throws Exception {
		WorldClient worldClient = Mockito.mock(WorldClient.class);
		WorldClientMessageFactory messageFactory = Mockito.mock(WorldClientMessageFactory.class);
		File worldFile = Mockito.mock(File.class);
		Message message = Mockito.mock(Message.class);
		MessageStreamPair messageStreamPair = Mockito.mock(MessageStreamPair.class);
		Mockito.when(worldClient.getMessageFactory()).thenReturn(messageFactory);
		Mockito.when(message.getArgument("checksum")).thenReturn("abc123".getBytes());
		Mockito.when(messageFactory.generateWorldChecksumRequest()).thenReturn(message);
		Mockito.when(worldClient.getMessageStreamPair()).thenReturn(messageStreamPair);

		WorldFileTracker worldFileTracker = new WorldFileTracker(worldFile);
		Mockito.when(worldClient.getWorldFileTracker()).thenReturn(worldFileTracker);
		worldFileTracker.setWaitingForChecksum(true);
		WorldClient.requestChecksum(worldClient);

		Assert.assertTrue(worldFileTracker.isWaitingForChecksum());

		ExecutableWorldChecksumResponse exec = new ExecutableWorldChecksumResponse(worldClient, message, null);
		exec.execute();

		Assert.assertFalse(worldFileTracker.isWaitingForChecksum());
		Assert.assertEquals(worldFileTracker.getChecksum(), "abc123".getBytes());

	}

}
