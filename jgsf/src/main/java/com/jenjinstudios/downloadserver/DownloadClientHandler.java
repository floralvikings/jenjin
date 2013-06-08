package com.jenjinstudios.downloadserver;

import com.jenjinstudios.jgsf.ClientHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles messages to and from one client of a DownloadServer.
 *
 * @author Caleb Brinkman
 */
class DownloadClientHandler extends ClientHandler
{
	/**
	 * Construct a new Client Handler using the given socket.
	 *
	 * @param s  The DownloadServer for which this client handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public DownloadClientHandler(DownloadServer s, Socket sk) throws IOException
	{
		super(s, sk);
	}

	/** Update anything that needs to be taken care of before broadcast. */
	@Override
	public void update()
	{
	}

	/** Reset anything that needs to be taken care of after broadcast. */
	@Override
	public void refresh()
	{
	}

}
