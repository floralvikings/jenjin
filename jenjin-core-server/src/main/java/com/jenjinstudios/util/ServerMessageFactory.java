package com.jenjinstudios.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.net.ClientHandler;
import com.jenjinstudios.net.Connection;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Caleb Brinkman */
public class ServerMessageFactory
{
	private static final Logger LOGGER = Logger.getLogger(ServerMessageFactory.class.getName());

	public static Message generateLogoutResponse(Connection conn, boolean success) {
		Message logoutResponse = new Message(conn, "LogoutResponse");
		logoutResponse.setArgument("success", success);
		return logoutResponse;
	}

	public static Message generateFirstConnectResponse(ClientHandler conn) {
		Message firstConnectResponse = new Message(conn, "FirstConnectResponse");
		firstConnectResponse.setArgument("ups", conn.getServer().UPS);
		return firstConnectResponse;
	}

	public static Message generateAESKeyMessage(Connection conn, byte[] publicKeyBytes) {
		Message aesMessage = new Message(conn, "AESKeyMessage");
		byte[] encryptedAESKey = MessageInputStream.NO_KEY;
		try {
			// Generate an AES key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
			// Set the output stream and input stream aes key for the client handler.
			conn.setAESKey(aesKeyBytes);
			// Get the public key from the message.
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
			// Create a cipher using the public key, and encrypt the AES key.
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedAESKey = cipher.doFinal(aesKeyBytes);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.log(Level.SEVERE, "Unable to create AES key!", e);
		} catch (InvalidKeySpecException e) {
			LOGGER.log(Level.SEVERE, "Unable to create public key from received bytes!", e);
		} catch (NoSuchPaddingException | BadPaddingException e) {
			LOGGER.log(Level.SEVERE, "Incorrect padding specified in RSA encryption?!?", e);
		} catch (InvalidKeyException e) {
			LOGGER.log(Level.SEVERE, "Unable to encrypt RSA, invalid key received!", e);
		} catch (IllegalBlockSizeException e) {
			LOGGER.log(Level.SEVERE, "Illegal block size?!?", e);
		}

		// Construct the AESKeyMessage
		aesMessage.setArgument("key", encryptedAESKey);
		return aesMessage;
	}

	public static Message generatePingResponse(ClientHandler clientHandler, long requestTimeNanos) {
		Message pingResponse = new Message(clientHandler, "PingResponse");
		pingResponse.setArgument("requestTimeNanos", requestTimeNanos);
		return pingResponse;
	}

	public static Message generateLoginResponse(Connection conn, boolean success, long loggedInTime) {
		Message loginResponse = new Message(conn, "LoginResponse");
		loginResponse.setArgument("success", success);
		loginResponse.setArgument("loginTime", loggedInTime);
		return loginResponse;
	}
}
