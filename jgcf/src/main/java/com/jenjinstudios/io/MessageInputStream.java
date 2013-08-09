package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.BaseMessage;
import com.jenjinstudios.jgcf.message.MessageRegistry;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * Reads messages registered with the MessageRegistry class from stream.
 *
 * @author Caleb Brinkman
 */
public class MessageInputStream
{
	/** The output stream used by this message stream. */
	private final DataInputStream inputStream;

	/**
	 * Construct a new {@code MessageInputStream} from the given InputStream.
	 *
	 *
	 * @param inputStream The InputStream from which messages will be read.
	 * @throws java.io.IOException If there is an IO error.
	 */
	public MessageInputStream(InputStream inputStream) throws IOException
	{
		this.inputStream = new DataInputStream(inputStream);
	}

	/**
	 * Read a BaseMessage or subclass from the DataStream.
	 *
	 * @return The BaseMessage constructed form the data stream.
	 * @throws IOException If there is an IO error.
	 */
	public BaseMessage readMessage() throws IOException
	{
		try
		{
			short id = inputStream.readShort();
			LinkedList<Class> classes = MessageRegistry.getArgumentClasses(id);
			Class<?>[] classArray = new Class[classes.size()];
			classes.toArray(classArray);
			Object[] args = readMessageArgs(classes);
			return new BaseMessage(id, args);
		} catch (EOFException | SocketException e)
		{
			return null;
			// This means the stream has closed.
		}
	}

	/**
	 * Read from the DataInputStream an array of Objects to be passed as argumentTypes to a message.
	 *
	 * @param classes The class names of the argumentTypes to be read.
	 * @return An Object[] containing the message argumentTypes.
	 * @throws IOException If there is an IO error
	 */
	private Object[] readMessageArgs(LinkedList<Class> classes) throws IOException
	{
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
	 * Read a string from the input stream, determining if it is encrypted and decrypting it if necessary.
	 *
	 * @param inputStream The input stream used to read.
	 * @return The read, decrypted string.
	 * @throws IOException If there is an IO error.
	 */
	private String readString(DataInputStream inputStream) throws IOException
	{
		boolean encrypted = inputStream.readBoolean();
		String received;
		if (encrypted)
		{
			// TODO Get encrypted string here.
			received = inputStream.readUTF();
		} else
			received = inputStream.readUTF();
		return received;
	}

	/**
	 * Read an array of bytes from the DataInputStream.
	 *
	 * @return The read array of bytes.
	 * @throws IOException If there is an error reading an array of bytes.
	 */
	private byte[] readByteArray() throws IOException
	{
		byte[] bytes;
		int size = inputStream.readInt();
		bytes = new byte[size];
		int read = inputStream.read(bytes, 0, size);
		if (read != size) throw new IOException("Incorrect number of bytes read for byte array.");
		return bytes;
	}

	/**
	 * Read an array of strings from the DataInputStream.
	 *
	 * @return The read array of strings.
	 * @throws IOException If there is an error reading an array of strings.
	 */
	private String[] readStringArray() throws IOException
	{
		String[] strings;
		int size = inputStream.readInt();
		strings = new String[size];
		for (int i = 0; i < strings.length; i++)
			strings[i] = readString(inputStream);
		return strings;
	}

	/**
	 * Close the input stream.
	 *
	 * @throws java.io.IOException If there is an IO error.
	 */
	public void close() throws IOException
	{
		inputStream.close();
	}
}
