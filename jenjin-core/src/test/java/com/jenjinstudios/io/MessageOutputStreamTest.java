package com.jenjinstudios.io;

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
	@Test
	public void testWriteMessage() throws Exception {
		MessageRegistry mr = new MessageRegistry(false);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, bos);

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageID", (short) -255);
		msg.setArgument("messageName", "FooBar");

		mos.writeMessage(msg);
		byte[] bytes = bos.toByteArray();

		mos.close();

		// Have to read the data back out in order.
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		short id = dis.readShort();
		boolean encrypted = dis.readBoolean();
		String msgName = dis.readUTF();
		short invID = dis.readShort();

		Assert.assertEquals(id, msg.getID());
		Assert.assertEquals(invID, msg.getArgument("messageID"));
		Assert.assertEquals(encrypted, false);
		Assert.assertEquals(msgName, msg.getArgument("messageName"));
	}

	@Test(expectedExceptions = {IOException.class})
	public void testEncryptedMessageNoAESKey() throws Exception {
		MessageRegistry mr = new MessageRegistry(false);
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

		MessageRegistry mr = new MessageRegistry(false);
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
}
