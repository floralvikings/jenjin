package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	@Test
	public void testMessageIsDisabled() {
		Message disabledMessage = MessageRegistry.getInstance().createMessage("DisabledMessage");
		Connection connection = mock(Connection.class);
		ExecutableMessage message =
			  ExecutableMessage.getExecutableMessageFor(connection, disabledMessage);
		Assert.assertNull(message);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testMessageExecution() {
		Connection connection = mock(Connection.class);
		Message message = mock(Message.class);

		DisabledExecutableMessage disabledExecutableMessage = new DisabledExecutableMessage(connection, message);
		disabledExecutableMessage.runImmediate();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testMessageExecutionDelayed() {
		Connection connection = mock(Connection.class);
		Message message = mock(Message.class);

		DisabledExecutableMessage disabledExecutableMessage = new DisabledExecutableMessage(connection, message);
		disabledExecutableMessage.runDelayed();

	}
}