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
    private static final MessageRegistry MESSAGE_REGISTRY = MessageRegistry.getInstance();

    /**
     * Test creating a message with an invalid name.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidName() {
        Message msg = MESSAGE_REGISTRY.createMessage("InvalidMessage");
        msg.setArgument("FooBar", 1337); // Expect exception
    }

    /**
     * Test creating a message with an invalid argument.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidArgumentType() {
        Message msg = MESSAGE_REGISTRY.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.setArgument("messageID", "I'm totally a short, you guys."); // Expect exception
    }

    /**
     * Test retrieving message arguments with arguments unset.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnsetArgs() {
        Message msg = MESSAGE_REGISTRY.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.getArgs(); // Expect exception
    }

    /**
     * Test message toString method.
     */
    @Test
    public void testToString() {
        Message msg = MESSAGE_REGISTRY.createMessage("InvalidMessage");
        msg.setArgument("messageName", "FooBar");
        msg.setArgument("messageID", (short) -255);

        String expected = "Message 0 InvalidMessage";
        String actual = msg.toString();

        Assert.assertEquals(actual, expected);
    }

    /**
     * Test constructor used in streams.
     *
     * @return Nothing.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public Object testStreamConstructor() {
        return new Message((short) 0, "Bob"); // Expect Exception
    }
}
