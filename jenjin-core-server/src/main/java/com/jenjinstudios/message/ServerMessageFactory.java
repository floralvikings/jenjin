package com.jenjinstudios.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.util.MessageFactory;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to generate messages for the Jenjin core server.
 * @author Caleb Brinkman
 */
public class ServerMessageFactory extends MessageFactory
{
	/** The logger used by this class. */
	private static final Logger LOGGER = Logger.getLogger(ServerMessageFactory.class.getName());
	/** The ClientHandler for which this message factory works. */
	private final ClientHandler clientHandler;

	/**
	 * Construct a new ServerMessageFactory.
	 * @param conn The ClientHandler for which this message factory works.
	 */
	public ServerMessageFactory(ClientHandler conn, MessageRegistry messageRegistry) {
		super(messageRegistry);
		this.clientHandler = conn;
	}

	/**
	 * Generate a LogoutResponse.
	 * @param success Whether the logout attempt was successful.
	 * @return The LogoutResponse.
	 */
	public Message generateLogoutResponse(boolean success) {
		Message logoutResponse = getMessageRegistry().createMessage("LogoutResponse");
		logoutResponse.setArgument("success", success);
		return logoutResponse;
	}

	/**
	 * Generate a FirstConnectResponse to be sent to the client to indicate a successful connection has been
	 * established.
	 * @param ups The Updates Per Second being run by the server.
	 * @return The FirstConnectResponse.
	 */
	public Message generateFirstConnectResponse(int ups) {
		Message firstConnectResponse = getMessageRegistry().createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", ups);
		return firstConnectResponse;
	}

	/**
	 * Generate a new AESKeyMessage.
	 * @param publicKeyBytes The bytes of the public key.
	 * @return The AESKeyMessage.
	 */
	public Message generateAESKeyMessage(byte[] publicKeyBytes) {
		Message aesMessage = getMessageRegistry().createMessage("AESKeyMessage");
		byte[] encryptedAESKey = MessageInputStream.NO_KEY;
		try
		{
			// Generate an AES key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
			// Set the output stream and input stream aes key for the client handler.
			clientHandler.setAESKey(aesKeyBytes);
			// Get the public key from the message.
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
			// Create a cipher using the public key, and encrypt the AES key.
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedAESKey = cipher.doFinal(aesKeyBytes);
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create AES key!", e);
		} catch (InvalidKeySpecException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create public key from received bytes!", e);
		} catch (NoSuchPaddingException | BadPaddingException e)
		{
			LOGGER.log(Level.SEVERE, "Incorrect padding specified in RSA encryption?!?", e);
		} catch (InvalidKeyException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to encrypt RSA, invalid key received!", e);
		} catch (IllegalBlockSizeException e)
		{
			LOGGER.log(Level.SEVERE, "Illegal block size?!?", e);
		}

		// Construct the AESKeyMessage
		aesMessage.setArgument("key", encryptedAESKey);
		return aesMessage;
	}

	/**
	 * Generate a response to a PingRequest.
	 * @param requestTimeNanos The time at which the ping request was made.
	 * @return The PingResponse message.
	 */
	public Message generatePingResponse(long requestTimeNanos) {
		Message pingResponse = getMessageRegistry().createMessage("PingResponse");
		pingResponse.setArgument("requestTimeNanos", requestTimeNanos);
		return pingResponse;
	}

	/**
	 * Generate a response to a login attempt.
	 * @param success Whether the login attempt was successful.
	 * @param loggedInTime The time of the successful login.
	 * @return The LoginResponse message.
	 */
	public Message generateLoginResponse(boolean success, long loggedInTime) {
		Message loginResponse = getMessageRegistry().createMessage("LoginResponse");
		loginResponse.setArgument("success", success);
		loginResponse.setArgument("loginTime", loggedInTime);
		return loginResponse;
	}
}
