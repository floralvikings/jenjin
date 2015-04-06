package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
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
		Message response = mock(Message.class);
		byte[] checksum = {1, 2, 3, 4, 5};
		when(response.getArgument("checksum")).thenReturn(checksum);

		// Mocks
		PowerMockito.mockStatic(WorldServerMessageFactory.class);
		WorldClientHandler clientHandler = mock(WorldClientHandler.class);
		Message message = mock(Message.class);
		WorldServer server = mock(WorldServer.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);

		// Mock returns
		when(WorldServerMessageFactory.generateWorldChecksumResponse(any())).thenReturn(response);
		when(context.getWorldChecksum()).thenReturn(checksum);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageStreamPair()).thenReturn(messageStreamPair);

		ExecutableWorldChecksumRequest exec = new ExecutableWorldChecksumRequest(clientHandler, message, context);
		Message resp = exec.execute();

		Assert.assertEquals(resp, response, "Response mocks should be equal.");
	}
}
