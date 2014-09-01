package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class MessageTest
{
	private static final MessageRegistry mr = MessageRegistry.getInstance();

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidName() {
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("FooBar", 1337); // Expect exception
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidArgumentType() {
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", "I'm totally a short, you guys."); // Expect exception
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testUnsetArgs() {
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.getArgs(); // Expect exception
	}

	@Test
	public void testToString() {
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", (short) -255);

		String expected = "Message -1 InvalidMessage";
		String actual = msg.toString();

		Assert.assertEquals(actual, expected);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testStreamConstructor() {
		new Message(mr, (short) -1, "Bob"); // Expect Exception
	}
}
