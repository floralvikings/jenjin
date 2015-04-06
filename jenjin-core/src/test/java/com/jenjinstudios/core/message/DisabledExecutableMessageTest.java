package com.jenjinstudios.core.message;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Test the DisabledExecutableMessage class.
 *
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{

    /**
     * Ensure that the DisabledMessage cannot be invoked.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMessageExecution() {
		Message message = mock(Message.class);
		MessageContext context = mock(MessageContext.class);

		DisabledExecutableMessage executableMessage = new DisabledExecutableMessage(message, context);
		executableMessage.execute();
	}
}