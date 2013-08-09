package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.ArgumentType;
import com.jenjinstudios.jgcf.message.Message;
import com.jenjinstudios.jgcf.message.MessageRegistry;
import com.jenjinstudios.jgcf.message.MessageType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles the sending and reception of messages registered in the MessageRegistry class.
 *
 * @author Caleb Brinkman
 */
public class MessageOutputStream
{
	/** The output stream used by this message stream. */
	private final DataOutputStream outputStream;

	/**
	 * Creates a new message output stream to write data to the specified
	 * underlying output stream. The counter {@code written} is
	 * set to zero.
	 *
	 * @param out the underlying output stream, to be saved for later
	 *            use.
	 * @throws java.io.IOException If there is an error sending the public key.
	 * @see java.io.FilterOutputStream#out
	 */
	public MessageOutputStream(OutputStream out) throws IOException
	{
		outputStream = new DataOutputStream(out);
	}

	/**
	 * Write the given {@code Message} to the output stream.
	 *
	 * @param message The Message to be written to the stream.
	 * @throws IOException If there is an IO error.
	 */
	public void writeMessage(Message message) throws IOException
	{
		Object[] args = message.getArgs();
		MessageType messageType = MessageRegistry.getMessageType(message.getID());
		ArgumentType[] argumentTypes = messageType.argumentTypes;
		int id = message.getID();
		outputStream.writeShort(id);
		for (int i = 0; i < args.length; i++)
			writeArgument(args[i], argumentTypes[i].encrypt);
	}

	/**
	 * Write an argument to the data stream, properly cast.
	 *
	 * @param arg            The argument to be written.
	 * @param encryptStrings Whether to encryptPublic strings in this message.
	 * @throws IOException If there is an IO error.
	 */
	private void writeArgument(Object arg, boolean encryptStrings) throws IOException
	{
		if (arg instanceof String) writeString((String) arg, encryptStrings);
		else if (arg instanceof Integer) outputStream.writeInt((int) arg);
		else if (arg instanceof Long) outputStream.writeLong((long) arg);
		else if (arg instanceof Float) outputStream.writeFloat((float) arg);
		else if (arg instanceof Double) outputStream.writeDouble((double) arg);
		else if (arg instanceof Boolean) outputStream.writeBoolean((boolean) arg);
		else if (arg instanceof Byte) outputStream.writeByte((byte) arg);
		else if (arg instanceof byte[]) writeByteArray((byte[]) arg);
		else if (arg instanceof String[]) writeStringArray((String[]) arg, encryptStrings);
		else throw new IOException("Invalid argument type passed to MessageOutputStream: " + arg);
	}

	/**
	 * Write a string to the output stream, specifying whether the string should be encrypted with this stream's public key.
	 *
	 * @param s       The string to write.
	 * @param encrypt Whether the string should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	public void writeString(String s, boolean encrypt) throws IOException
	{
		if (encrypt)
		{
			// TODO encrypt string here.
			outputStream.writeBoolean(false);
		}
		outputStream.writeUTF(s);
	}

	/**
	 * Write an array of strings to the output stream, preceded by the array length.
	 *
	 * @param strings        The array of string strings.
	 * @param encryptStrings Whether the strings being written should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	private void writeStringArray(String[] strings, boolean encryptStrings) throws IOException
	{
		int stringsLength = strings.length;
		outputStream.writeInt(stringsLength);
		for (String string : strings) writeString(string, encryptStrings);
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
		outputStream.writeInt(bytesLength);
		outputStream.write(bytes);
	}

	/**
	 * Close the output stream.
	 *
	 * @throws IOException If there is an IO error.
	 */
	public void close() throws IOException
	{
		outputStream.close();
	}
}
