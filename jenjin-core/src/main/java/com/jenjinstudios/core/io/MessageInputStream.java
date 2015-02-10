package com.jenjinstudios.core.io;

import com.jenjinstudios.core.event.MessageReceivedListener;
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
import java.util.List;
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
	private final List<MessageReceivedListener> messageReceivedListeners = new LinkedList<>();

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
		Message message = new Message(id, args);
		nofityMessageReceivedListeners(message);
		return message;

	}

	private void nofityMessageReceivedListeners(Message message) {
		// TODO Handle in event dispatch thread.
		for (MessageReceivedListener listener : messageReceivedListeners)
		{
			listener.onMessageReceived(message);
		}
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

	private Object[] readMessageArgs(LinkedList<Class> classes) throws IOException {
		Object[] args = new Object[classes.size()];

		for (int i = 0; i < args.length; i++)
		{
			String currentClass = classes.pop().getName();
			String simple = simplifyClassName(currentClass);
			Object currentArg = readArgument(simple);
			args[i] = currentArg;
		}

		return args;
	}

	private String simplifyClassName(String complexName) {
		String simple = complexName;
		switch (complexName)
		{
			case "java.lang.Integer":
				simple = "int";
				break;
			case "java.lang.Long":
				simple = "long";
				break;
			case "java.lang.Double":
				simple = "double";
				break;
			case "java.lang.Float":
				simple = "float";
				break;
			case "java.lang.Short":
				simple = "short";
				break;
			case "java.lang.Boolean":
				simple = "boolean";
				break;
			case "java.lang.Byte":
				simple = "byte";
				break;
			case "[Ljava.lang.Byte;":
				simple = "[B";
		}
		return simple;
	}

	private Object readArgument(String currentClass) throws IOException {
		Object currentArg = null;

		if (currentClass.startsWith("["))
		{
			currentArg = readArray(currentClass);
		} else
		{
			switch (currentClass)
			{
				case "java.lang.String":
					currentArg = readString();
					break;
				case "int":
					currentArg = readInt();
					break;
				case "long":
					currentArg = readLong();
					break;
				case "double":
					currentArg = readDouble();
					break;
				case "float":
					currentArg = readFloat();
					break;
				case "short":
					currentArg = readShort();
					break;
				case "boolean":
					currentArg = readBoolean();
					break;
				case "byte":
					currentArg = readByte();
					break;
			}
		}
		return currentArg;
	}

	private Object readArray(String currentClass) throws IOException {
		Object currentArg = null;
		switch (currentClass)
		{
			case "[Ljava.lang.String;":
				currentArg = readStringArray();
				break;
			case "[B":
				currentArg = readByteArray();
				break;
		}
		return currentArg;
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

	/**
	 * Get the MessageReceivedListeners listening for messages on this stream.
	 *
	 * @return The MessageReceivedListeners listening for messages on this stream.
	 */
	public List<MessageReceivedListener> getMessageReceivedListeners() { return messageReceivedListeners; }
}
