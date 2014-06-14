package com.jenjinstudios.io;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

/**
 * @author Caleb Brinkman
 */
public class MessageOutputStreamTest
{
	@Test
	public void testWriteMessage() throws Exception {
		MessageRegistry mr = new MessageRegistry(false);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessageOutputStream mos = new MessageOutputStream(mr, os);

		Message msg = mr.createMessage("InvalidMessage");
		msg.setArgument("messageID", (short) -255);
		msg.setArgument("messageName", "FooBar");

		mos.writeMessage(msg);
		byte[] bytes = os.toByteArray();

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

	@Test
	public void testEncryptedMessageNoAESKey() throws Exception {

	}
}
