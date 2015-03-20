package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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
public class ExecutableWorldChecksumRequestTest extends PowerMockTestCase
{
	@Test
	public void testExecuteMessage() {
		Message response = MessageRegistry.getInstance().createMessage("WorldChecksumResponse");
		byte[] checksum = {1, 2, 3, 4, 5};
		response.setArgument("checksum", checksum);

		// Mocks
		PowerMockito.mockStatic(WorldServerMessageFactory.class);
		WorldClientHandler clientHandler = mock(WorldClientHandler.class);
		Message message = mock(Message.class);
		WorldServer server = mock(WorldServer.class);
		MessageIO messageIO = mock(MessageIO.class);

		// Mock returns
		when(WorldServerMessageFactory.generateWorldChecksumResponse(any())).thenReturn(response);
		when(server.getWorldFileChecksum()).thenReturn(checksum);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableWorldChecksumRequest exec = new ExecutableWorldChecksumRequest(clientHandler, message);
		exec.runImmediate();
		exec.runDelayed();

		verify(messageIO).queueOutgoingMessage(response);
	}
}
