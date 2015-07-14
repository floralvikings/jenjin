package com.jenjinstudios.core.integration.connection;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.connection.ConnectionConfig;
import com.jenjinstudios.core.connection.ConnectionConfigReader;
import com.jenjinstudios.core.connection.ConnectionInstantiationException;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fully test the Connection class, by creating two sockets for IO and testing various functionality.
 *
 * @author Caleb Brinkman
 */
public class ConnectionTest
{
	private static final Logger LOGGER = Logger.getLogger(ConnectionTest.class.getName());
	private static final String CONFIG_STRING = "{\n" +
		  "\"secure\":\"false\",\n" +
		  "\"address\":\"127.0.0.1\",\n" +
		  "\"port\":\"1234\",\n" +
		  "\"messageRegistryFiles\":[\n" +
		  	"\"test/jenjinstudios/core/integration/connection/Messages.xml\"," +
		  	"\"com/jenjinstudios/core/io/Messages.xml\"" +
		  "], \n" +
		  "\"contextClass\":\"" + MessageContext.class.getName() + "\"\n" +
		  '}';
	private Connection connectionOne;
	private Connection connectionTwo;

	/**
	 * Register messages used for testing, and set up a pair of connections for testing.
	 *
	 * @throws Exception If there's an exception during setup.
	 */
	@BeforeClass(groups = "integration")
	public void registerTestMessages() throws Exception {
		InputStream stream = getClass().
			  getClassLoader().
			  getResourceAsStream("test/jenjinstudios/core/integration/connection/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Integration test messages", stream);
		ConnectionPair connectionPair = new ConnectionPair();
		connectionOne = connectionPair.getConnectionOne();
		connectionTwo = connectionPair.getConnectionTwo();
	}

	/**
	 * Clear the message registry after testing.
	 */
	@AfterClass(groups = "integration")
	public void clearMessageRegistry() {
		// Make sure they can shut down w/o exceptions
		connectionOne.shutdown();
		connectionTwo.shutdown();

		MessageRegistry.getGlobalRegistry().clear();
	}

	/**
	 * Run the integration tests for Connection.  This test will send a series of messages back and forth, stopping
	 * until certain conditions are met.  If one of these hangs, the test will be stuck until the timeout, causing
	 * it to
	 * fail.
	 *
	 * @throws Exception If there's an exception.
	 */
	@SuppressWarnings({"BusyWait", "MethodWithMoreThanThreeNegations"})
	@Test(groups = "integration", timeOut = 30000)
	public void integrationTest() throws Exception {
		connectionOne.start();
		connectionTwo.start();

		// Give the connections time to set key pairs
		Thread.sleep(100);

		Message message = MessageRegistry.getGlobalRegistry().createMessage("Test");
		message.setArgument("encryptedString", "FooBar");

		LOGGER.log(Level.INFO, "Enqueueing first message to connectionTwo");
		connectionOne.enqueueMessage(message);
		while (!"FooBar".equals(connectionTwo.getMessageContext().getName())) { Thread.sleep(10); }
		LOGGER.log(Level.INFO, "connectionTwo received and executed first message");
		LOGGER.log(Level.INFO, "Enqueueing first message to connectionOne");
		connectionTwo.enqueueMessage(message);
		while (!"FooBar".equals(connectionOne.getMessageContext().getName())) { Thread.sleep(10); }
		LOGGER.log(Level.INFO, "connectionOne received and executed first message");
		LOGGER.log(Level.INFO, "Sending message with no valid executable to connectionTwo");
		Message errorCausing = MessageRegistry.getGlobalRegistry().createMessage("Malformed");
		errorCausing.setArgument("someString", "Baz");
		connectionOne.enqueueMessage(errorCausing);
		connectionTwo.getMessageContext().setName("Reset");
		connectionOne.getMessageContext().setName("Reset");
		connectionOne.enqueueMessage(message);
		while (!"FooBar".equals(connectionTwo.getMessageContext().getName())) { Thread.sleep(10); }
		connectionTwo.enqueueMessage(message);
		while (!"FooBar".equals(connectionOne.getMessageContext().getName())) { Thread.sleep(10); }
		LOGGER.log(Level.INFO, "Neither connection shutdown after sending and receiving errored message");
	}

	private static class SocketPair
	{
		private final Socket socketOne;
		private final Socket socketTwo;

		public Socket getSocketOne() {
			return socketOne;
		}

		public Socket getSocketTwo() {
			return socketTwo;
		}

		private SocketPair() throws IOException, InterruptedException {
			ServerSocket serverSocket = new ServerSocket(51015, 0, InetAddress.getByName(null));
			final Socket[] socketArray = new Socket[1];

			Thread listenThread = new Thread(() -> {
				try {
					socketArray[0] = serverSocket.accept();
					LOGGER.log(Level.INFO, "Server accepted socket");
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Couldn't create socket.");
				}
			});
			//noinspection CallToThreadStartDuringObjectConstruction
			listenThread.start();
			socketOne = new Socket("localhost", 51015);

			Thread.sleep(100);

			socketTwo = socketArray[0];
			if (socketTwo == null) {
				throw new IOException("Couldn't create socket.");
			}
		}
	}

	private static class ConnectionPair
	{
		private final Connection connectionOne;
		private final Connection connectionTwo;

		public Connection getConnectionOne() {
			return connectionOne;
		}

		public Connection getConnectionTwo() {
			return connectionTwo;
		}

		private ConnectionPair() throws IOException, InterruptedException, ConnectionInstantiationException {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CONFIG_STRING.getBytes());
			ConnectionConfigReader reader = new ConnectionConfigReader(byteArrayInputStream);
			ConnectionConfig config = reader.read(ConnectionConfig.class);

			SocketPair socketPair = new SocketPair();
			Socket socketOne = socketPair.getSocketOne();
			Socket socketTwo = socketPair.getSocketTwo();

			MessageInputStream inputStreamOne = new MessageInputStream(socketOne.getInputStream());
			MessageOutputStream outputStreamOne = new MessageOutputStream(socketOne.getOutputStream());

			MessageInputStream inputStreamTwo = new MessageInputStream(socketTwo.getInputStream());
			MessageOutputStream outputStreamTwo = new MessageOutputStream(socketTwo.getOutputStream());

			connectionOne = new Connection<>(config, inputStreamOne, outputStreamOne);
			connectionTwo = new Connection<>(config, inputStreamTwo, outputStreamTwo);
		}
	}
}
