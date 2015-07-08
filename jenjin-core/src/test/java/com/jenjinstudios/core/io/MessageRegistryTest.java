package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.MessageType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

/**
 * Test the MessageRegistry class.
 *
 * @author Caleb Brinkman
 */
public class MessageRegistryTest
{
	/**
	 * Test the register method.
	 */
	@Test(groups = "unit")
	public void testRegister() {
		String messageFile = "test/jenjinstudios/core/Messages.xml";
		InputStream in = getClass().getClassLoader().getResourceAsStream(messageFile);
		String name = "Test Message Group";

		MessageRegistry messageRegistry = new MessageRegistry();
		messageRegistry.register(name, in);

		MessageType message = messageRegistry.getMessageType("BlankMessage");
		Assert.assertNotNull(message, "Message type should have been registered");
	}

	/**
	 * Test the clear method.
	 */
	@Test(groups = "unit")
	public void testClear() {
		String messageFile = "test/jenjinstudios/core/Messages.xml";
		InputStream in = getClass().getClassLoader().getResourceAsStream(messageFile);
		String name = "Test Message Group";

		MessageRegistry messageRegistry = new MessageRegistry();
		messageRegistry.register(name, in);
		messageRegistry.clear();

		MessageType message = messageRegistry.getMessageType("BlankMessage");
		Assert.assertNull(message, "Message type should have been cleared");
	}

	/**
	 * Test the createMessage method.
	 */
	@Test(groups = "unit")
	public void testCreateMessage() {
		String messageFile = "test/jenjinstudios/core/Messages.xml";
		InputStream in = getClass().getClassLoader().getResourceAsStream(messageFile);
		String name = "Test Message Group";

		MessageRegistry messageRegistry = new MessageRegistry();
		messageRegistry.register(name, in);

		Message message = messageRegistry.createMessage("BlankMessage");
		Assert.assertEquals(message.getID(), -1, "Message type should have ID 0");
	}
}
