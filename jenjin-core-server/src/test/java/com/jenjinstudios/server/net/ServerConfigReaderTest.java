package com.jenjinstudios.server.net;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

/**
 * Test reading the ServerConfig class.
 *
 * @author Caleb Brinkman
 */
public class ServerConfigReaderTest
{
	/**
	 * Test the read method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRead() throws Exception {
		String testJson = "{\n" +
			  "\"secure\":\"false\",\n" +
			  "\"address\":\"127.0.0.1\",\n" +
			  "\"port\":\"1234\",\n" +
			  "\"messageRegistryFiles\":[], \n" +
			  "\"contextClass\":\"" + ServerMessageContext.class.getName() + "\", \n" +
			  "\"updateTasks\":[], \n" +
			  "\"shutdownTasks\":[], \n" +
			  "\"connectionAddedTasks\":[] \n" +
			  '}';
		ByteArrayInputStream inputStream = new ByteArrayInputStream(testJson.getBytes());
		ServerConfigReader reader = new ServerConfigReader(inputStream);
		ServerConfig connectionConfig = reader.read(ServerConfig.class);

		Assert.assertFalse(connectionConfig.isSecure(), "Should not be secure");
		Assert.assertEquals(connectionConfig.getAddress(), InetAddress.getLoopbackAddress(), "Address not correct");
		Assert.assertTrue(connectionConfig.getMessageRegistryFiles().isEmpty(), "Message registry should be empty");
		Assert.assertEquals(connectionConfig.getPort(), 1234, "Port should be 1234");
		Assert.assertEquals(connectionConfig.getContextClass(), ServerMessageContext.class, "Context incorrect");
		Assert.assertNotNull(connectionConfig.getConnectionAddedTasks(), "Tasks should not be null");
	}
}
