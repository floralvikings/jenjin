package com.jenjinstudios.downloadserver;

import com.jenjinstudios.jgsf.ClientListener;
import com.jenjinstudios.jgsf.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import static com.jenjinstudios.downloadserver.DownloadServer.LOGGER;

/**
 * Listens for new clients on behalf of a DownloadServer.
 *
 * @author Caleb Brinkman
 */
class DownloadListener extends ClientListener
{
	/**
	 * Construct a new DownloadListener for the given server and port.
	 *
	 * @param s The server.
	 * @param p The port number.
	 * @throws IOException If there is an error listening on the port.
	 */
	public DownloadListener(Server s, int p) throws IOException
	{
		super(s, p);
	}

	/**
	 * Adds a new client to the server using the specified socket.
	 *
	 * @param sock The socket used to communicate with the new client.
	 */
	@Override
	public void addNewClient(Socket sock)
	{
		try
		{
			DownloadServer dlServer = (DownloadServer) getServer();
			addNewClient(new DownloadClientHandler(dlServer, sock));
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Error", ex);
		}
	}
}
