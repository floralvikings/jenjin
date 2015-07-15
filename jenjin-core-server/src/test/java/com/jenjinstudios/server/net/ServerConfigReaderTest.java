package com.jenjinstudios.server.net;

import com.jenjinstudios.server.serialization.ServerConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

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
		String testJson = "{\n " +
			  "\"connectionConfig\": {" +
			  "\"secure\":\"true\",\n" +
			  "\"address\":\"127.0.0.1\",\n" +
			  "\"port\":\"1234\",\n" +
			  "\"messageRegistryFiles\":[], \n" +
			  "\"contextClass\":\"" + ServerMessageContext.class.getName() + "\" \n" +
			  "}, \n" +
			  "\"updateTasks\":[], \n" +
			  "\"shutdownTasks\":[ \n" +
			  "\"com.jenjinstudios.server.net.EmergencyLogoutTask\" \n" +
			  "], \n" +
			  "\"connectionAddedTasks\":[] \n" +
			  '}';
		ByteArrayInputStream inputStream = new ByteArrayInputStream(testJson.getBytes());
		ServerConfigReader reader = new ServerConfigReader(inputStream);
		ServerConfig serverConfig = reader.read();
		Assert.assertFalse(serverConfig.getShutdownTasks().isEmpty(), "Tasks should not be empty");
		Assert.assertNotNull(serverConfig.getConnectionConfig(), "Connection config should not be null");
		Assert.assertTrue(serverConfig.getConnectionConfig().isSecure(), "Connection should be secure");
	}
}
