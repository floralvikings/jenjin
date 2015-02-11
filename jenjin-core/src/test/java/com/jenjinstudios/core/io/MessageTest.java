package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the {@code Message class}.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("MagicNumber")
public class MessageTest
{
    private static final MessageRegistry mr = MessageRegistry.getInstance();

    /**
     * Test creating a message with an invalid name.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidName() {
        Message msg = mr.createMessage("InvalidMessage");
        msg.setArgument("FooBar", 1337); // Expect exception
    }

    /**
     * Test creating a message with an invalid argument.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidArgumentType() {
        Message msg = mr.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.setArgument("messageID", "I'm totally a short, you guys."); // Expect exception
    }

    /**
     * Test retrieving message arguments with arguments unset.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnsetArgs() {
        Message msg = mr.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.getArgs(); // Expect exception
    }

    /**
     * Test message toString method.
     */
    @Test
    public void testToString() {
        Message msg = mr.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.setArgument("messageID", (short) -255);

        String expected = "Message 0 InvalidMessage";
        String actual = msg.toString();

        Assert.assertEquals(actual, expected);
    }

    /**
     * Test constructor used in streams.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testStreamConstructor() {
        new Message((short) 0, "Bob"); // Expect Exception
    }
}
