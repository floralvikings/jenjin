package com.jenjinstudios.world;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.AuthClient;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.WorldClientMessageFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WorldClient class is used to connect to a WorldServer and stores information about the environment immediately
 * surrounding the player.
 * @author Caleb Brinkman
 */
public class WorldClient extends AuthClient
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
	/** The number of milliseconds before a blocking method should time out. */
	public static long TIMEOUT_MILLIS = 30000;
	/** The password used to login to the world. */
	private final String password;
	/** The world. */
	private World world;
	/** The actor representing the player controlled by this client. */
	private ClientPlayer player;
	/** Whether this client has received a world file checksum from the server. */
	private boolean hasReceivedWorldFileChecksum;
	/** The world file checksum received from the server. */
	private byte[] serverWorldFileChecksum;
	/** The world file. */
	private File worldFile;
	/** The world file reader for this client. */
	private WorldFileReader worldFileReader;
	/** Whether this client has received the world file. */
	private boolean hasReceivedWorldFile;
	/** The bytes in the world server file. */
	private byte[] serverWorldFileBytes;

	/**
	 * Construct a client connecting to the given address over the given port.  This client <i>must</i> have a username and
	 * password.
	 * @param worldFile The file containing the world information.
	 * @param address The address to which this client will attempt to connect.
	 * @param port The port over which this client will attempt to connect.
	 * @param username The username that will be used by this client.
	 * @param password The password that will be used by this client.
	 * @throws java.security.NoSuchAlgorithmException If there is an error generating encryption keys.
	 * @throws java.io.IOException If there's an error reading the world file.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error configuring the xml parser.
	 * @throws javax.xml.transform.TransformerException If there's an error transforming the xml file.
	 * @throws org.xml.sax.SAXException If there's an error in the XML syntax.
	 */
	public WorldClient(File worldFile, String address, int port, String username, String password)
			throws NoSuchAlgorithmException, SAXException, TransformerException, ParserConfigurationException, IOException
	{
		super(address, port, username, password);
		this.password = password;
		this.worldFile = worldFile;
		if (worldFile.exists())
		{
			this.worldFileReader = new WorldFileReader(worldFile);
			this.world = worldFileReader.read();
		}
	}

	@Override
	public boolean sendBlockingLoginRequest() {
		sendLoginRequest();
		long startTime = System.currentTimeMillis();
		long timePast = System.currentTimeMillis() - startTime;
		while (!isLoggedIn() && isWaitingForLoginResponse() && (timePast < TIMEOUT_MILLIS))
		{
			try
			{
				sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.WARNING, "Interrupted while waiting for login response.", e);
			}
			timePast = System.currentTimeMillis() - startTime;
		}
		return isLoggedIn();
	}

	/**
	 * Get the player associated with this client.
	 * @return The player (ClientActor) associated with this client.
	 */
	public ClientPlayer getPlayer() { return player; }

	/**
	 * Set the player being controlled by this client.
	 * @param player The player to be controlled by this client.
	 */
	public void setPlayer(ClientPlayer player) {
		this.player = player;
	}

	/**
	 * Get the world for this client.
	 * @return The world being managed by this client.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world managed by this client.
	 * @param world The world managed by this client.
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Set whether the world file checksum has been received.
	 * @param hasReceivedWorldFileChecksum Whether the checksum has been received.
	 */
	public void setHasReceivedWorldFileChecksum(boolean hasReceivedWorldFileChecksum) {
		this.hasReceivedWorldFileChecksum = hasReceivedWorldFileChecksum;
	}

	/**
	 * Set the checksum received from the server.
	 * @param serverWorldFileChecksum The checksum received from the server.
	 */
	public void setServerWorldFileChecksum(byte[] serverWorldFileChecksum) {
		this.serverWorldFileChecksum = serverWorldFileChecksum;
	}

	/**
	 * Set whether the client has received the world file.
	 * @param hasReceivedWorldFile Whether the client has received the world file.
	 */
	public void setHasReceivedWorldFile(boolean hasReceivedWorldFile) {
		this.hasReceivedWorldFile = hasReceivedWorldFile;
	}

	/**
	 * Set the bytes of the world file stored on the server.
	 * @param serverWorldFileBytes The bytes.
	 */
	public void setServerWorldFileBytes(byte[] serverWorldFileBytes) {
		this.serverWorldFileBytes = serverWorldFileBytes;
	}

	/**
	 * Send a request for the world file, and wait for the response to return.
	 * @throws InterruptedException If the thread is interrupted while waiting for responses.
	 * @throws java.io.IOException If there's an error writing the world file.
	 * @throws java.security.NoSuchAlgorithmException If the MD5 algorithm can't be found.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error configuring the XML parser.
	 * @throws javax.xml.transform.TransformerException If there's an error with the XML transformer.
	 * @throws org.xml.sax.SAXException If there's an error with the XML.
	 */
	public void sendBlockingWorldFileRequest() throws InterruptedException, NoSuchAlgorithmException, SAXException, TransformerException, ParserConfigurationException, IOException {
		Message worldFileChecksumRequest = WorldClientMessageFactory.generateWorldChecksumRequest(this);
		queueMessage(worldFileChecksumRequest);

		while (!hasReceivedWorldFileChecksum)
		{
			Thread.sleep(10);
		}

		if (worldFileReader == null || !Arrays.equals(serverWorldFileChecksum, worldFileReader.getWorldFileChecksum()))
		{
			queueMessage(WorldClientMessageFactory.generateWorldFileRequest(this));
			while (!hasReceivedWorldFile)
			{
				Thread.sleep(10);
			}
			if ((!worldFile.getParentFile().exists() && !worldFile.getParentFile().mkdirs()) || (!worldFile.exists() &&  !worldFile.createNewFile()))
			{
				throw new IOException("Unable to create new world file!");
			}
			FileOutputStream worldOut = new FileOutputStream(worldFile);
			worldOut.write(serverWorldFileBytes);
			worldOut.close();
			worldFileReader = new WorldFileReader(new ByteArrayInputStream(serverWorldFileBytes));
			world = worldFileReader.read();
		}


	}

	/** Send a LoginRequest to the server. */
	private void sendLoginRequest() {
		Message loginRequest = WorldClientMessageFactory.generateLoginRequest(this, getUsername(), password);
		setWaitingForLoginResponse(true);
		queueMessage(loginRequest);
	}

	/**
	 * Send a state change request to the server.
	 * @param moveState The move state used to generate the request.
	 */
	protected void sendStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = WorldClientMessageFactory.generateStateChangeRequest(this, moveState);
		queueMessage(stateChangeRequest);
	}

	@Override
	protected void sendLogoutRequest() {
		Message logoutRequest = WorldClientMessageFactory.generateWorldLogoutRequest(this);

		// Send the request, continue when response is received.
		setWaitingForLogoutResponse(true);
		queueMessage(logoutRequest);
	}
}
