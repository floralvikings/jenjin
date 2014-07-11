package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Caleb Brinkman
 */
public class ClientTest
{
	private static MessageRegistry mr = MessageRegistry.getInstance();

	@Test
	public void testAddRepeatedTask() {
		Runnable r = Mockito.mock(Runnable.class);
		MessageInputStream in = Mockito.mock(MessageInputStream.class);
		MessageOutputStream out = Mockito.mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(in, out, mr);
		Client client = new Client(messageIO);
		client.addRepeatedTask(r);
		client.runRepeatedTasks();
		Mockito.verify(r).run();
	}

	@Test
	public void testDoPostConnectInit() throws Exception {
		int ups = 100;
		int period = 1000 / ups;
		// Build a FirstConnectResponse message
		Message fcr = mr.createMessage("FirstConnectResponse");
		fcr.setArgument("ups", ups);

		// Mock a stream containing a FirstConnectResponse
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);
		mos.writeMessage(fcr);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

		MessageInputStream in = new MessageInputStream(mr, bis);
		MessageOutputStream out = new MessageOutputStream(mr, bos);
		MessageIO messageIO = new MessageIO(in, out, mr);
		Client client = new Client(messageIO);
		client.run();

		Assert.assertEquals(client.getPeriod(), period);
	}

	@Test
	public void testBlockingStart() throws Exception {
		int ups = 100;

		// Build a FirstConnectResponse message
		Message fcr = mr.createMessage("FirstConnectResponse");
		fcr.setArgument("ups", ups);

		MessageInputStream in = Mockito.mock(MessageInputStream.class);
		MessageOutputStream out = Mockito.mock(MessageOutputStream.class);

		OngoingStubbing<Message> inReturn = Mockito.when(in.readMessage()).thenReturn(fcr);
		MessageIO messageIO = new MessageIO(in, out, mr);
		Client client = new Client(messageIO);

		// Nastiness.
		byte[] clientKey = client.getPublicKey().getEncoded();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientKey));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedAESKey = cipher.doFinal(aesKeyBytes);
		Message aesMessage = mr.createMessage("AESKeyMessage");
		// Construct the AESKeyMessage
		aesMessage.setArgument("key", encryptedAESKey);

		inReturn.thenReturn(aesMessage);

		Assert.assertTrue(client.blockingStart());
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testInvalidDoPostConnectInit() throws Exception {
		int ups = 100;
		Message fcr = mr.createMessage("FirstConnectResponse");
		fcr.setArgument("ups", ups);

		// Mock a stream containing a FirstConnectResponse
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);
		mos.writeMessage(fcr);
		mos.writeMessage(fcr);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		// Mock a socket which returns the mocked stream.
		MessageInputStream in = new MessageInputStream(mr, bis);
		MessageOutputStream out = new MessageOutputStream(mr, bos);
		MessageIO messageIO = new MessageIO(in, out, mr);
		Client client = new Client(messageIO);
		client.run();
	}
}
