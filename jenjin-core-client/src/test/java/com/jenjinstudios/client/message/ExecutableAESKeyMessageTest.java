package com.jenjinstudios.client.message;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableAESKeyMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PrivateKey clientPrivateKey = keyPair.getPrivate();
		byte[] clientPublicKey = keyPair.getPublic().getEncoded();
		byte[] decryptedAESKey = keyGenerator.generateKey().getEncoded();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientPublicKey));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedAESKey = cipher.doFinal(decryptedAESKey);

		Message message = MessageRegistry.getInstance().createMessage("AESKeyMessage");
		message.setArgument("key", encryptedAESKey);

		Client client = mock(Client.class);
		when(client.getClientPrivateKey()).thenReturn(clientPrivateKey);

		ExecutableAESKeyMessage executableAESKeyMessage = new ExecutableAESKeyMessage(client, message);
		executableAESKeyMessage.runImmediate();
		executableAESKeyMessage.runDelayed();

		verify(client).setAESKey(decryptedAESKey);
	}
}
