package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponseTest extends WorldClientExecutableMessageTest
{
	@Test(timeOut = 5000)
	@Override
	public void testMessageExecution() throws Exception {
		Message worldLogoutResponse = messageRegistry.createMessage("WorldLogoutResponse");
		worldLogoutResponse.setArgument("success", true);
		inStreamReadMessage.thenReturn(worldLogoutResponse, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		worldClient.sendBlockingLogoutRequest();
		Thread.sleep(500); // Sleep to allow client to "catch up"

		assertFalse(worldClient.isLoggedIn());
	}
}
