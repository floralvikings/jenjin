package com.jenjinstudios.io;

import com.jenjinstudios.message.FileMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles the sending and reception of messages registered in the MessageRegistry class.
 *
 * @author Caleb Brinkman
 */
public class MessageOutputStream extends DataOutputStream
{

	/**
	 * Creates a new message output stream to write data to the specified
	 * underlying output stream. The counter {@code written} is
	 * set to zero.
	 *
	 * @param out the underlying output stream, to be saved for later
	 *            use.
	 * @see java.io.FilterOutputStream#out
	 */
	public MessageOutputStream(OutputStream out)
	{
		super(out);
	}

	/**
	 * Write the given {@code BaseMessage} to the output stream.
	 *
	 * @param message The BaseMessage to be written to the stream.
	 * @throws IOException If there is an IO error.
	 */
	public void writeMessage(BaseMessage message) throws IOException
	{
		Object[] args = message.getArgs();
		int id = message.getID();
		writeShort(id);
		if (message instanceof FileMessage)
			System.out.println();
		for (Object arg : args)
			writeArgument(arg);
	}

	/**
	 * Write an argument to the data stream, properly cast.
	 *
	 * @param arg The argument to be written.
	 * @throws IOException If there is an IO error.
	 */
	private void writeArgument(Object arg) throws IOException
	{
		if (arg instanceof String) writeUTF((String) arg);
		else if (arg instanceof Integer) writeInt((int) arg);
		else if (arg instanceof Long) writeLong((long) arg);
		else if (arg instanceof Float) writeFloat((float) arg);
		else if (arg instanceof Double) writeDouble((double) arg);
		else if (arg instanceof Boolean) writeBoolean((boolean) arg);
		else if (arg instanceof Byte) writeByte((byte) arg);
		else if (arg instanceof byte[]) writeByteArray((byte[]) arg);
		else if (arg instanceof String[]) writeStringArray((String[]) arg);
	}

	/**
	 * Write an array of strings to the output stream, preceded by the array length.
	 *
	 * @param strings The array of string strings.
	 * @throws IOException If there is an IO error.
	 */
	private void writeStringArray(String[] strings) throws IOException
	{
		int stringsLength = strings.length;
		writeInt(stringsLength);
		for (String string : strings) writeUTF(string);
	}

	/**
	 * Write an array of bytes to the output stream, preceded by the array length.
	 *
	 * @param bytes The array of byte bytes.
	 * @throws IOException If there is an IO error.
	 */
	private void writeByteArray(byte[] bytes) throws IOException
	{
		int bytesLength = bytes.length;
		writeInt(bytesLength);
		write(bytes);
	}
}
