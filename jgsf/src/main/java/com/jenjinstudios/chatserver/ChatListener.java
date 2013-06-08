package com.jenjinstudios.chatserver;

import com.jenjinstudios.jgsf.ClientListener;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import static com.jenjinstudios.chatserver.ChatServer.LOGGER;

/**
 * The ChatListener listens for new clients on behalf of a ChatServer.
 *
 * @author Caleb Brinkman
 */
class ChatListener extends ClientListener
{

	/**
	 * Construct a new ChatListener listening for the specified ChatServer on the Specified port.
	 *
	 * @param s The chatserver
	 * @param p The port number
	 * @throws IOException If there is an error listening for clients.
	 */
	public ChatListener(ChatServer s, int p) throws IOException
	{
		super(s, p);
	}

	/**
	 * Add a new client to the server using the specified socket.
	 *
	 * @param sock The socket used to communicate with the new client.
	 */
	@Override
	public void addNewClient(Socket sock)
	{
		try
		{
			LOGGER.log(Level.FINE, "Adding new client");
			ChatServer chatServer = (ChatServer) getServer();
			addNewClient(new ChatClientHandler(chatServer, sock));
		} catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, "Error", ex);
		}
	}
}

