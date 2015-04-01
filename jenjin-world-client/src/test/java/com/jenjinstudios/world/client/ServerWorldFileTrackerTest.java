package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.world.client.message.ExecutableWorldChecksumResponse;
import com.jenjinstudios.world.client.message.ExecutableWorldFileResponse;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Caleb Brinkman
 */
public class ServerWorldFileTrackerTest
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

		ServerWorldFileTracker serverWorldFileTracker = new ServerWorldFileTracker(worldClient, worldFile);
		Mockito.when(worldClient.getServerWorldFileTracker()).thenReturn(serverWorldFileTracker);
		serverWorldFileTracker.setWaitingForChecksum(true);
		serverWorldFileTracker.requestServerWorldFileChecksum();

		Assert.assertTrue(serverWorldFileTracker.isWaitingForChecksum());

		ExecutableWorldChecksumResponse exec = new ExecutableWorldChecksumResponse(worldClient, message, null);
		exec.execute();

		Assert.assertFalse(serverWorldFileTracker.isWaitingForChecksum());
		Assert.assertEquals(serverWorldFileTracker.getChecksum(), "abc123".getBytes());

	}

	@Test(timeOut = 5000)
	public void testRequestServerWorldFile() throws Exception {
		WorldClient worldClient = Mockito.mock(WorldClient.class);
		WorldClientMessageFactory messageFactory = Mockito.mock(WorldClientMessageFactory.class);
		File worldFile = Mockito.mock(File.class);
		Message message = Mockito.mock(Message.class);
		MessageStreamPair messageStreamPair = Mockito.mock(MessageStreamPair.class);
		Mockito.when(worldClient.getMessageFactory()).thenReturn(messageFactory);
		Mockito.when(message.getArgument("fileBytes")).thenReturn("abc123".getBytes());
		Mockito.when(messageFactory.generateWorldChecksumRequest()).thenReturn(message);
		Mockito.when(worldClient.getMessageStreamPair()).thenReturn(messageStreamPair);

		ServerWorldFileTracker serverWorldFileTracker = new ServerWorldFileTracker(worldClient, worldFile);
		Mockito.when(worldClient.getServerWorldFileTracker()).thenReturn(serverWorldFileTracker);
		serverWorldFileTracker.setWaitingForFile(true);
		serverWorldFileTracker.requestServerWorldFile();

		Assert.assertTrue(serverWorldFileTracker.isWaitingForFile());

		ExecutableWorldFileResponse exec = new ExecutableWorldFileResponse(worldClient, message, null);
		exec.execute();

		Assert.assertFalse(serverWorldFileTracker.isWaitingForFile());
		Assert.assertEquals(serverWorldFileTracker.getBytes(), "abc123".getBytes());
	}
}
