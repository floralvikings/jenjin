package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.BaseMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads messages registered with the MessageRegistry class from stream.
 *
 * @author Caleb Brinkman
 */
public class MessageInputStream
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageInputStream.class.getName());
	/** The output stream used by this message stream. */
	private final DataInputStream inputStream;
	/** The public key used by the application on this end of the stream to write encrypted strings to the other end. */
	private PublicKey publicKey;
	/** The private key used to decrypt messages sent from the other side.*/
	private PrivateKey privateKey;
	/** Flags whether the key has been received. */
	private boolean hasReceivedKey;

	/**
	 * Construct a new {@code MessageInputStream} from the given InputStream.
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
		if(!hasReceivedKey)
		{
			String keyString = this.inputStream.readUTF();

			if(keyString.equals(MessageOutputStream.NO_ENCRYPTION_KEY))
				LOGGER.log(Level.WARNING, "No public encryption key received!");

			else
			{
				try
				{
					KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
					X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyString.getBytes());
					publicKey = rsaKeyFac.generatePublic(keySpec);
				}catch(NoSuchAlgorithmException | InvalidKeySpecException ex)
				{
					LOGGER.log(Level.WARNING, "Unable to generate key from string: ", ex);
				}
			}
			hasReceivedKey = true;
		}
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
	 * Read from the DataInputStream an array of Objects to be passed as arguments to a message.
	 *
	 * @param classes The class names of the arguments to be read.
	 * @return An Object[] containing the message arguments.
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
	 * @param inputStream The input stream used to read.
	 * @return The read, decrypted string.
	 * @throws IOException If there is an IO error.
	 */
	private String readString(DataInputStream inputStream) throws IOException
	{
		boolean encrypted = inputStream.readBoolean();
		String received = inputStream.readUTF();
		if(encrypted)
		{
			byte[] encryptedBytes = received.getBytes();
			try
			{
				Cipher cipher = Cipher.getInstance(MessageOutputStream.XFORM);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				received = new String(cipher.doFinal(encryptedBytes));
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e)
			{
				LOGGER.log(Level.SEVERE, "Unable to find RSA algorithm; strings will not be decrypted!", e);
			} catch (InvalidKeyException e)
			{
				LOGGER.log(Level.SEVERE, "Invalid key when decrypting string: ", e );
			} catch (BadPaddingException | IllegalBlockSizeException e)
			{
				LOGGER.log(Level.SEVERE, "Unable to decrypt strings: ", e );
			}
		}
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
	 * Get the public key used to encrypt messages.
	 * @return The public key used to encrypt messages.
	 */
	public PublicKey getPublicKey()
	{
		return publicKey;
	}

	/**
	 * Set the private key for this stream.
	 * @param privateKey The private key to be used by this stream.
	 */
	public void setPrivateKey(PrivateKey privateKey)
	{
		this.privateKey = privateKey;
	}

	/**
	 * Close the input stream.
	 * @throws java.io.IOException If there is an IO error.
	 */
	public void close() throws IOException
	{
		inputStream.close();
	}
}
