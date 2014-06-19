package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class MessageTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidName() {
		MessageRegistry mr = new MessageRegistry();
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("FooBar", 1337); // Expect exception
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidArgumentType() {
		MessageRegistry mr = new MessageRegistry();
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", "I'm totally a short, you guys."); // Expect exception
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testUnsetArgs() {
		MessageRegistry mr = new MessageRegistry();
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.getArgs(); // Expect exception
	}

	@Test
	public void testToString() {
		MessageRegistry mr = new MessageRegistry();
		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageName", "FooBar");
		msg.setArgument("messageID", (short) -255);

		String expected = "Message -1 InvalidMessage {messageID=-255, messageName=FooBar}";
		String actual = msg.toString();

		Assert.assertEquals(actual, expected);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testStreamConstructor() {
		MessageRegistry mr = new MessageRegistry();
		new Message(mr, (short) -1, "Bob"); // Expect Exception
	}
}
