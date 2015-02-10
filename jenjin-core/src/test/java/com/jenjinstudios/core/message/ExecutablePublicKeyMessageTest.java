package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.util.MessageFactory;
import com.jenjinstudios.core.util.SecurityUtil;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.security.Key;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Test key verification.
 *
 * @author Caleb Brinkman
 */
public class ExecutablePublicKeyMessageTest
{
	/**
	 * Test key verification.
	 */
	@Test
	public void testVerification() {
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
		Map<InetAddress, Key> keys = new HashMap<>();
		keys.put(address, rsaKeyPair.getPublic());

		Connection connection = mock(Connection.class);
		MessageIO messageIO = mock(MessageIO.class);

		when(messageIO.getAddress()).thenReturn(address);
		when(connection.getMessageIO()).thenReturn(messageIO);
		when(connection.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
		executable.runImmediate();

		verify(messageIO).setPublicKey(rsaKeyPair.getPublic());
	}

	/**
	 * Test verification bypass when no verified keys are present.
	 */
	@Test
	public void testVerificationNoKeys() {
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		Message message = MessageFactory.generatePublicKeyMessage(rsaKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
		Map<InetAddress, Key> keys = new HashMap<>();

		Connection connection = mock(Connection.class);
		MessageIO messageIO = mock(MessageIO.class);

		when(messageIO.getAddress()).thenReturn(address);
		when(connection.getMessageIO()).thenReturn(messageIO);
		when(connection.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
		executable.runImmediate();

		verify(messageIO).setPublicKey(rsaKeyPair.getPublic());
	}

	/**
	 * Test failed key verification due to invalid key.
	 */
	@Test
	public void testVerificationInvalidKey() {
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		KeyPair invalidKeyPair = SecurityUtil.generateRSAKeyPair();
		Message message = MessageFactory.generatePublicKeyMessage(invalidKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
		Map<InetAddress, Key> keys = new HashMap<>();
		keys.put(address, rsaKeyPair.getPublic());

		Connection connection = mock(Connection.class);
		MessageIO messageIO = mock(MessageIO.class);

		when(messageIO.getAddress()).thenReturn(address);
		when(connection.getMessageIO()).thenReturn(messageIO);
		when(connection.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
		executable.runImmediate();

		verify(messageIO, times(0)).setPublicKey(any());
	}

	/**
	 * Test invalid key verification due to unknown host.
	 */
	@Test
	public void testVerificationNoAddress() {
		KeyPair rsaKeyPair = SecurityUtil.generateRSAKeyPair();
		KeyPair invalidKeyPair = SecurityUtil.generateRSAKeyPair();
		Message message = MessageFactory.generatePublicKeyMessage(invalidKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
		Map<InetAddress, Key> keys = new HashMap<>();
		keys.put(address, rsaKeyPair.getPublic());

		Connection connection = mock(Connection.class);
		MessageIO messageIO = mock(MessageIO.class);

		when(connection.getMessageIO()).thenReturn(messageIO);
		when(connection.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
		executable.runImmediate();

		verify(messageIO, times(0)).setPublicKey(any());
	}
}
