package com.jenjinstudios.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.net.ClientHandler;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
public class ServerMessageFactoryTest
{
	@Test
	public void testGenerateFirstConnectResponse() {
		ClientHandler clientHandler = Mockito.mock(ClientHandler.class);
		ServerMessageFactory serverMessageFactory =
			  new ServerMessageFactory(clientHandler, MessageRegistry.getInstance());
		Message message = serverMessageFactory.generateFirstConnectResponse(100);

		assertEquals(message.name, "FirstConnectResponse");
		assertEquals(message.getArgument("ups"), 100);
	}

	@Test
	public void testGenerateLogoutResponse() {
		ClientHandler clientHandler = Mockito.mock(ClientHandler.class);
		ServerMessageFactory serverMessageFactory =
			  new ServerMessageFactory(clientHandler, MessageRegistry.getInstance());
		Message message = serverMessageFactory.generateLogoutResponse(true);

		assertEquals(message.name, "LogoutResponse");
		assertEquals(message.getArgument("success"), true);
	}

	@Test
	public void testGenerateAESKeyMessage() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		byte[] publicKeyEncoded = publicKey.getEncoded();

		ClientHandler clientHandler = Mockito.mock(ClientHandler.class);
		ServerMessageFactory serverMessageFactory =
			  new ServerMessageFactory(clientHandler, MessageRegistry.getInstance());
		Message message = serverMessageFactory.generateAESKeyMessage(publicKeyEncoded);

		assertEquals(message.name, "AESKeyMessage");
		assertEquals(((byte[]) message.getArgument("key")).length, 64);
	}
}
