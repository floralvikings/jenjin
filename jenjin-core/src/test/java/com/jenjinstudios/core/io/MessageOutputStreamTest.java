package com.jenjinstudios.core.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.KeyGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Caleb Brinkman
 */
public class MessageOutputStreamTest
{
	private static final MessageRegistry mr = MessageRegistry.getInstance();

	@Test
	public void testWriteMessage() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageID", (short) -255);
		msg.setArgument("messageName", "FooBar");

		mos.writeMessage(msg);
		byte[] bytes = bos.toByteArray();
		mos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		MessageInputStream mis = new MessageInputStream(mr, bis);
		Message readMsg = mis.readMessage();
		Assert.assertEquals(readMsg.getArgs(), msg.getArgs());
	}

	@Test(expectedExceptions = {IOException.class})
	public void testEncryptedMessageNoAESKey() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);

		Message msg = mr.createMessage("TestEncryptedMessage");
		msg.setArgument("encryptedString", "FooBar");

		mos.writeMessage(msg);
	}

	@Test
	public void testEncryptedMessage() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] key = keyGenerator.generateKey().getEncoded();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);
		mos.setAesKey(key);

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

	@Test
	public void testAllTypesMessage() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);
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
		MessageInputStream mis = new MessageInputStream(mr, bis);
		Message readMsg = mis.readMessage();
		Assert.assertEquals(readMsg.getArgs(), msg.getArgs());
	}
}
