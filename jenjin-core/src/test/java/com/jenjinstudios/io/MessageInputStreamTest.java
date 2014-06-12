package com.jenjinstudios.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the MessageInputStream class.
 * @author Caleb Brinkman
 */
public class MessageInputStreamTest
{
	@Test
	public void testReadMessage() throws IOException {
		InputStream inputStream = mock(InputStream.class);
		// Have to spoof this because... You just do.
		when(inputStream.read(new byte[80], 0, 6)).thenCallRealMethod();
		// Spoof -1 from readShort
		when(inputStream.read()).thenReturn(255).thenReturn(255).
				// Then spoof false from readBoolean
						thenReturn(0).
				// Then spoof "FooBar, which has to first pass a length of 6"
						thenReturn(0).thenReturn(6).
				// The UTF-8 characters for FooBar
						thenReturn(70).thenReturn(111).thenReturn(111).thenReturn(66).thenReturn(97).thenReturn(114).
				// Finally, spoof another -1 short.
						thenReturn(255).thenReturn(255);

		MessageRegistry messageRegistry = new MessageRegistry(false);

		MessageInputStream messageInputStream = new MessageInputStream(messageRegistry, inputStream);
		Message message = messageInputStream.readMessage();

		Assert.assertEquals((String) message.getArgument("messageName"), "FooBar");
		Assert.assertEquals((short) message.getArgument("messageID"), -1);
	}
}
