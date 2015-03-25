package test.jenjinstudios.integration;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;

/**
 * Fully test the Connection class, by creating two sockets for IO and testing various functionality.
 *
 * @author Caleb Brinkman
 */
public class ConnectionTest
{
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

		ServerSocket serverSocket = new ServerSocket(51015);
		Socket socketOne = new Socket("localhost", 51015);
		Socket socketTwo = serverSocket.accept();

		MessageInputStream inputStreamOne = new MessageInputStream(socketOne.getInputStream());
		MessageOutputStream outputStreamOne = new MessageOutputStream(socketTwo.getOutputStream());
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

		Message message = MessageRegistry.getGlobalRegistry().createMessage("Test");
		message.setArgument("encryptedString", "FooBar");

		connectionOne.enqueueMessage(message);

		// Give the second connection time to read the message
		Thread.sleep(100);
		Assert.assertEquals(connectionTwo.getName(), "FooBar", "Connection name should be set by executable message.");

		MessageRegistry.getGlobalRegistry().clear();
	}
}
