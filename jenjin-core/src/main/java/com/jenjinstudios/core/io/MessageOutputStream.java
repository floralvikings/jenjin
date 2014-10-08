package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of a {@code DataOutputStream} used to write {@code Message} objects to an {@code OutputStream}.
 *
 * @author Caleb Brinkman
 */
public class MessageOutputStream extends DataOutputStream
{
	private static final Logger LOGGER = Logger.getLogger(MessageOutputStream.class.getName());
	private final MessageRegistry messageRegistry;
	private Cipher encryptCipher;
	private boolean closed;

	/**
	 * Construct a new {@code MessageOutputStream} from the given {@code OutputStream}.
	 *
	 * @param out The {@code OutputStream} to which to write messages.
	 */
	public MessageOutputStream(OutputStream out) {
		super(out);
		this.messageRegistry = MessageRegistry.getInstance();
	}

	/**
	 * Write the given {@code Message} to the output stream.
	 *
	 * @param message The {@code Message} to write.
	 *
	 * @throws IOException If there is an error writing to the output stream.
	 */
	public void writeMessage(Message message) throws IOException {
		if (closed)
		{
			throw new IOException("Cannot write message: stream closed");
		}
		Object[] args = message.getArgs();
		MessageType messageType = messageRegistry.getMessageType(message.getID());
		List<ArgumentType> argumentTypes = messageType.getArguments();
		int id = message.getID();
		writeShort(id);
		for (int i = 0; i < args.length; i++)
			writeArgument(args[i], argumentTypes.get(i).isEncrypt());
	}

	/**
	 * Return whether this stream has been closed.
	 *
	 * @return Whether this stream has been closed.
	 */
	public boolean isClosed() {
		return closed;
	}

	public void setPublicKey(PublicKey publicKey) {
		try
		{
			encryptCipher = Cipher.getInstance("RSA");
			encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create cipher, messages will not be decrypted.", e);
		}
	}

	@SuppressWarnings("OverlyComplexMethod")
	private void writeArgument(Object arg, boolean encryptStrings) throws IOException {
		if (arg instanceof String) writeString((String) arg, encryptStrings);
		else if (arg instanceof Integer) writeInt((int) arg);
		else if (arg instanceof Short) writeShort((short) arg);
		else if (arg instanceof Long) writeLong((long) arg);
		else if (arg instanceof Float) writeFloat((float) arg);
		else if (arg instanceof Double) writeDouble((double) arg);
		else if (arg instanceof Boolean) writeBoolean((boolean) arg);
		else if (arg instanceof Byte) writeByte((byte) arg);
		else if (arg instanceof byte[]) writeByteArray((byte[]) arg);
		else if (arg instanceof String[]) writeStringArray((String[]) arg, encryptStrings);
		else throw new IOException("Invalid argument type passed to MessageOutputStream: " + arg.getClass().getName());
	}

	void writeString(String s, boolean encrypt) throws IOException {
		if (encrypt)
		{
			if (encryptCipher == null)
			{
				LOGGER.log(Level.SEVERE, "AES key not set, message will not be encrypted: " + s);
				throw new IOException("Unable to encrypt sensitive data.");
			} else
			{
				try
				{
					byte[] sBytes = s.getBytes("UTF-8");
					String encryptedString = DatatypeConverter.printHexBinary(encryptCipher.doFinal(sBytes));
					writeBoolean(true);
					writeUTF(encryptedString);
				} catch (IllegalBlockSizeException | BadPaddingException | IllegalStateException e)
				{
					LOGGER.log(Level.SEVERE, "Error encrypting string, will use unencrypted.", e);
					throw new IOException("Unable to encrypt sensitive data.");
				}
			}
		} else
		{
			writeBoolean(false);
			writeUTF(s);
		}

	}

	private void writeStringArray(String[] strings, boolean encryptStrings) throws IOException {
		int stringsLength = strings.length;
		writeInt(stringsLength);
		for (String string : strings) writeString(string, encryptStrings);
	}

	private void writeByteArray(byte[] bytes) throws IOException {
		int bytesLength = bytes.length;
		writeInt(bytesLength);
		write(bytes);
	}

	@Override
	public void close() throws IOException {
		super.close();
		closed = true;
	}

}
