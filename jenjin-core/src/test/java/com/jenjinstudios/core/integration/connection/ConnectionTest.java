package com.jenjinstudios.core.integration.connection;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

	/**
	 * Register messages used for testing.
	 */
	@BeforeClass(groups = "integration")
	public void registerTestMessages() {
		InputStream stream = getClass().
			  getClassLoader().
			  getResourceAsStream("test/jenjinstudios/core/integration/connection/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Integration test messages", stream);
	}

	/**
	 * Clear the message registry after testing.
	 */
	@AfterClass(groups = "integration")
	public void clearMessageRegistry() {
		MessageRegistry.getGlobalRegistry().clear();
	}

	/**
	 * Run the integration tests for Connection.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(groups = "integration")
	public void integrationTest() throws Exception {
		ConnectionPair connectionPair = new ConnectionPair();
		Connection connectionOne = connectionPair.getConnectionOne();
		Connection connectionTwo = connectionPair.getConnectionTwo();

		connectionOne.start();
		connectionTwo.start();

		// Give the connections time to set key pairs
		Thread.sleep(100);

		Message message = MessageRegistry.getGlobalRegistry().createMessage("Test");
		message.setArgument("encryptedString", "FooBar");

		connectionOne.enqueueMessage(message);

		// Give the second connection time to read the message
		Thread.sleep(100);
		Assert.assertEquals(connectionTwo.getMessageContext().getName(), "Message Executed", "Message not executed.");

		// Sleep for a while, then send another message to mimic real communication.
		Thread.sleep(100);
		connectionTwo.enqueueMessage(message);

		// Sleep to give the connection time to retrieve the message.
		Thread.sleep(100);
		Assert.assertEquals(connectionOne.getMessageContext().getName(), "Message Executed", "Message not executed.");

		// Make sure they can shut down w/o exceptions
		connectionOne.shutdown();
		connectionTwo.shutdown();
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
				try
				{
					socketArray[0] = serverSocket.accept();
					LOGGER.log(Level.INFO, "Server accepted socket");
				} catch (IOException e)
				{
					LOGGER.log(Level.SEVERE, "Couldn't create socket.");
				}
			});
			//noinspection CallToThreadStartDuringObjectConstruction
			listenThread.start();
			socketOne = new Socket("localhost", 51015);

			Thread.sleep(100);

			socketTwo = socketArray[0];
			if (socketTwo == null)
			{
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

		private ConnectionPair() throws IOException, InterruptedException {
			SocketPair socketPair = new SocketPair();
			Socket socketOne = socketPair.getSocketOne();
			Socket socketTwo = socketPair.getSocketTwo();

			MessageInputStream inputStreamOne = new MessageInputStream(socketOne.getInputStream());
			MessageOutputStream outputStreamOne = new MessageOutputStream(socketOne.getOutputStream());
			MessageStreamPair messageStreamPairOne = new MessageStreamPair(inputStreamOne, outputStreamOne);

			MessageInputStream inputStreamTwo = new MessageInputStream(socketTwo.getInputStream());
			MessageOutputStream outputStreamTwo = new MessageOutputStream(socketTwo.getOutputStream());
			MessageStreamPair messageStreamPairTwo = new MessageStreamPair(inputStreamTwo, outputStreamTwo);

			connectionOne = new Connection<>(messageStreamPairOne, new MessageContext());
			connectionTwo = new Connection<>(messageStreamPairTwo, new MessageContext());
		}
	}
}
