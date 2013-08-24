package com.jenjinstudios.jgsf.world;

import com.jenjinstudios.jgsf.ClientHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Handles clients for a world server.
 *
 * @author Caleb Brinkman
 */
public class WorldClientHandler extends ClientHandler
{
	/**
	 * Construct a new Client Handler using the given socket.  When constructing a new ClientHandler, it is necessary
	 * to send the client a FirstConnectResponse message with the server's UPS
	 *
	 * @param s  The server for which this handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public WorldClientHandler(WorldServer s, Socket sk) throws IOException
	{
		super(s, sk);
	}
}
