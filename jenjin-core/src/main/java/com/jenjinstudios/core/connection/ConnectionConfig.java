package com.jenjinstudios.core.connection;

import com.jenjinstudios.core.concurrency.MessageContext;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;

/**
 * Used to configure a connection on construction.
 *
 * @author Caleb Brinkman
 */
public class ConnectionConfig<T extends MessageContext>
{
    private InetAddress address;
    private Class<T> contextClass;
	private Collection<String> messageRegistryFiles;
	private boolean secure;
	private int port;

	/**
	 * Get the message registry files for this configuration.
	 *
	 * @return The message registry files for this configuration.
	 */
	public Collection<String> getMessageRegistryFiles() {
		return Collections.unmodifiableCollection(messageRegistryFiles);
	}

	/**
	 * Get the address the connection with this configuration should connect to.
	 *
	 * @return The address the connection with this configuration should connect to.
	 */
	public InetAddress getAddress() { return address; }

	/**
	 * Get whether the connection with this configuration should use SSL sockets.
	 *
	 * @return Whether the connection with this configuration should use SSL sockets.
	 */
	public boolean isSecure() { return secure; }

	/**
	 * Get the String representation of the MessageContext subclass used by the connection.
	 *
	 * @return The name of the MessageContext class used by the connection.
	 */
	public Class<T> getContextClass() { return contextClass; }

	/**
	 * Get the port over which the connection will communicate.
	 *
	 * @return The port over which the connection will communicate.
	 */
	public int getPort() { return port; }
}
