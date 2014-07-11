package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ArgumentTypeTest
{
	@Test
	public void testToString() {
		MessageRegistry mr = MessageRegistry.getInstance();
		MessageType mt = mr.getMessageType("InvalidMessage");
		String actual = mt.getArgumentType("messageName").toString();
		String expected = "messageName, class java.lang.String, encrypt: false";
		Assert.assertEquals(actual, expected);
	}
}
