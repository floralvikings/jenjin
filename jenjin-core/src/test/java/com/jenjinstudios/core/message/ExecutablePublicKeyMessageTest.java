package com.jenjinstudios.core.message;

import com.jenjinstudios.core.Connection;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
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
		MessageRegistry.getInstance().register("Test Message Registry",
			  getClass().getClassLoader().getResourceAsStream("test/jenjinstudios/core/Messages.xml"));
		MessageRegistry.getInstance().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/core/io/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass
	public void clearMessageRegistry() {
		MessageRegistry.getInstance().clear();
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

        MessageIO messageIO = new MessageIO(in, out, address);

        when(connection.getMessageIO()).thenReturn(messageIO);
        when(connection.getVerifiedKeys()).thenReturn(keys);

        ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
        executable.runImmediate();

        verify(out).setPublicKey(rsaKeyPair.getPublic());
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

        Connection connection = mock(Connection.class);
        MessageIO messageIO = mock(MessageIO.class);
        MessageOutputStream out = mock(MessageOutputStream.class);

        when(messageIO.getOut()).thenReturn(out);
        when(messageIO.getAddress()).thenReturn(address);
        when(connection.getMessageIO()).thenReturn(messageIO);
        when(connection.getVerifiedKeys()).thenReturn(keys);

        ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
        executable.runImmediate();

        verify(out).setPublicKey(rsaKeyPair.getPublic());
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

        Connection connection = mock(Connection.class);
        MessageIO messageIO = mock(MessageIO.class);
        MessageOutputStream out = mock(MessageOutputStream.class);

        when(messageIO.getOut()).thenReturn(out);
        when(messageIO.getAddress()).thenReturn(address);
        when(connection.getMessageIO()).thenReturn(messageIO);
        when(connection.getVerifiedKeys()).thenReturn(keys);

        ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
        executable.runImmediate();

        verify(out, times(0)).setPublicKey(any());
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

        Connection connection = mock(Connection.class);
        MessageIO messageIO = mock(MessageIO.class);
        MessageOutputStream out = mock(MessageOutputStream.class);

        when(messageIO.getOut()).thenReturn(out);
        when(connection.getMessageIO()).thenReturn(messageIO);
        when(connection.getVerifiedKeys()).thenReturn(keys);

        ExecutablePublicKeyMessage executable = new ExecutablePublicKeyMessage(connection, message);
        executable.runImmediate();

        verify(out, times(0)).setPublicKey(any());
    }
}
