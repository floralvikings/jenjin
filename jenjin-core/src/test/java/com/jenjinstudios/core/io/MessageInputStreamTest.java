package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Test the MessageInputStream class.
 * @author Caleb Brinkman
 */
@SuppressWarnings({"NumericCastThatLosesPrecision", "MagicNumber"})
public class MessageInputStreamTest
{

	private static final Logger LOGGER = Logger.getLogger(MessageInputStreamTest.class.getName());

	/**
	 * Register messages for testing purposes.
	 */
	@BeforeClass(groups = "unit")
	public void setUp() {
		MessageRegistry.getGlobalRegistry().register("Test Message Registry",
			  getClass().getClassLoader().getResourceAsStream("test/jenjinstudios/core/Messages.xml"));
		MessageRegistry.getGlobalRegistry().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass(groups = "unit")
	public void clearMessageRegistry() {
		MessageRegistry.getGlobalRegistry().clear();
	}

	/**
	 * Test the ability to read a valid message.
	 *
	 * @throws IOException If there's an IOException.
	 */
	@Test(groups = "unit")
	public void testReadValidMessage() throws IOException {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) 0);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) 0);

		InputStream inputStream = dataInputStreamMock.getIn();

		MessageInputStream messageInputStream = new MessageInputStream(inputStream);
		Message message = messageInputStream.readMessage();
		messageInputStream.close();

        Assert.assertEquals((String) message.getArgument("messageName"), "FooBar", "Argument names do not match.");
    }

	/**
	 * Test reading an invalid message.
	 * @throws IOException If there's an (unexpected) IOException.
	 */
	@Test(expectedExceptions = MessageTypeException.class, groups = "unit")
	public void testReadInvalidMessage() throws IOException {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -256); // Invalid message number
		mock.mockReadBoolean(false);
		mock.mockReadUtf("FooBar");
		mock.mockReadShort((short) -1);

		InputStream is = mock.getIn();

		MessageInputStream mis = new MessageInputStream(is);
		mis.readMessage();
	}

    /**
     * Test each type of message argument.
     * @throws Exception If there's an Exception.
	 */
	@Test(groups = "unit")
	public void testAllTypesMessage() throws Exception {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -4);
		mock.mockReadUtf("FooBar");
		mock.mockReadInt(123);
		mock.mockReadLong(456);
		mock.mockReadDouble(Math.random());
		mock.mockReadFloat((float) Math.random());
		mock.mockReadShort((short) 246);
		mock.mockReadBoolean(true);
		mock.mockReadByte((byte) 867);
		// Mock a byte array
		mock.mockReadInt(3);
		mock.mockReadByte((byte) 8);
		mock.mockReadByte((byte) 16);
		mock.mockReadByte((byte) 32);
		// Mock a String array
		mock.mockReadInt(3);
		mock.mockReadUtf("I'm");
		mock.mockReadUtf("A");
		mock.mockReadUtf("Lumberjack");

		InputStream in = mock.getIn();
		MessageInputStream mis = new MessageInputStream(in);
		Message msg = mis.readMessage();
		mis.close();

        Assert.assertEquals(((String[]) msg.getArgument("testStringArray"))[1], "A", "String array contents incorrect");
    }

}
