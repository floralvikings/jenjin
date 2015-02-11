package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test the {@code MessageOutputStream} class.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("MagicNumber")
public class MessageOutputStreamTest
{
	private static final MessageRegistry mr = MessageRegistry.getInstance();
	private static final Logger LOGGER = Logger.getLogger(MessageOutputStreamTest.class.getName());

	/**
	 * Test writing a message to the stream.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testWriteMessage() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(bos);

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageID", (short) -255);
		msg.setArgument("messageName", "FooBar");

		mos.writeMessage(msg);
		byte[] bytes = bos.toByteArray();
		mos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		MessageInputStream mis = new MessageInputStream(bis);
		Message readMsg = mis.readMessage();
		Assert.assertEquals(readMsg.getArgs(), msg.getArgs());
	}

	/**
	 * Test writing an encrypted message with no key sent.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test(expectedExceptions = {IOException.class})
	public void testEncryptedMessageNoPublicKey() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(bos);

		Message msg = mr.createMessage("TestEncryptedMessage");
		msg.setArgument("encryptedString", "FooBar");

		mos.writeMessage(msg);
	}

	/**
	 * Test sending a correct encrypted message.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testEncryptedMessage() throws Exception {

		KeyPair keyPair = generateRSAKeyPair();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(bos);
		mos.setPublicKey(keyPair.getPublic());

		Message msg = mr.createMessage("TestEncryptedMessage");
		msg.setArgument("encryptedString", "FooBar");

		mos.writeMessage(msg);
		byte[] bytes = bos.toByteArray();
		mos.close();

		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		short id = dis.readShort();
		boolean encrypted = dis.readBoolean();
		String encStr = dis.readUTF();

		Assert.assertEquals(id, msg.getID());
		Assert.assertTrue(encrypted);
		Assert.assertNotEquals(encStr, msg.getArgument("encryptedString"));

	}

	/**
	 * Test writing a message with each type of argument.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testAllTypesMessage() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(bos);
		Message msg = mr.createMessage("TestAllTypesMessage");
		msg.setArgument("testString", "SNAFU");
		msg.setArgument("testInt", 123);
		msg.setArgument("testLong", 456l);
		msg.setArgument("testDouble", 4.567);
		msg.setArgument("testFloat", 0.123f);
		msg.setArgument("testShort", (short) 789);
		msg.setArgument("testBoolean", true);
		msg.setArgument("testByte", (byte) 101);
		msg.setArgument("testByteArray", new byte[]{2, 3, 5, 7, 11});
		msg.setArgument("testStringArray", new String[]{"Foo", "Bar"});
		mos.writeMessage(msg);
		byte[] bytes = bos.toByteArray();
		mos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		MessageInputStream mis = new MessageInputStream(bis);
		Message readMsg = mis.readMessage();
		Assert.assertEquals(readMsg.getArgs(), msg.getArgs());
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
