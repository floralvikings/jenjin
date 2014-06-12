package com.jenjinstudios.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test the MessageInputStream class.
 * @author Caleb Brinkman
 */
public class MessageInputStreamTest
{
	@Test
	public void testReadValidMessage() throws IOException {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);

		InputStream inputStream = dataInputStreamMock.getWhen().getMock();

		MessageRegistry messageRegistry = new MessageRegistry(false);

		MessageInputStream messageInputStream = new MessageInputStream(messageRegistry, inputStream);
		Message message = messageInputStream.readMessage();
		messageInputStream.close();

		System.out.println(message);

		Assert.assertEquals((String) message.getArgument("messageName"), "FooBar");
	}

	@Test
	public void testReadInvalidMessage() throws IOException {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -256); // Invalid message number
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);

		InputStream inputStream = dataInputStreamMock.getWhen().getMock();

		MessageRegistry messageRegistry = new MessageRegistry(false);

		MessageInputStream messageInputStream = new MessageInputStream(messageRegistry, inputStream);
		Message message = messageInputStream.readMessage();
		messageInputStream.close();

		Assert.assertNull(message);
	}
}
