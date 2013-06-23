package com.jenjinstudios.chatserver;

import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;


/**
 * Handles a single client on behalf of a Server.
 *
 * @author Caleb Brinkman
 */
public class ChatClientHandler extends ClientHandler
{
	/** The chat group ID numbers in which this client has permission to broadcast. */
	private HashSet<Integer> groupPermissions;
	/** The chat group ID numbers to which this client belongs. */
	private HashSet<Integer> chatGroups;
	/** The server this clienthandler will work for. */
	Server<ChatClientHandler> server;

	/**
	 * Construct a new Client Handler using the given socket.
	 *
	 * @param s  The server for which this client handler works.
	 * @param sk The socket used to communicate with the client.
	 * @throws java.io.IOException If the socket is unable to connect.
	 */
	public ChatClientHandler(Server<ChatClientHandler> s, Socket sk) throws IOException
	{
		super(s, sk);
		server = s;
		chatGroups = new HashSet<>();
		groupPermissions = new HashSet<>();
		groupPermissions.add(0);
		chatGroups.add(0);
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

	/**
	 * Returns whether this client has permission to broadcast in the specified chat group.
	 *
	 * @param groupID The ID of the group to check
	 * @return true if the client has permission, false otherwise.
	 */
	public boolean hasChatPermission(int groupID)
	{
		return groupPermissions.contains(groupID);
	}

	/**
	 * Returns whether this client is in the given chat group.
	 *
	 * @param groupID The ID of the chat group
	 * @return true if the client is in the chat group, false otherwise.
	 */
	public boolean inChatGroup(int groupID)
	{
		return chatGroups.contains(groupID);
	}

	/**
	 * The server.
	 *
	 * @return The server for which this client handler works.
	 */
	public Server<ChatClientHandler> getServer()
	{
		return server;
	}
}