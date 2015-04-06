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
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(WorldServerMessageFactory.generateWorldFileResponse(any())).thenReturn(response);
		when(context.getWorldBytes()).thenReturn(fileBytes);
		when(clientHandler.getServer()).thenReturn(server);
		when(clientHandler.getMessageStreamPair()).thenReturn(messageStreamPair);

		ExecutableWorldFileRequest exec = new ExecutableWorldFileRequest(message, context);
		Message resp = exec.execute();

		Assert.assertEquals(resp, response, "Response mocks should be equal");
	}
}
