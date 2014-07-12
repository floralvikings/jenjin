package com.jenjinstudios.server.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.message.ExecutableMessage;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	@Test
	public void testMessageIsDisabled() {
		Message disabledMessage = MessageRegistry.getInstance().createMessage("DisabledMessage");
		Connection connection = mock(Connection.class);
		when(connection.getMessageRegistry()).thenReturn(MessageRegistry.getInstance());
		ExecutableMessage message =
			  ExecutableMessage.getExecutableMessageFor(connection, disabledMessage);
		Assert.assertNull(message);
	}
}
