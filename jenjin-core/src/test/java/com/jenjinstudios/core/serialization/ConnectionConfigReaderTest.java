package com.jenjinstudios.core.serialization;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.connection.ConnectionConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

/**
 * Test the ConnectionConfigReader class.
 *
 * @author Caleb Brinkman
 */
public class ConnectionConfigReaderTest
{
	/**
	 * Test the read method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "unit")
	public void testRead() throws Exception {
		String testJson = "{\n" +
			  "\"secure\":\"false\",\n" +
			  "\"address\":\"127.0.0.1\",\n" +
			  "\"port\":\"1234\",\n" +
			  "\"messageRegistryFiles\":[], \n" +
			  "\"contextClass\":\"" + MessageContext.class.getName() + "\"\n" +
			  '}';
		ByteArrayInputStream inputStream = new ByteArrayInputStream(testJson.getBytes());
		ConnectionConfigReader<MessageContext> reader = new ConnectionConfigReader<>(inputStream);
		ConnectionConfig<MessageContext> connectionConfig = reader.read();
		Assert.assertFalse(connectionConfig.isSecure(), "Should not be secure");
		Assert.assertEquals(connectionConfig.getAddress(), InetAddress.getLoopbackAddress(), "Address not correct");
		Assert.assertTrue(connectionConfig.getMessageRegistryFiles().isEmpty(), "Message registry should be empty");
		Assert.assertEquals(connectionConfig.getPort(), 1234, "Port should be 1234");
		Assert.assertEquals(connectionConfig.getContextClass(), MessageContext.class, "MessageContext incorrect");
	}
}
