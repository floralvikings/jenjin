package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@code ArgumentType} class.
 * @author Caleb Brinkman
 */
public class ArgumentTypeTest
{
	/**
	 * Test the {@code toString} method.
	 */
	@Test
	public void testToString() {
		MessageRegistry mr = MessageRegistry.getInstance();
		MessageType mt = mr.getMessageType("InvalidMessage");
		ArgumentType argumentType = null;
		for (ArgumentType a : mt.getArguments())
		{
			if (a.getName().equals("messageName"))
			{
				argumentType = a;
			}
		}
		Assert.assertNotNull(argumentType);
		String actual = argumentType.toString();
		String expected = "messageName, String, encrypt: false";
		Assert.assertEquals(actual, expected);
	}
}
