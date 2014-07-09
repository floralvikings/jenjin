package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.WorldObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ExecutableObjectInvisibleMessageTest extends WorldClientExecutableMessageTest
{
	@Test(timeOut = 5000)
	@Override
	public void testMessageExecution() throws Exception {
		Message objectInvisibleMessage = messageRegistry.createMessage("ObjectInvisibleMessage");
		objectInvisibleMessage.setArgument("id", 100);
		inStreamReadMessage.thenReturn(objectInvisibleMessage, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		worldClient.getWorld().addObject(new WorldObject("Bob"), 100);
		Thread.sleep(500); // Sleep to allow client to "catch up"

		Assert.assertEquals(worldClient.getWorld().getObjectCount(), 1);
	}
}
