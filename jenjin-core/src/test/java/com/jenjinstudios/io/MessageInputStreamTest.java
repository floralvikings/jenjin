package com.jenjinstudios.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test the MessageInputStream class.
 * @author Caleb Brinkman
 */
public class MessageInputStreamTest
{
	@Test
	public void testReadValidMessage() throws IOException {
		DataInputStreamMock dataInputStreamMock = new DataInputStreamMock();
		dataInputStreamMock.mockReadShort((short) -1);
		dataInputStreamMock.mockReadBoolean(false);
		dataInputStreamMock.mockReadUtf("FooBar");
		dataInputStreamMock.mockReadShort((short) -1);

		InputStream inputStream = dataInputStreamMock.getIn();

		MessageRegistry messageRegistry = new MessageRegistry();

		MessageInputStream messageInputStream = new MessageInputStream(messageRegistry, inputStream);
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

		MessageRegistry mr = new MessageRegistry();

		MessageInputStream mis = new MessageInputStream(mr, is);
		mis.readMessage();
	}

	@Test
	public void testEncryptedMessageNoAESKey() throws IOException {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -3);
		mock.mockReadBoolean(true);
		mock.mockReadUtf("FooBar");

		InputStream is = mock.getIn();
		MessageRegistry mr = new MessageRegistry();
		MessageInputStream mis = new MessageInputStream(mr, is);
		Message msg = mis.readMessage();
		mis.close();

		Assert.assertEquals(msg.getArgument("encryptedString"), "FooBar");
	}

	@Test
	public void testEncryptedMessage() throws Exception {
		DataInputStreamMock mock = new DataInputStreamMock();
		mock.mockReadShort((short) -3);

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] key = keyGenerator.generateKey().getEncoded();
		SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
		Cipher aesEncryptCipher = Cipher.getInstance("AES");
		aesEncryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] sBytes = "FooBar".getBytes("UTF-8");
		String encryptedString = DatatypeConverter.printHexBinary(
				aesEncryptCipher.doFinal(sBytes));

		Assert.assertNotEquals("FooBar", encryptedString);

		mock.mockReadBoolean(true);
		mock.mockReadUtf(encryptedString);

		InputStream is = mock.getIn();
		MessageRegistry mr = new MessageRegistry();
		MessageInputStream mis = new MessageInputStream(mr, is);
		mis.setAESKey(key);
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
		mock.mockReadDouble(7.89);
		mock.mockReadFloat(0.123f);
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
		MessageRegistry mr = new MessageRegistry();
		MessageInputStream mis = new MessageInputStream(mr, in);
		Message msg = mis.readMessage();
		mis.close();

		Assert.assertEquals(((String[]) msg.getArgument("testStringArray"))[1], "A");
	}
}
