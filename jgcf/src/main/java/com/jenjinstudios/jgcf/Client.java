package com.jenjinstudios.jgcf;

import com.jenjinstudios.io.MessageInputStream;
import com.jenjinstudios.io.MessageOutputStream;
import com.jenjinstudios.jgcf.message.ClientExecutableMessage;
import com.jenjinstudios.message.ExecutableMessage;
import com.jenjinstudios.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.util.LinkedList;
import java.util.Timer;
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
	private final LinkedList<Message> outgoingMessages;
	/** The list of tasks that this client will execute each update cycle. */
	private final LinkedList<Runnable> repeatedSyncedTasks;
	/** The "one-shot" tasks to be executed in the current client loop. */
	private final LinkedList<Runnable> syncedTasks;
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
	/** Whether the user is logged in. */
	private boolean loggedIn;
	/** The input stream used to read messages from the server. */
	private MessageInputStream inputStream;
	/** The output stream used to write messages to the server. */
	private MessageOutputStream outputStream;
	/** The time at which this client was successfully logged in. */
	private long loggedInTime;
	/** flags whether the login response has been received. */
	private volatile boolean receivedLoginResponse;
	/** flags whether the logout response has been received. */
	private volatile boolean receivedLogoutResponse;
	/** The username this client will use when logging in. */
	private String username;
	/** The password this client will use when logging in. */
	private String password;
	/** The public key sent to the server. */
	private PublicKey publicKey;
	/** The private key sent to the server. */
	private PrivateKey privateKey;
	/** The AES key of this client. */
	private byte[] aesKey;


	/**
	 * Construct a new client and attempt to connect to the server over the specified port.
	 *
	 * @param address The address of the server to which to connect
	 * @param port    The port over which to connect to the server.
	 */
	protected Client(String address, int port)
	{
		ADDRESS = address;
		PORT = port;
		connected = false;
		outgoingMessages = new LinkedList<>();
		repeatedSyncedTasks = new LinkedList<>();
		syncedTasks = new LinkedList<>();

		try
		{
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(512);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to create RSA key pair!", e);
		}

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

			outputStream = new MessageOutputStream(socket.getOutputStream());
			inputStream = new MessageInputStream(socket.getInputStream());

			Message firstConnectResponse = inputStream.readMessage();
			/* The ups of this client. */
			int ups = (int) firstConnectResponse.getArgument("ups");
			period = 1000 / ups;
			connected = true;

		} catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, "Unable to connect to server.", ex);
		}
	}

	/**
	 * Add a task to the repeated queue of this client.  Should be called to extend client functionality.
	 *
	 * @param r The task to be performed.
	 */
	@SuppressWarnings("unused")
	protected void addRepeatedTask(Runnable r)
	{
		synchronized (repeatedSyncedTasks)
		{
			repeatedSyncedTasks.add(r);
		}
	}

	/**
	 * Send all messages in the outgoing queue.  This method should only be called from the client update thread.
	 *
	 * @throws IOException If there is an error writing to the output stream.
	 */
	protected void sendAllMessages() throws IOException
	{
		synchronized (outgoingMessages)
		{
			while (!outgoingMessages.isEmpty())
				outputStream.writeMessage(outgoingMessages.pop());
		}
	}

	/**
	 * Queue a message in the outgoing messages. This method is thread safe.
	 *
	 * @param message The message to add to the outgoing queue.
	 */
	public void sendMessage(Message message)
	{
		synchronized (outgoingMessages)
		{
			outgoingMessages.add(message);
		}
	}

	/**
	 * Process the specified message.  This method should be overridden by any implementing classes, but it does
	 * contain functionality necessary to communicate with a DownloadServer or a ChatServer.
	 *
	 * @param message The message to be processed.
	 * @throws IOException If there is an IO error.
	 */
	protected void processMessage(Message message) throws IOException
	{
		ExecutableMessage exec;
		exec = ClientExecutableMessage.getClientExecutableMessageFor(this, message);
		if (exec != null)
		{
			exec.runASync();
			synchronized (syncedTasks)
			{
				syncedTasks.add(exec);
			}
		} else
		{
			this.shutdown();
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
			LOGGER.log(Level.WARNING, "Attempted to login without username or password");
			return;
		}
		receivedLoginResponse = false;
		// Create the login request.
		Message loginRequest = new Message("LoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);

		sendMessage(loginRequest);
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
		Message logoutRequest = new Message("LogoutRequest");
		sendMessage(logoutRequest);
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
		try
		{
			while (!running)
				Thread.sleep(1);
			while (aesKey == null)
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
		sendMessagesTimer.scheduleAtFixedRate(new ClientLoop(this), 0, period);

		Message publicKeyMessage = new Message("PublicKeyMessage");
		publicKeyMessage.setArgument("key", publicKey.getEncoded());

		sendMessage(publicKeyMessage);

		try
		{
			Message currentMessage;
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
	 * Get whether this client is logged in.
	 *
	 * @return true if this client has received a successful LoginResponse
	 */
	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	/**
	 * Set whether this client is logged in.
	 *
	 * @param l Whether this client is logged in.
	 */
	public void setLoggedIn(boolean l)
	{
		loggedIn = l;
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
	 * Set the logged in time for this client.
	 *
	 * @param loggedInTime The logged in time for this client.
	 */
	public void setLoggedInTime(long loggedInTime)
	{
		this.loggedInTime = loggedInTime;
	}

	/**
	 * Get the list of repeating tasks.
	 *
	 * @return The list of repeating tasks.
	 */
	public LinkedList<Runnable> getRepeatedSyncedTasks()
	{
		return repeatedSyncedTasks;
	}

	/**
	 * The "one-shot" tasks to be executed in the current client loop.
	 *
	 * @return The list of Synced Tasks
	 */
	protected LinkedList<Runnable> getSyncedTasks()
	{
		LinkedList<Runnable> temp = new LinkedList<>();
		synchronized (syncedTasks)
		{
			temp.addAll(syncedTasks);
			syncedTasks.removeAll(temp);
		}
		return temp;

	}

	/**
	 * Set whether this client has received a login response.
	 *
	 * @param receivedLoginResponse Whether this client has received a login response.
	 */
	public void setReceivedLoginResponse(boolean receivedLoginResponse)
	{
		this.receivedLoginResponse = receivedLoginResponse;
	}

	/**
	 * Get the username of this client.
	 *
	 * @return The username of this client.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Set whether this client has received a logout response.
	 *
	 * @param receivedLogoutResponse Whether this client has received a logout response.
	 */
	public void setReceivedLogoutResponse(boolean receivedLogoutResponse)
	{
		this.receivedLogoutResponse = receivedLogoutResponse;
	}

	/**
	 * Get the private key.
	 *
	 * @return The private key.
	 */
	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}

	/**
	 * Set the AES key used by this client.
	 *
	 * @param key The key used by this client.
	 */
	public void setAESKey(byte[] key)
	{
		aesKey = key;
		inputStream.setAESKey(key);
		outputStream.setAesKey(key);
	}
}
