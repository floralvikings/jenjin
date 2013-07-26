package com.jenjinstudios.io;

import com.jenjinstudios.jgcf.message.BaseMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the sending and reception of messages registered in the MessageRegistry class.
 *
 * @author Caleb Brinkman
 */
public class MessageOutputStream
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageOutputStream.class.getName());
	/** The string to send so that the receiving stream knows that there will be no string encryption. */
	private static final String NO_ENCRYPTION_KEY = "NO_ENCRYPTION_KEY";
	/** The private key used to decrypt responses to this stream. */
	private PrivateKey privateKey;
	/** The public key used to encrypt outgoing strings. */
	private PublicKey publicKey;
	/** The public key sent to the receiving end of this string. */
	private PublicKey outgoingKey;
	/** The output stream used by this message stream. */
	private final DataOutputStream outputStream;
	/** Flags whether this stream has sent the public key. */
	private boolean hasSentKey;

	/**
	 * Creates a new message output stream to write data to the specified
	 * underlying output stream. The counter {@code written} is
	 * set to zero.
	 *
	 * @param out the underlying output stream, to be saved for later
	 *            use.
	 * @see java.io.FilterOutputStream#out
	 * @throws java.io.IOException If there is an error sending the public key.
	 */
	public MessageOutputStream(OutputStream out) throws IOException
	{
		outputStream = new DataOutputStream(out);
		privateKey = null;
		try
		{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(512);
			KeyPair kp = kpg.generateKeyPair();
			privateKey = kp.getPrivate();
			// Here we need to send out public key; it's important to note that the public key generate here is
			// NOT the one used to send encrypted messages from this stream.  To do that, a public key must be set
			// using the setPublicKey method.
			outgoingKey = kp.getPublic();
		} catch (NoSuchAlgorithmException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to find RSA algorithm; strings will not be encrypted!", ex);
		}
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
		if(!hasSentKey)
		{
			String keyString = NO_ENCRYPTION_KEY;
			if(outgoingKey != null)
				keyString = new String(outgoingKey.getEncoded());
			outputStream.writeUTF(keyString);
			hasSentKey = true;
		}
		Object[] args = message.getArgs();
		int id = message.getID();
		outputStream.writeShort(id);
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
			encryptedString = encrypt(s);
		if (s.equals(encryptedString))
			outputStream.writeBoolean(false);
		else
			outputStream.writeBoolean(true);
		outputStream.writeUTF(encryptedString);
	}

	/**
	 * Encrypt the given string with this stream's public key.
	 *
	 * @param raw The string to be encrypted.
	 * @return The encrypted string.
	 */
	private String encrypt(String raw)
	{
		// If there's no public key, we can't encrypt it.
		if (publicKey == null)
		{
			LOGGER.log(Level.WARNING, "No public key set; unable to encrypt strings!");
			return raw;
		}
		String encrypted;
		Cipher cipher;
		try
		{
			/* The xform used to encrypt strings. */
			String XFORM = "RSA/ECB/PKCS1Padding";
			cipher = Cipher.getInstance(XFORM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = cipher.doFinal(raw.getBytes());
			encrypted = new String(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			// If the cipher can't be constructed, we can't encrypt things.
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
	 * Get the private key used to decrypt responses to this stream.
	 *
	 * @return The private key used to decrypt responses to this stream.
	 */
	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}

	/**
	 * Set the public key used to encrypt outgoing strings.
	 *
	 * @param publicKey The public key.
	 */
	public void setPublicKey(PublicKey publicKey)
	{
		if (publicKey == null)
			this.publicKey = publicKey;
	}
}
