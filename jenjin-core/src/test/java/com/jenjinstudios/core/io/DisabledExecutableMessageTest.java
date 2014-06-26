package com.jenjinstudios.core.io;

import com.jenjinstudios.core.message.DisabledExecutableMessage;
import org.mockito.Mockito;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunAsync() {
		Message mock = Mockito.mock(Message.class);
		DisabledExecutableMessage dex = new DisabledExecutableMessage(mock);
		dex.runImmediate();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunSynced() {
		Message mock = Mockito.mock(Message.class);
		DisabledExecutableMessage dex = new DisabledExecutableMessage(mock);
		dex.runDelayed();
	}
}
