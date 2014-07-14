package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.ClientHandler;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessageTest
{
	@Test
	public void testMessageExecution() {
		byte[] bytes = {1, 2, 3, 4, 5};
		Message message = MessageRegistry.getInstance().createMessage("PublicKeyMessage");
		message.setArgument("key", bytes);

		ClientHandler clientHandler = mock(ClientHandler.class);
		ServerMessageFactory serverMessageFactory = mock(ServerMessageFactory.class);
		when(clientHandler.getMessageFactory()).thenReturn(serverMessageFactory);

		ExecutablePublicKeyMessage executablePublicKeyMessage = new ExecutablePublicKeyMessage(clientHandler, message);
		executablePublicKeyMessage.runImmediate();
		executablePublicKeyMessage.runDelayed();

		verify(serverMessageFactory).generateAESKeyMessage(bytes);
	}
}
