package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.BaseMessage;

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
		// TODO Create public/private key pair and send out public key.
	}

	/**
	 * Write the given {@code BaseMessage} to the output stream.
	 *
	 * @param message        The BaseMessage to be written to the stream.
	 * @param encryptStrings Whether to encrypt strings in this message.
	 * @throws IOException If there is an IO error.
	 */
	public void writeMessage(BaseMessage message, boolean encryptStrings) throws IOException
	{
		Object[] args = message.getArgs();
		int id = message.getID();
		writeShort(id);
		for (Object arg : args)
			writeArgument(arg, encryptStrings);
	}

	/**
	 * Write an argument to the data stream, properly cast.
	 *
	 * @param arg            The argument to be written.
	 * @param encryptStrings Whether to encrypt strings in this message.
	 * @throws IOException If there is an IO error.
	 */
	private void writeArgument(Object arg, boolean encryptStrings) throws IOException
	{
		if (arg instanceof String) writeString((String) arg, encryptStrings);
		else if (arg instanceof Integer) writeInt((int) arg);
		else if (arg instanceof Long) writeLong((long) arg);
		else if (arg instanceof Float) writeFloat((float) arg);
		else if (arg instanceof Double) writeDouble((double) arg);
		else if (arg instanceof Boolean) writeBoolean((boolean) arg);
		else if (arg instanceof Byte) writeByte((byte) arg);
		else if (arg instanceof byte[]) writeByteArray((byte[]) arg);
		else if (arg instanceof String[]) writeStringArray((String[]) arg, encryptStrings);
	}

	/**
	 * Write a string to the output stream, specifying whether the string should be encrypted with this stream's public key.
	 * @param s The string to write.
	 * @param encrypted Whether the string should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	public void writeString(String s, boolean encrypted) throws IOException
	{
		super.writeBoolean(encrypted);
		if(encrypted)
			s = encrypt(s);
		super.writeUTF(s);
	}

	/**
	 * Encrypt the given string with this stream's public key.
	 * @param raw The string to be encrypted.
	 * @return The encrypted string.
	 */
	private String encrypt(String raw)
	{
		String encrypted = raw;
		// TODO encrypt string with public key here.
		return encrypted;
	}

	/**
	 * Write an array of strings to the output stream, preceded by the array length.
	 *
	 *
	 * @param strings The array of string strings.
	 * @param encryptStrings Whether the strings being written should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	private void writeStringArray(String[] strings, boolean encryptStrings) throws IOException
	{
		int stringsLength = strings.length;
		writeInt(stringsLength);
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
		writeInt(bytesLength);
		write(bytes);
	}
}
