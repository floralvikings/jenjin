package test.jenjinstudios.downloadserver;

import com.jenjinstudios.downloadserver.DownloadServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * This test serves to test both the Client and DownloadServer.
 *
 * @author Caleb Brinkman
 */
public class DownloadServerTest
{
	/** The expected number of files to be sent. */
	private static final int numFiles = 2;
	/** The download server to be used for testing. */
	private DownloadServer downloadServer;

	/**
	 * Set up the clients and server, and wait for an appropriate number of update cycles in the server.
	 *
	 * @throws InterruptedException If there is an error during Thread.Sleep()
	 */
	@Before
	public void setUp() throws InterruptedException
	{
		downloadServer = new DownloadServer("jgsf/src/test/resources/");
		downloadServer.blockingStart();
	}

	/** Verify that the server is properly initialized. */
	@Test
	public void testIsInitialized()
	{
		Assert.assertTrue(downloadServer.isInitialized());
	}

	/** Verify the array of needed files. */
	@Test
	public void testGetFileListArray()
	{
		String message = new File(downloadServer.getRootDirectory()).getAbsolutePath();
		Assert.assertEquals(message, numFiles, downloadServer.getFileListArray().length);
	}

	/**
	 * Verify the server's UPS.
	 *
	 * @throws InterruptedException If there's a problem.
	 */
	@Test
	public void testGetAverageUPS() throws InterruptedException
	{
		Thread.sleep(100 * (1000 / DownloadServer.UPS));
		Assert.assertEquals(DownloadServer.UPS, downloadServer.getAverageUPS(), 1.0);
	}

	/**
	 * Shut down the clients and server.
	 *
	 * @throws IOException If there is an error shutting down the clients or server.
	 */
	@After
	public void tearDown() throws IOException
	{
		downloadServer.shutdown();
	}
}
