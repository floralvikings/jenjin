package com.jenjinstudios.client.net;

import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class AuthClientTest
{

	@Test
	public void testSendBlockingLoginRequest() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		MessageInputStream mis = Mockito.mock(MessageInputStream.class);
		MessageOutputStream mos = Mockito.mock(MessageOutputStream.class);

		Message firstConnectResponse = mr.createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", 123);

		Message blankMessage = mr.createMessage("BlankMessage");

		OngoingStubbing<Message> isWhen = Mockito.when(mis.readMessage()).
				thenReturn(firstConnectResponse).thenReturn(blankMessage);
		MessageIO messageIO = new MessageIO(mis, mos, mr);
		AuthClient client = new AuthClient(messageIO, new ClientUser("foo", "bar"));

		// Get client key and make a message for it
		byte[] clientKey = client.getClientPublicKey().getEncoded();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientKey));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedAESKey = cipher.doFinal(aesKeyBytes);
		Message aesMessage = mr.createMessage("AESKeyMessage");
		aesMessage.setArgument("key", encryptedAESKey);
		isWhen = isWhen.thenReturn(aesMessage).thenReturn(blankMessage);

		client.blockingStart();

		Message loginResponse = mr.createMessage("LoginResponse");
		loginResponse.setArgument("success", true);
		loginResponse.setArgument("loginTime", 1234l);

		isWhen.thenReturn(loginResponse);
		Assert.assertTrue(client.sendBlockingLoginRequest());
		Assert.assertEquals(client.getLoggedInTime(), 1234l);
	}

	@Test
	public void testSendBlockingLogoutRequest() throws Exception {
		MessageRegistry mr = new MessageRegistry();
		MessageInputStream mis = Mockito.mock(MessageInputStream.class);
		MessageOutputStream mos = Mockito.mock(MessageOutputStream.class);

		Message firstConnectResponse = mr.createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", 123);

		Message blankMessage = mr.createMessage("BlankMessage");

		OngoingStubbing<Message> isWhen = Mockito.when(mis.readMessage()).
				thenReturn(firstConnectResponse).thenReturn(blankMessage);
		MessageIO messageIO = new MessageIO(mis, mos, mr);
		AuthClient client = new AuthClient(messageIO, new ClientUser("foo", "bar"));

		// Get client key and make a message for it
		byte[] clientKey = client.getClientPublicKey().getEncoded();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientKey));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedAESKey = cipher.doFinal(aesKeyBytes);
		Message aesMessage = mr.createMessage("AESKeyMessage");
		aesMessage.setArgument("key", encryptedAESKey);
		isWhen = isWhen.thenReturn(aesMessage).thenReturn(blankMessage);

		client.blockingStart();

		Message loginResponse = mr.createMessage("LogoutResponse");
		loginResponse.setArgument("success", true);

		isWhen.thenReturn(loginResponse);
		Assert.assertTrue(client.sendBlockingLogoutRequest());
	}
}