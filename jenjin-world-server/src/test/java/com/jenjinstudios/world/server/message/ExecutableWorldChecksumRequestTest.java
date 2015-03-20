package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldChecksumRequestTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteMessage() {
		Message message = mock(Message.class);
		Message response = MessageRegistry.getInstance().createMessage("WorldChecksumResponse");
		byte[] checksum = {1, 2, 3, 4, 5};
		response.setArgument("checksum", checksum);
		WorldClientHandler clientHandler = mock(WorldClientHandler.class);
		WorldServer server = mock(WorldServer.class);
		WorldServerMessageFactory messageFactory = mock(WorldServerMessageFactory.class);
		MessageIO messageIO = mock(MessageIO.class);
		when(WorldServerMessageFactory.generateWorldChecksumResponse(any())).thenReturn(response);
		when(server.getWorldFileChecksum()).thenReturn(checksum);
		when(clientHandler.getMessageFactory()).thenReturn(messageFactory);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageIO()).thenReturn(messageIO);

		ExecutableWorldChecksumRequest exec = new ExecutableWorldChecksumRequest(clientHandler, message);
		exec.runImmediate();
		exec.runDelayed();

		verify(messageIO).queueOutgoingMessage(response);
	}
}
