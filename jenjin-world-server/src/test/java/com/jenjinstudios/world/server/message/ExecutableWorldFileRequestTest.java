package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
@PrepareForTest(WorldServerMessageFactory.class)
public class ExecutableWorldFileRequestTest extends PowerMockTestCase
{
	@Test
	@PrepareForTest(WorldServerMessageFactory.class)
	public void testExecuteMessage() {
		Message message = mock(Message.class);
		Message response = mock(Message.class);
		byte[] fileBytes = {1, 2, 3, 4, 5};
		when(response.getArgument("fileBytes")).thenReturn(fileBytes);

		PowerMockito.mockStatic(WorldServerMessageFactory.class);
		WorldClientHandler clientHandler = mock(WorldClientHandler.class);
		WorldServer server = mock(WorldServer.class);
		MessageIO messageIO = mock(MessageIO.class);

		when(WorldServerMessageFactory.generateWorldFileResponse(any())).thenReturn(response);
		when(server.getWorldFileChecksum()).thenReturn(fileBytes);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableWorldFileRequest exec = new ExecutableWorldFileRequest(clientHandler, message);
		exec.runImmediate();

		verify(clientHandler).enqueueMessage(response);
	}
}
