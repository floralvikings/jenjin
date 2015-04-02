package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
	 * Set up the message registry.
	 */
	@BeforeClass
	public void setUp() {
		MessageRegistry.getGlobalRegistry().register("Test Message Registry",
			  getClass().getClassLoader().getResourceAsStream("test/jenjinstudios/core/Messages.xml"));
		MessageRegistry.getGlobalRegistry().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass
	public void clearMessageRegistry() {
		MessageRegistry.getGlobalRegistry().clear();
	}

    /**
     * Test key verification.
     */
    @Test
    public void testVerification() {
		KeyPair rsaKeyPair = Connection.generateRSAKeyPair();
		Message message = Connection.generatePublicKeyMessage(rsaKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
        Map<InetAddress, Key> keys = new HashMap<>(10);
        keys.put(address, rsaKeyPair.getPublic());

		Connection connection = mock(Connection.class);
		MessageInputStream in = mock(MessageInputStream.class);
        MessageOutputStream out = mock(MessageOutputStream.class);
		MessageContext context = mock(MessageContext.class);

		MessageStreamPair messageStreamPair = new MessageStreamPair(in, out, address);

		when(connection.getMessageStreamPair()).thenReturn(messageStreamPair);
		when(context.getVerifiedKeys()).thenReturn(keys);
		when(context.getAddress()).thenReturn(address);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message, context);
		executable.execute();

		verify(context).setEncryptionKey(rsaKeyPair.getPublic());
	}

    /**
     * Test verification bypass when no verified keys are present.
     */
    @Test
    public void testVerificationNoKeys() {
		KeyPair rsaKeyPair = Connection.generateRSAKeyPair();
		Message message = Connection.generatePublicKeyMessage(rsaKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
        Map<InetAddress, Key> keys = new HashMap<>(10);

		MessageContext context = mock(MessageContext.class);
		Connection connection = mock(Connection.class);
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);
		MessageOutputStream out = mock(MessageOutputStream.class);

		when(messageStreamPair.getOut()).thenReturn(out);
		when(connection.getMessageStreamPair()).thenReturn(messageStreamPair);
		when(context.getAddress()).thenReturn(address);
		when(context.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message, context);
		executable.execute();

		verify(context).setEncryptionKey(rsaKeyPair.getPublic());
	}

    /**
     * Test failed key verification due to invalid key.
     */
    @Test
    public void testVerificationInvalidKey() {
		KeyPair rsaKeyPair = Connection.generateRSAKeyPair();
		KeyPair invalidKeyPair = Connection.generateRSAKeyPair();
		Message message = Connection.generatePublicKeyMessage(invalidKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
        Map<InetAddress, Key> keys = new HashMap<>(10);
        keys.put(address, rsaKeyPair.getPublic());

		MessageContext context = mock(MessageContext.class);
		Connection connection = mock(Connection.class);
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);
		MessageOutputStream out = mock(MessageOutputStream.class);

		when(messageStreamPair.getOut()).thenReturn(out);
		when(connection.getMessageStreamPair()).thenReturn(messageStreamPair);
		when(context.getAddress()).thenReturn(address);
		when(context.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message, context);
		executable.execute();

		verify(context, times(0)).setEncryptionKey(any());
	}

    /**
     * Test invalid key verification due to unknown host.
     */
    @Test
    public void testVerificationNoAddress() {
		KeyPair rsaKeyPair = Connection.generateRSAKeyPair();
		KeyPair invalidKeyPair = Connection.generateRSAKeyPair();
		Message message = Connection.generatePublicKeyMessage(invalidKeyPair.getPublic());
		InetAddress address = InetAddress.getLoopbackAddress();
        Map<InetAddress, Key> keys = new HashMap<>(10);
        keys.put(address, rsaKeyPair.getPublic());

		MessageContext context = mock(MessageContext.class);
		Connection connection = mock(Connection.class);
		MessageStreamPair messageStreamPair = mock(MessageStreamPair.class);
		MessageOutputStream out = mock(MessageOutputStream.class);

		when(messageStreamPair.getOut()).thenReturn(out);
		when(connection.getMessageStreamPair()).thenReturn(messageStreamPair);
		when(context.getVerifiedKeys()).thenReturn(keys);

		ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message, context);
		executable.execute();

		verify(context, times(0)).setEncryptionKey(any());
	}
}
