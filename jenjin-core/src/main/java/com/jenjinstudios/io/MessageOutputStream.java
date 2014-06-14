package com.jenjinstudios.io;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the sending and reception of messages registered in the MessageRegistry class.
 * @author Caleb Brinkman
 */
public class MessageOutputStream
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageOutputStream.class.getName());
	/** The output stream used by this message stream. */
	private final DataOutputStream outputStream;
	/** The messageRegistry using this stream. */
	private final MessageRegistry messageRegistry;
	/** The AES key used to encrypt messages from this client handler. */
	private SecretKey aesKey;
	/** The AES cipher. */
	private Cipher aesEncryptCipher;

	/**
	 * Creates a new message output stream to write data to the specified underlying output stream. The counter {@code
	 * written} is set to zero.
	 * @param messageRegistry The messageRegistry using this stream.
	 * @param out the underlying output stream, to be saved for later use.
	 * @see java.io.FilterOutputStream#out
	 */
	public MessageOutputStream(MessageRegistry messageRegistry, OutputStream out) {
		this.messageRegistry = messageRegistry;
		outputStream = new DataOutputStream(out);
	}

	/**
	 * Write the given {@code Message} to the output stream.
	 * @param message The Message to be written to the stream.
	 * @throws IOException If there is an IO error.
	 */
	public void writeMessage(Message message) throws IOException {
		Object[] args = message.getArgs();
		MessageType messageType = messageRegistry.getMessageType(message.getID());
		ArgumentType[] argumentTypes = messageType.argumentTypes;
		int id = message.getID();
		outputStream.writeShort(id);
		for (int i = 0; i < args.length; i++)
			writeArgument(args[i], argumentTypes[i].encrypt);
	}

	/**
	 * Write an argument to the data stream, properly cast.
	 * @param arg The argument to be written.
	 * @param encryptStrings Whether to encryptPublic strings in this message.
	 * @throws IOException If there is an IO error.
	 */
	private void writeArgument(Object arg, boolean encryptStrings) throws IOException {
		if (arg instanceof String) writeString((String) arg, encryptStrings);
		else if (arg instanceof Integer) outputStream.writeInt((int) arg);
		else if (arg instanceof Short) outputStream.writeShort((short) arg);
		else if (arg instanceof Long) outputStream.writeLong((long) arg);
		else if (arg instanceof Float) outputStream.writeFloat((float) arg);
		else if (arg instanceof Double) outputStream.writeDouble((double) arg);
		else if (arg instanceof Boolean) outputStream.writeBoolean((boolean) arg);
		else if (arg instanceof Byte) outputStream.writeByte((byte) arg);
		else if (arg instanceof byte[]) writeByteArray((byte[]) arg);
		else if (arg instanceof String[]) writeStringArray((String[]) arg, encryptStrings);
		else throw new IOException("Invalid argument type passed to MessageOutputStream: " + arg.getClass().getName());
	}

	/**
	 * Write a string to the output stream, specifying whether the string should be encrypted with this stream's public
	 * key.
	 * @param s The string to write.
	 * @param encrypt Whether the string should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	void writeString(String s, boolean encrypt) throws IOException {
		if (encrypt)
		{
			if (aesKey == null)
			{
				LOGGER.log(Level.SEVERE, "AES key not set, message will not be encrypted: " + s);
				// TODO Wrap with better exception.
				throw new IOException("Unable to encrypt sensitive data.");
			} else
			{
				try
				{
					byte[] sBytes = s.getBytes("UTF-8");
					String encryptedString = DatatypeConverter.printHexBinary(aesEncryptCipher.doFinal(sBytes));
					outputStream.writeBoolean(true);
					outputStream.writeUTF(encryptedString);
				} catch (IllegalBlockSizeException | BadPaddingException | IllegalStateException e)
				{
					LOGGER.log(Level.SEVERE, "Error encrypting string, will use unencrypted.", e);
					// TODO Wrap this in a better exception.
					throw new IOException("Unable to encrypt sensitive data.");
				}
			}
		} else
		{
			outputStream.writeBoolean(false);
			outputStream.writeUTF(s);
		}

	}

	/**
	 * Write an array of strings to the output stream, preceded by the array length.
	 * @param strings The array of string strings.
	 * @param encryptStrings Whether the strings being written should be encrypted.
	 * @throws IOException If there is an IO error.
	 */
	private void writeStringArray(String[] strings, boolean encryptStrings) throws IOException {
		int stringsLength = strings.length;
		outputStream.writeInt(stringsLength);
		for (String string : strings) writeString(string, encryptStrings);
	}

	/**
	 * Write an array of bytes to the output stream, preceded by the array length.
	 * @param bytes The array of byte bytes.
	 * @throws IOException If there is an IO error.
	 */
	private void writeByteArray(byte[] bytes) throws IOException {
		int bytesLength = bytes.length;
		outputStream.writeInt(bytesLength);
		outputStream.write(bytes);
	}

	/**
	 * Close the output stream.
	 * @throws IOException If there is an IO error.
	 */
	public void close() throws IOException {
		outputStream.close();
	}

	/**
	 * Set the AES key for this output stream to encrypt messages.
	 * @param key The AES key used by this output stream to encrypt messages.
	 */
	public void setAesKey(byte[] key) {
		if (key == null)
			return;
		try
		{
			aesKey = new SecretKeySpec(key, "AES");
			aesEncryptCipher = Cipher.getInstance("AES");
			aesEncryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create cipher, messages will not be encrypted.", e);
			aesKey = null;
		}
	}
}
