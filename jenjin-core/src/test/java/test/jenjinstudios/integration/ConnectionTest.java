package test.jenjinstudios.integration;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
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
	 * Run the integration tests for Connection.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void integrationTest() throws Exception {
		InputStream stream = getClass().
			  getClassLoader().
			  getResourceAsStream("test/jenjinstudios/integration/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("Integration test messages", stream);

		SocketPair socketPair = new SocketPair();
		socketPair.invoke();
		Socket socketOne = socketPair.getSocketOne();
		Socket socketTwo = socketPair.getSocketTwo();

		MessageInputStream inputStreamOne = new MessageInputStream(socketOne.getInputStream());
		MessageOutputStream outputStreamOne = new MessageOutputStream(socketOne.getOutputStream());
		MessageIO messageIOOne = new MessageIO(inputStreamOne, outputStreamOne);

		MessageInputStream inputStreamTwo = new MessageInputStream(socketTwo.getInputStream());
		MessageOutputStream outputStreamTwo = new MessageOutputStream(socketTwo.getOutputStream());
		MessageIO messageIOTwo = new MessageIO(inputStreamTwo, outputStreamTwo);

		Connection connectionOne = new Connection(messageIOOne);
		Connection connectionTwo = new Connection(messageIOTwo);

		connectionOne.start();
		connectionTwo.start();

		KeyPair keyPairOne = Connection.generateRSAKeyPair();
		KeyPair keyPairTwo = Connection.generateRSAKeyPair();

		connectionOne.setRSAKeyPair(keyPairOne);
		connectionTwo.setRSAKeyPair(keyPairTwo);

		// Give the connections time to set key pairs
		Thread.sleep(1000);

		Message message = MessageRegistry.getGlobalRegistry().createMessage("Test");
		message.setArgument("encryptedString", "FooBar");

		connectionOne.enqueueMessage(message);

		// Give the second connection time to read the message
		Thread.sleep(100);
		Assert.assertEquals(connectionTwo.getName(), "FooBar", "Connection name should be set by executable message.");

		MessageRegistry.getGlobalRegistry().clear();
	}

	private static class SocketPair
	{
		private Socket socketOne;
		private Socket socketTwo;

		public Socket getSocketOne() {
			return socketOne;
		}

		public Socket getSocketTwo() {
			return socketTwo;
		}

		public void invoke() throws IOException, InterruptedException {
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
}
