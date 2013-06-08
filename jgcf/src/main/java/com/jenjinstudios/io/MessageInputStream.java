package com.jenjinstudios.io;

import com.jenjinstudios.message.MessageRegistry;

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
public class MessageInputStream extends DataInputStream
{
	/**
	 * Construct a new {@code MesageInputStream} from the given InputStream.
	 *
	 * @param inputStream The InputStream from which messages will be read.
	 */
	public MessageInputStream(InputStream inputStream)
	{
		super(inputStream);
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
			short id = readShort();
			LinkedList<String> classNames = MessageRegistry.getClassNames(id);
			LinkedList<Class> classes = new LinkedList<>();
			for (String s : classNames) classes.add(Class.forName(s));
			Class<?>[] classArray = new Class[classes.size()];
			classes.toArray(classArray);
			Class<?> messageClass = Class.forName(MessageRegistry.getMessageClass(id));
			Object[] args = readMessageArgs(classNames);
			Object message = messageClass.getConstructor(classArray).newInstance(args);
			if (!(message instanceof BaseMessage)) throw new IOException("Message not BaseMessage");
			return (BaseMessage) message;
		} catch (EOFException | SocketException e)
		{
			return null;
			// This means the stream has closed.
		} catch (Exception e)
		{
			throw new IOException(e);
		}
	}

	/**
	 * Read from the DataInputStream an array of Objects to be passed as arguments to a message.
	 *
	 * @param classNames The class names of the arguments to be read.
	 * @return An Object[] containing the message arguments.
	 * @throws IOException If there is an IO error
	 */
	private Object[] readMessageArgs(LinkedList<String> classNames) throws IOException
	{
		Object[] args = new Object[classNames.size()];

		for (int i = 0; i < args.length; i++)
		{
			String currentClass = classNames.pop();
			switch (currentClass)
			{
				case "java.lang.String":
					args[i] = readUTF();
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

	/**
	 * Read an array of bytes from the DataInputStream.
	 *
	 * @return The read array of bytes.
	 * @throws IOException If there is an error reading an array of bytes.
	 */
	private byte[] readByteArray() throws IOException
	{
		byte[] bytes;
		int size = readInt();
		bytes = new byte[size];
		int read = read(bytes, 0, size);
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
		int size = readInt();
		strings = new String[size];
		for (int i = 0; i < strings.length; i++)
			strings[i] = readUTF();
		return strings;
	}

}
