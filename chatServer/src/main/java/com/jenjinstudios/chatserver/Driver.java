package com.jenjinstudios.chatserver;

import com.jenjinstudios.jgsf.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is included to initialize and run a ChatServer.
 * @author Caleb Brinkman
 */
public class Driver
{
	/**
	 * This method starts a ChatServer, then waits for command line input to be "quit" before shutting down the server.
	 * @param args Command line arguments.
	 * @throws IOException If there's an error reading from a client.
	 */
	public static void main(String[] args) throws IOException
	{
		Server<ChatClientHandler> chatServer;
		chatServer = new Server<>(50, 51019, ChatClientHandler.class);
		chatServer.blockingStart();

		BufferedReader commandLineReader = new BufferedReader(new InputStreamReader(System.in));
		String readLine = commandLineReader.readLine();

		System.out.println("Enter \"q\" to quit.");

		while(!readLine.equals("q"))
			readLine = commandLineReader.readLine();

		chatServer.shutdown();
	}
}
