package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;

import static org.testng.Assert.assertFalse;

/**
 * @author Caleb Brinkman
 */
public class ExecutableWorldLogoutResponseTest extends WorldClientExecutableMessageTest
{
	@Override
	public void testMessageExecution() throws Exception {
		Message worldLogoutResponse = messageRegistry.createMessage("WorldLogoutResponse");
		worldLogoutResponse.setArgument("success", true);
		inStreamReadMessage.thenReturn(worldLogoutResponse, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		Thread.sleep(500); // Sleep to allow client to "catch up"

		assertFalse(worldClient.isLoggedIn());
	}
}
