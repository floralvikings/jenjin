package test.jenjinstudios.downloadserver;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.downloadclient.DownloadClient;
import com.jenjinstudios.downloadserver.DownloadServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Test the download features of the JGC.
 *
 * @author Caleb Brinkman
 */
public class DownloadClientTest
{
	/** The expected number of files to be sent. */
	private static final int numFiles = 2;
	/** The download server to be used for testing. */
	private static DownloadServer downloadServer;
	/** The client to be used for testing. */
	private static DownloadClient client;

	/**
	 * Set up the clients and server, and wait for an appropriate number of update cycles in the server.
	 *
	 * @throws InterruptedException If there is an error during Thread.Sleep()
	 */
	@Before
	public void setUp() throws InterruptedException
	{
		downloadServer = new DownloadServer("downloadServer/src/test/resources/");
		downloadServer.blockingStart();

		client = new DownloadClient("localhost", DownloadServer.PORT);
		client.blockingStart();
		client.requestFileList();
		client.requestNeededFiles();
	}

	/** Verify the number of clients. */
	@Test
	public void testGetNumClients()
	{

		Assert.assertEquals(1, downloadServer.getNumClients());
	}

	/** Verify that the client has received all files. */
	@Test
	public void testReceivedAllFiles()
	{
		Assert.assertTrue(client.hasReceivedAllFiles());
	}

	/** Verify that the client is running. */
	@Test
	public void testIsRunning()
	{
		Assert.assertTrue(client.isRunning());
	}

	/** Verify that the client is connected. */
	@Test
	public void testIsConnected()
	{
		Assert.assertTrue(client.isConnected());
	}

	/** Verify that the client has received the correct file list. */
	@Test
	public void testFileList()
	{
		Assert.assertEquals(numFiles, client.getFileList().size());
	}

	/** Verify that the proper files were downloaded, without mistakes. */
	@Test
	public void testDownloadedFiles()
	{
		String[] serverHashes = downloadServer.getFileHashArray();

		ArrayList<String> clientFiles = client.getFileList();
		String[] clientHashes = new String[clientFiles.size()];
		for (int i = 0; i < clientFiles.size(); i++)
			clientHashes[i] = FileUtil.getMD5Checksum(new File(clientFiles.get(i)));
		for (int i = 0; i < clientFiles.size(); i++)
		{
			Assert.assertTrue(serverHashes[i].equals(clientHashes[i]));
		}
	}

	/**
	 * Shut down the clients and server.
	 *
	 * @throws IOException If there is an error shutting down the clients or server.
	 */
	@After
	public void tearDown() throws IOException
	{
		ArrayList<String> clientFiles = client.getFileList();
		for (String s : clientFiles)
		{
			File file = new File(s);
			if (!file.delete()) throw new IOException();
			if (s.contains(File.separator))
				if (!file.getParentFile().delete()) throw new IOException();
		}
		client.shutdown();
		downloadServer.shutdown();
	}
}
