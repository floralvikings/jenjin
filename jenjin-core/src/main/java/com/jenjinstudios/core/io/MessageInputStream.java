package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.TypeMapper;
import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code MessageInputStream} class is an implementation of a {@code DataInputStream} used to read {@code Message}
 * objects from a stream.
 *
 * @author Caleb Brinkman
 */
public class MessageInputStream extends DataInputStream
{
	private static final Logger LOGGER = Logger.getLogger(MessageInputStream.class.getName());
	private final MessageRegistry messageRegistry;
	private Cipher decryptCipher;

	/**
	 * Construct a new {@code MessageInputStream} which will read from the specified {@code InputStream}.
	 *
	 * @param inputStream The {@code InputStream} from which this {@code MessageInputStream} will read.
	 */
	public MessageInputStream(InputStream inputStream) {
		super(inputStream);
		this.messageRegistry = MessageRegistry.getInstance();
	}

	/**
	 * Read a {@code Message} object from the stream.
	 *
	 * @return The read {@code Message} object.
	 *
	 * @throws IOException If there is an error reading from the stream.
	 */
	public Message readMessage() throws IOException {
		short id = readShort();
		MessageType messageType = messageRegistry.getMessageType(id);
		if (messageType == null)
		{
			throw new MessageTypeException(id);
		}
		LinkedList<Class> classes = new LinkedList<>();
		for (ArgumentType argumentType : messageType.getArguments())
		{
			Class c = TypeMapper.getTypeForName(argumentType.getType());
			classes.add(c);
		}
		Class<?>[] classArray = new Class[classes.size()];
		classes.toArray(classArray);
		Object[] args = readMessageArgs(classes);
		return new Message(messageRegistry, id, args);

	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	/**
	 * Set the {@code PrivateKey} used by this stream to decrypt incoming messages.
	 *
	 * @param privateKey The private key.
	 */
	public void setPrivateKey(PrivateKey privateKey) {
		try
		{
			decryptCipher = Cipher.getInstance("RSA");
			decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create cipher, messages will not be decrypted.", e);
		}
	}

	@SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
	private Object[] readMessageArgs(LinkedList<Class> classes) throws IOException {
		Object[] args = new Object[classes.size()];

		for (int i = 0; i < args.length; i++)
		{
			String currentClass = classes.pop().getName();
			switch (currentClass)
			{
				case "java.lang.String":
					args[i] = readString();
					break;
				case "int":
				case "java.lang.Integer":
					args[i] = readInt();
					break;
				case "java.lang.Long":
				case "long":
					args[i] = readLong();
					break;
				case "double":
				case "java.lang.Double":
					args[i] = readDouble();
					break;
				case "float":
				case "java.lang.Float":
					args[i] = readFloat();
					break;
				case "short":
				case "java.lang.Short":
					args[i] = readShort();
					break;
				case "boolean":
				case "java.lang.Boolean":
					args[i] = readBoolean();
					break;
				case "byte":
				case "java.lang.Byte":
					args[i] = readByte();
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

	private String[] readStringArray() throws IOException {
		String[] strings;
		int size = readInt();
		strings = new String[size];
		for (int i = 0; i < strings.length; i++)
			strings[i] = readString();
		return strings;
	}

	private byte[] readByteArray() throws IOException {
		byte[] bytes;
		int size = readInt();
		bytes = new byte[size];
		int numBytesRead = 0;
		while (numBytesRead < size)
		{
			numBytesRead += read(bytes, numBytesRead, size - numBytesRead);
		}
		if (numBytesRead != size) throw new IOException("Incorrect number of bytes read for byte array:" +
			  "Expected " + size + ", got " + numBytesRead);
		return bytes;
	}

	private String readString() throws IOException {
		boolean encrypted = readBoolean();
		String received = readUTF();
		if (encrypted)
		{
			if (decryptCipher != null)
			{
				received = decryptString(received);
			} else
			{
				LOGGER.log(Level.SEVERE, "AES key not properly set, unable to decrypt messages.");
			}
		}
		return received;
	}

	private String decryptString(String encrypted) throws UnsupportedEncodingException {
		String decrypted = encrypted;
		try
		{
			byte[] encBytes = DatatypeConverter.parseHexBinary(encrypted);
			byte[] decBytes = decryptCipher.doFinal(encBytes);
			decrypted = new String(decBytes, "UTF-8");
		} catch (IllegalBlockSizeException | BadPaddingException e)
		{
			LOGGER.log(Level.WARNING, "Unable to decrypt message: ", e);
		}
		return decrypted;
	}

}
