package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Test the DisabledExecutableMessage class.
 *
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	/**
	 * Ensure that a message can be properly disabled.
	 */
	@Test
	public void testMessageIsDisabled() {
		Message disabledMessage = MessageRegistry.getInstance().createMessage("DisabledMessage");
		Connection connection = mock(Connection.class);
		List<ExecutableMessage> message = ExecutableMessage.getExecutableMessagesFor(connection, disabledMessage);
		Assert.assertTrue(message.isEmpty());
	}

	/**
	 * Ensure that the DisabledMessage cannot be invoked.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void testMessageExecution() {
		Connection connection = mock(Connection.class);
		Message message = mock(Message.class);

		DisabledExecutableMessage disabledExecutableMessage = new DisabledExecutableMessage(connection, message);
		disabledExecutableMessage.runImmediate();
	}

	/**
	 * Ensure that the DisabledMessage cannot be invoked.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void testMessageExecutionDelayed() {
		Connection connection = mock(Connection.class);
		Message message = mock(Message.class);

		DisabledExecutableMessage disabledExecutableMessage = new DisabledExecutableMessage(connection, message);
		disabledExecutableMessage.runDelayed();

	}
}