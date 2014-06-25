package com.jenjinstudios.core.io;

import com.jenjinstudios.core.message.DisabledExecutableMessage;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunAsync() {
		DisabledExecutableMessage dex = new DisabledExecutableMessage(null);
		dex.runImmediate();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunSynced() {
		DisabledExecutableMessage dex = new DisabledExecutableMessage(null);
		dex.runDelayed();
	}
}
