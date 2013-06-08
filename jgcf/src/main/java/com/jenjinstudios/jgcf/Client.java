package com.jenjinstudios.jgcf;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.io.BaseMessage;
import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.message.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for any client.  This class uses a similar system to the JGSA.
 *
 * @author Caleb Brinkman
 */
public class Client extends Thread
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	/** The port over which the client communicates with the server. */
	private final int PORT;
	/** The address of the server to which this client will connect. */
	private final String ADDRESS;
	/** The collection of messages to send at the next broadcast. */
	private final LinkedList<BaseMessage> outgoingMessages;
	/** The list of received chat messages. */
	private final LinkedList<ChatBroadcast> chatMessages;
	/** The period of the update in milliseconds. */
	private int period;
	/** The socket used to connect to the server. */
	private Socket socket;
	/** Flags whether this client is connected. */
	private volatile boolean connected;
	/** The timer that manages the update loop. */
	private Timer sendMessagesTimer;
	/** Flags whether the client threads should be running. */
	private volatile boolean running;
	/** The FileListMessage sent by the server, if any. */
	private ArrayList<String> fileList;
	/** Whether the user is logged in. */
	private boolean loggedIn;
	/** The inputstream used to read messages from the server. */
	private MessageInputStream inputStream;
	/** The output stream used to write messages to the server. */
	private MessageOutputStream outputStream;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** Counts the number of files downloaded. */
	private int numReceivedFiles;
	/** Flags whether the client has received files. */
	private boolean receivedAllFiles;
	/** flags whether the login response has been received. */
	private volatile boolean receivedLoginResponse;
	/** flags whether the logout response has been received. */
	private volatile boolean receivedLogoutResponse;
	/** The username this client will use when logging in. */
	private String username;
	/** The password this client will use when logging in. */
	private String password;
	/** The list of tasks that this client will execute each update cycle. */
	private final LinkedList<Runnable> repeatedTasks;
	/** Flags whether the file list has been received. */
	private volatile boolean hasReceivedFileList;

	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 *
	 * @param address The address of the server to which to connect
	 * @param port    The port over which to connect to the server.
	 */
	public Client(String address, int port)
	{
		ADDRESS = address;
		PORT = port;
		connected = false;
		outgoingMessages = new LinkedList<>();
		chatMessages = new LinkedList<>();
		repeatedTasks = new LinkedList<>();
		if (MessageRegistry.hasMessagesRegistered())
			return;
		MessageRegistry.registerAllBaseMessages();
	}

	/**
	 * Construct a client connecting to the given address over the given port.
	 *
	 * @param address  The address to which this client will attempt to connect.
	 * @param port     The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 */
	public Client(String address, int port, String username, String password)
	{
		this(address, port);
		this.username = username;
		this.password = password;
	}

	/**
	 * Attempt to connect to the server at {@code ADDRESS} over {@code PORT}  This method must be
	 * called <i>before</i> the client thread is started.
	 */
	private void connect()
	{
		if (isConnected())
			return;
		try
		{
			socket = new Socket(ADDRESS, PORT);
			inputStream = new MessageInputStream(socket.getInputStream());
			FirstConnectResponse firstConnectResponse = (FirstConnectResponse) inputStream.readMessage();
			outputStream = new MessageOutputStream(socket.getOutputStream());
			/* The ups of this client. */
			int ups = firstConnectResponse.UPS;
			period = 1000 / ups;
			connected = true;
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to connect to server.", ex);
		}
	}

	/**
	 * Add a task to the repeated queue of this client.
	 *
	 * @param r The task to be performed.
	 */
	protected void addRepeatedTask(Runnable r)
	{
		synchronized (repeatedTasks)
		{
			repeatedTasks.add(r);
		}
	}

	/**
	 * Send all messages in the outgoing queue.  This method should only be called from the client update thread.
	 *
	 * @throws IOException If there is an error writing to the output stream.
	 */
	private void sendAllMessages() throws IOException
	{
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
				sendMessage(outgoingMessages.pop());
		}
	}

	/**
	 * Queue a message in the outgoing messages. This method is thread safe.
	 *
	 * @param message The message to add to the outgoing queue.
	 */
	public void queueMessage(BaseMessage message)
	{
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Request the list of files that can be downloaded.  It is important to note that the message sent does nothing on
	 * the server by default.  The com.jenjinstudios.downloadserver module contains an implementation of Server that
	 * allows for this functionality.  This method blocks until the file list has been received.
	 */
	public final void requestFileList()
	{
		queueMessage(new FileRequest(""));
		while (!hasReceivedFileList)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.INFO, "Interrupted", e);
			}
	}

	/** Request all files needed that have been pulled from the FileListMessage sent from the server. */
	public final void requestNeededFiles()
	{
		if (fileList == null)
			return;
		for (String fileName : fileList)
			queueMessage(new FileRequest(fileName));
		while (!receivedAllFiles)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.INFO, "Interrupted", e);
			}
	}

	/**
	 * Send a chat message to the server.
	 *
	 * @param message The message to send to the server.
	 */
	public final void sendChatMessage(ChatMessage message)
	{
		queueMessage(message);
	}

	/**
	 * Get all the chat messages collected since the last check.
	 *
	 * @return A {@code LinkedList} of all the ChatMessages collected since the last time this method was
	 *         called.
	 */
	public final LinkedList<ChatBroadcast> getChatMessages()
	{
		LinkedList<ChatBroadcast> temp;
		synchronized (chatMessages)
		{
			temp = new LinkedList<>(chatMessages);
			chatMessages.clear();
		}
		return temp;
	}

	/**
	 * Send the specified message.  This method should only be called from the client update thread.
	 *
	 * @param message The message to be sent.
	 * @throws IOException if there is an error writing to the message stream.
	 */
	private void sendMessage(BaseMessage message) throws IOException
	{
		outputStream.writeMessage(message);
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does
	 * contain functionality necessary to communicate with a DownloadServer or a ChatServer.
	 *
	 * @param message The message to be processed.
	 * @throws IOException If there is an IO error.
	 */
	protected void processMessage(Object message) throws IOException
	{
		if (message instanceof FileMessage)
		{
			processFileMessage((FileMessage) message);
		} else if (message instanceof FileListMessage)
		{
			processFileListMessage((FileListMessage) message);
			hasReceivedFileList = true;
		} else if (message instanceof ChatBroadcast)
		{
			processChatBroadcast((ChatBroadcast) message);
		} else if (message instanceof LoginResponse)
		{
			processLoginResponse((LoginResponse) message);
		} else if (message instanceof LogoutResponse)
		{
			receivedLogoutResponse = true;
			LogoutResponse logoutResponse = (LogoutResponse) message;
			loggedIn = !logoutResponse.SUCCESS;
		}
	}

	/**
	 * Process a login response message.
	 *
	 * @param message The login response.
	 */
	protected void processLoginResponse(LoginResponse message)
	{
		receivedLoginResponse = true;
		loggedIn = message.SUCCESS;
		if (!loggedIn)
			return;
		loggedInTime = message.LOGIN_TIME;
		super.setName("Client: " + username);
	}

	/**
	 * Add a chat message to the incoming chat message queue.
	 *
	 * @param message The chat message that hasbeen received.
	 */
	protected void processChatBroadcast(ChatBroadcast message)
	{
		synchronized (chatMessages)
		{
			chatMessages.add(message);
		}
	}

	/**
	 * Create the file sent by the file message.  This implementation assumes that if you have received this message,
	 * the file should be downloaded.  Therefore, it overwrites any files already in place.  This behavior is intended,
	 * as only needed files should actually be requested and therefore sent.
	 *
	 * @param message The {@code FileMessage} sent from the server.
	 * @throws IOException if there is an error downloading or writing the file.
	 */
	protected void processFileMessage(FileMessage message) throws IOException
	{
		String fileName = message.FILENAME;
		File newFile = new File(fileName);
		// Create directories for the parent file
		File parent = newFile.getParentFile();
		if (parent != null && parent.mkdirs())
			LOGGER.log(Level.FINER, "Creating new directory for {0}", fileName);
		// Delete the file if it already exists
		if (newFile.exists())
			if (!newFile.delete())
				throw new IOException("Unable to delete file: " + fileName);
		// Try and create the new file
		if (!newFile.createNewFile())
			throw new IOException("Error creating new file: " + fileName);
		// Output the message bytes to the file.
		FileOutputStream outputStream = new FileOutputStream(newFile);
		outputStream.write(message.BYTES);
		outputStream.close();
		numReceivedFiles++;
		if (numReceivedFiles == fileList.size() - 1)
			receivedAllFiles = true;
	}

	/**
	 * Process a {@code FileListMessage} as sent from the server.  The file list is assumed to be an array of
	 * {@code File} objects, to be downloaded directly into the runtime directory.  If the file list has already
	 * been requested and received, this does nothing.
	 *
	 * @param message The message from the server.
	 */
	protected void processFileListMessage(FileListMessage message)
	{
		if (fileList != null)
			return;

		fileList = new ArrayList<>();
		String[] files = message.FILE_LIST;
		String[] hashes = message.MD5_HASH_LIST;


		for (int i = 0; i < files.length; i++)
		{
			String currentFileName = files[i];
			File currentFile = new File(currentFileName);
			String currentHash = hashes[i];
			// Check to see if the file doesn't exist, and if it does, make sure the checksum is correct
			if (!currentFile.exists() || !FileUtil.getMD5Checksum(currentFile).equals(currentHash))
				fileList.add(currentFile.getPath());
		}
	}

	/** Tell the client threads to stop running. */
	public void shutdown()
	{
		running = false;
		closeLink();
	}

	/** Close the link with the server. */
	private void closeLink()
	{
		sendMessagesTimer.cancel();

		try
		{
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex)
		{
			// Do nothing since we're closing the link anyway?
		} finally
		{
			connected = false;
		}
	}

	/** Queue a message to log into the server with the given username and password, and wait for the response. */
	public void sendLoginRequest()
	{
		if (username == null || password == null)
		{
			LOGGER.log(Level.WARNING, "Attmepted to login without username/password");
			return;
		}
		receivedLoginResponse = false;
		queueMessage(new LoginRequest(username, password));
		while (!receivedLoginResponse)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Queue a message to log the user out of the server. */
	public void sendLogoutRequest()
	{
		receivedLogoutResponse = false;
		queueMessage(new LogoutRequest());
		while (!receivedLogoutResponse)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
	}

	/** Start the client, blocking until the client has successfully initialized. */
	public void blockingStart()
	{
		start();
		while (!running) try
		{
			Thread.sleep(1);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Issue with client blockingStart", e);
		}
	}

	public final void run()
	{
		if (!isConnected())
			connect();
		running = true;
		sendMessagesTimer = new Timer("Client Update Loop", false);
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(), 0, period);
		try
		{
			Object currentMessage;
			while ((currentMessage = inputStream.readMessage()) != null && running)
				processMessage(currentMessage);
		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Error retrieving message from server.", ex);
		} finally
		{
			shutdown();
		}
	}

	/**
	 * Flags whether the client threads should be running.
	 *
	 * @return true if this client thread is still running.
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Flags whether this client is connected.
	 *
	 * @return true if this client is currently connected to a server.
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Get the File List sent by the server, if any.
	 *
	 * @return The list of files sent by the server that this client will need to download.
	 */
	public ArrayList<String> getFileList()
	{
		return fileList;
	}

	/**
	 * Get whether this client is logged in.
	 *
	 * @return true if this client has received a successful LoginResponse
	 */
	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	/**
	 * Get the time at which this client was successfully logged in.
	 *
	 * @return The time of the start of the server cycle during which this client was logged in.
	 */
	public long getLoggedInTime()
	{
		return loggedInTime;
	}

	/**
	 * Get whether the client has received files.
	 *
	 * @return true if the client has received all needed files from the server.
	 */
	public boolean hasReceivedAllFiles()
	{
		return receivedAllFiles;
	}

	/** The ClientLoop class is essentially what amounts to the output thread. */
	private class ClientLoop extends TimerTask
	{
		@Override
		public void run()
		{
			for (Runnable r : repeatedTasks)
				r.run();
			try
			{
				sendAllMessages();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
