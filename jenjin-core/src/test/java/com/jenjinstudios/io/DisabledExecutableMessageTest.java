package com.jenjinstudios.io;

import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class DisabledExecutableMessageTest
{
	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunAsync() {
		DisabledExecutableMessage dex = new DisabledExecutableMessage(null);
		dex.runASync();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testRunSynced() {
		DisabledExecutableMessage dex = new DisabledExecutableMessage(null);
		dex.runSynced();
	}
}
