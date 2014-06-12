package com.jenjinstudios.io;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads messages registered with the MessageRegistry class from stream.
 * @author Caleb Brinkman
 */
public class MessageInputStream
{
	/** The default value used when no AES key can be used. */
	public static final byte[] NO_KEY = new byte[1];
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageInputStream.class.getName());
	/** The output stream used by this message stream. */
	private final DataInputStream inputStream;
	/** The Connection using this stream. */
	private final MessageRegistry messageRegistry;
	/** The AES key used to encrypt outgoing messages. */
	private SecretKey aesKey;
	/** The cipher used to decrypt messages. */
	private Cipher aesDecryptCipher;

	/**
	 * Construct a new {@code MessageInputStream} from the given InputStream.
	 * @param messageRegistry The messageRegistry using this stream.
	 * @param inputStream The InputStream from which messages will be read.
	 */
	public MessageInputStream(MessageRegistry messageRegistry, InputStream inputStream) {
		this.messageRegistry = messageRegistry;
		this.inputStream = new DataInputStream(inputStream);
	}

	/**
	 * Read a Message or subclass from the DataStream.
	 * @return The Message constructed form the data stream.
	 */
	public Message readMessage() {
		try
		{
			short id = inputStream.readShort();
			LinkedList<Class> classes = messageRegistry.getArgumentClasses(id);
			Class<?>[] classArray = new Class[classes.size()];
			classes.toArray(classArray);
			Object[] args = readMessageArgs(classes);
			return new Message(messageRegistry, id, args);
		} catch (Exception e)
		{
			// TODO Improve this error handling
			LOGGER.log(Level.SEVERE, "Unable to parse message from stream: {0}", e.getMessage());
			return null;
			// This means the stream has closed, or the an invalid message was found.
		}
	}

	/**
	 * Close the input stream.
	 * @throws java.io.IOException If there is an IO error.
	 */
	public void close() throws IOException { inputStream.close(); }

	/**
	 * Set the AES key used to decrypt messages.
	 * @param key The AES key used to decrypt messages.
	 */
	public void setAESKey(byte[] key) {
		try
		{
			aesKey = new SecretKeySpec(key, "AES");
			aesDecryptCipher = Cipher.getInstance("AES");
			aesDecryptCipher.init(Cipher.DECRYPT_MODE, aesKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create cipher, messages will not be decrypted.", e);
			aesKey = null;
		}
	}

	/**
	 * Read from the DataInputStream an array of Objects to be passed as argumentTypes to a message.
	 * @param classes The class names of the argumentTypes to be read.
	 * @return An Object[] containing the message argumentTypes.
	 * @throws IOException If there is an IO error
	 */
	private Object[] readMessageArgs(LinkedList<Class> classes) throws IOException {
		Object[] args = new Object[classes.size()];

		for (int i = 0; i < args.length; i++)
		{
			String currentClass = classes.pop().getName();
			switch (currentClass)
			{
				case "java.lang.String":
					args[i] = readString(inputStream);
					break;
				case "int":
				case "java.lang.Integer":
					args[i] = inputStream.readInt();
					break;
				case "java.lang.Long":
				case "long":
					args[i] = inputStream.readLong();
					break;
				case "double":
				case "java.lang.Double":
					args[i] = inputStream.readDouble();
					break;
				case "float":
				case "java.lang.Float":
					args[i] = inputStream.readFloat();
					break;
				case "short":
				case "java.lang.Short":
					args[i] = inputStream.readShort();
					break;
				case "boolean":
				case "java.lang.Boolean":
					args[i] = inputStream.readBoolean();
					break;
				case "byte":
				case "java.lang.Byte":
					args[i] = inputStream.readByte();
					break;
				case "[Ljava.lang.Byte;":
				case "[B":
					args[i] = readByteArray();
					break;
				case "[Ljava.lang.String;":
					args[i] = readStringArray();
					break;
			}
		}

		return args;
	}

	/**
	 * Read an array of strings from the DataInputStream.
	 * @return The read array of strings.
	 * @throws IOException If there is an error reading an array of strings.
	 */
	private String[] readStringArray() throws IOException {
		String[] strings;
		int size = inputStream.readInt();
		strings = new String[size];
		for (int i = 0; i < strings.length; i++)
			strings[i] = readString(inputStream);
		return strings;
	}

	/**
	 * Read an array of bytes from the DataInputStream.
	 * @return The read array of bytes.
	 * @throws IOException If there is an error reading an array of bytes.
	 */
	private byte[] readByteArray() throws IOException {
		byte[] bytes;
		int size = inputStream.readInt();
		bytes = new byte[size];
		int read = inputStream.read(bytes, 0, size);
		if (read != size) throw new IOException("Incorrect number of bytes read for byte array.");
		return bytes;
	}

	/**
	 * Read a string from the input stream, determining if it is encrypted and decrypting it if necessary.
	 * @param inputStream The input stream used to read.
	 * @return The read, decrypted string.
	 * @throws IOException If there is an IO error.
	 */
	private String readString(DataInputStream inputStream) throws IOException {
		boolean encrypted = inputStream.readBoolean();
		String received = inputStream.readUTF();
		if (encrypted)
		{
			if (aesKey == null)
			{
				LOGGER.log(Level.SEVERE, "AES key not properly set, unable to decrypt messages.");
			} else
			{
				try
				{
					byte[] encBytes = DatatypeConverter.parseHexBinary(received);
					byte[] decBytes = aesDecryptCipher.doFinal(encBytes);
					received = new String(decBytes, "UTF-8");
				} catch (IllegalBlockSizeException | BadPaddingException e)
				{
					LOGGER.log(Level.WARNING, "Unable to decrypt message!", e);
				}
			}
		}
		return received;
	}

}
