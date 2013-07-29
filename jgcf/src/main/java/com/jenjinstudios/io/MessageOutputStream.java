package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.BaseMessage;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the sending and reception of messages registered in the MessageRegistry class.
 *
 * @author Caleb Brinkman
 */
public class MessageOutputStream
{
	/** The xform used to encryptPublic strings. */
	public static final String XFORM = "RSA/ECB/PKCS1Padding";
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageOutputStream.class.getName());
	/** The string to send so that the receiving stream knows that there will be no string encryption. */
	public static final byte[] NO_ENCRYPTION_KEY = new byte[94];
	/** The public key used to encryptPublic outgoing strings. */
	private PublicKey publicKey;
	/** The public key sent to the receiving end of this string. */
	private PublicKey outgoingKey;
	/** The output stream used by this message stream. */
	private final DataOutputStream outputStream;
	/** Flags whether the key has been sent. */
	private boolean hasSentKey;

	/**
	 * Creates a new message output stream to write data to the specified
	 * underlying output stream. The counter {@code written} is
	 * set to zero.
	 *
	 * @param out the underlying output stream, to be saved for later
	 *            use.
	 * @param outgoingKey The public key to send to the other end of this stream to return encrypted messages.
	 * @see java.io.FilterOutputStream#out
	 * @throws java.io.IOException If there is an error sending the public key.
	 */
	public MessageOutputStream(OutputStream out, PublicKey outgoingKey) throws IOException
	{
		outputStream = new DataOutputStream(out);
		this.outgoingKey = outgoingKey;

		byte[] keyBytes = NO_ENCRYPTION_KEY;
		if(outgoingKey != null)
		{
			keyBytes = this.outgoingKey.getEncoded();
		}
		outputStream.write(keyBytes);
	}

	/**
	 * Write the given {@code BaseMessage} to the output stream.
	 *
	 *
	 * @param message        The BaseMessage to be written to the stream.
	 * @throws IOException If there is an IO error.
	 */
	public void writeMessage(BaseMessage message) throws IOException
	{
		if(!hasSentKey)
		{

			hasSentKey = true;
		}
		Object[] args = message.getArgs();
		int id = message.getID();
		outputStream.writeShort(id);
		for (Object arg : args)
			writeArgument(arg, message.isEncrypted());
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
		String encryptedString = s;
		if (encrypt)
		{
			try
			{
				KeyGenerator keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(128);
				SecretKey secretKey = keyGen.generateKey();
				String secretKeyString = DatatypeConverter.printHexBinary(secretKey.getEncoded());
				encryptedString = encryptAES(s, secretKey);
				secretKeyString = encryptPublic(secretKeyString);
				if (s.equals(encryptedString))
					outputStream.writeBoolean(false);
				else
				{
					outputStream.writeBoolean(true);
					outputStream.writeUTF(secretKeyString);
				}
			} catch (NoSuchAlgorithmException e)
			{
				outputStream.writeBoolean(false);
				LOGGER.log(Level.SEVERE, "Unable to encryptPublic!", e);
			}
		}
		outputStream.writeUTF(encryptedString);
	}

	/**
	 * Encrypt the given string with this stream's public key.
	 *
	 * @param raw The string to be encrypted.
	 * @return The encrypted string.
	 */
	private String encryptPublic(String raw)
	{
		// If there's no public key, we can't encryptPublic it.
		if (publicKey == null)
		{
			LOGGER.log(Level.WARNING, "No public key set; unable to encryptPublic strings!");
			return raw;
		}

		String encrypted;
		Cipher cipher;
		try
		{
			cipher = Cipher.getInstance(XFORM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = cipher.doFinal(raw.getBytes());
			encrypted = DatatypeConverter.printHexBinary(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			// If the cipher can't be constructed, we can't encryptPublic things.
			LOGGER.log(Level.WARNING, "Unable to create Cipher; strings will not be encrypted!", e);
			return raw;
		} catch (BadPaddingException | IllegalBlockSizeException e)
		{
			LOGGER.log(Level.WARNING, "Unable to encrypt string; strings will not be encrypted!", e);
			return raw;
		}
		return encrypted;
	}

	/**
	 * Encrypt the given string with this stream's public key.
	 *
	 * @param raw The string to be encrypted.
	 * @param key The key used to encryptPublic the string.
	 * @return The encrypted string.
	 */
	private String encryptAES(String raw, SecretKey key)
	{
		String encrypted;
		Cipher cipher;
		try
		{
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedBytes = cipher.doFinal(raw.getBytes());
			encrypted = DatatypeConverter.printHexBinary(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			// If the cipher can't be constructed, we can't encryptPublic things.
			LOGGER.log(Level.WARNING, "Unable to create Cipher; strings will not be encrypted!", e);
			return raw;
		} catch (BadPaddingException | IllegalBlockSizeException e)
		{
			LOGGER.log(Level.WARNING, "Unable to encrypt string; strings will not be encrypted!", e);
			return raw;
		}
		return encrypted;
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
	 * Set the public key used to encryptPublic outgoing strings.
	 *
	 * @param publicKey The public key.
	 */
	public void setPublicKey(PublicKey publicKey)
	{
		if (this.publicKey == null)
			this.publicKey = publicKey;
	}

	/**
	 * Close the output stream.
	 * @throws IOException If there is an IO error.
	 */
	public void close() throws IOException
	{
		outputStream.close();
	}
}
