package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test the MessageInputStream class.
 * @author Caleb Brinkman
 */
public class MessageInputStreamTest
{

	private static final Logger LOGGER = Logger.getLogger(MessageInputStreamTest.class.getName());

	@Test
	public void testReadValidMessage() throws IOException {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);

		InputStream inputStream = dataInputStreamMock.getIn();

		MessageInputStream messageInputStream = new MessageInputStream(inputStream);
		Message message = messageInputStream.readMessage();
		messageInputStream.close();

		Assert.assertEquals((String) message.getArgument("messageName"), "FooBar");
	}

	@Test(expectedExceptions = MessageTypeException.class)
	public void testReadInvalidMessage() throws IOException {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -256); // Invalid message number
		mock.mockReadBoolean(false);
		mock.mockReadUtf("FooBar");
		mock.mockReadShort((short) -1);

		InputStream is = mock.getIn();

		MessageInputStream mis = new MessageInputStream(is);
		mis.readMessage();
	}

	@Test
	public void testEncryptedMessageNoAESKey() throws IOException {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -3);
		mock.mockReadBoolean(true);
		mock.mockReadUtf("FooBar");

		InputStream is = mock.getIn();
		MessageInputStream mis = new MessageInputStream(is);
		Message msg = mis.readMessage();
		mis.close();

		Assert.assertEquals(msg.getArgument("encryptedString"), "FooBar");
	}

	@Test
	public void testEncryptedMessage() throws Exception {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -3);

		KeyPair keyPair = generateRSAKeyPair();
		Cipher encryptCipher = Cipher.getInstance("RSA");
		encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

		byte[] sBytes = "FooBar".getBytes("UTF-8");
		String encryptedString = DatatypeConverter.printHexBinary(encryptCipher.doFinal(sBytes));

		Assert.assertNotEquals("FooBar", encryptedString);

		mock.mockReadBoolean(true);
		mock.mockReadUtf(encryptedString);

		InputStream is = mock.getIn();
		MessageInputStream mis = new MessageInputStream(is);
		mis.setPrivateKey(keyPair.getPrivate());
		Message msg = mis.readMessage();
		mis.close();

		Assert.assertEquals(msg.getArgument("encryptedString"), "FooBar");
	}

	@Test
	public void testAllTypesMessage() throws Exception {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -4);
		mock.mockReadBoolean(false);
		mock.mockReadUtf("FooBar");
		mock.mockReadInt(123);
		mock.mockReadLong(456);
		mock.mockReadDouble(Math.random());
		mock.mockReadFloat((float) Math.random());
		mock.mockReadShort((short) 246);
		mock.mockReadBoolean(true);
		mock.mockReadByte((byte) 867);
		// Mock a byte array
		mock.mockReadInt(3);
		mock.mockReadByte((byte) 8);
		mock.mockReadByte((byte) 16);
		mock.mockReadByte((byte) 32);
		// Mock a String array
		mock.mockReadInt(3);
		mock.mockReadBoolean(false);
		mock.mockReadUtf("I'm");
		mock.mockReadBoolean(false);
		mock.mockReadUtf("A");
		mock.mockReadBoolean(false);
		mock.mockReadUtf("Lumberjack");

		InputStream in = mock.getIn();
		MessageInputStream mis = new MessageInputStream(in);
		Message msg = mis.readMessage();
		mis.close();

		Assert.assertEquals(((String[]) msg.getArgument("testStringArray"))[1], "A");
	}

	private KeyPair generateRSAKeyPair() {
		KeyPair keyPair = null;
		try
		{
			KeyPairGenerator keyPairGenerator;
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}
		return keyPair;
	}
}
