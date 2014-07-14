package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.ChecksumUtil;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Caleb Brinkman
 */
public class ServerWorldFileTrackerTest
{
	private static final MessageRegistry messageRegistry = MessageRegistry.getInstance();
	private static final String validWorldString =
		  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<world>\n" +
				"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
				"        <location walkable=\"false\" x=\"1\" y=\"1\"/>\n" +
				"    </zone>\n" +
				"</world>\n";
	private WorldClient worldClient;

	public static void removeRecursive(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				  throws IOException
			{
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc == null)
				{
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else
				{
					throw exc;
				}
			}
		});
	}

	@Test(timeOut = 5000)
	public void testRequestWorldServerFileChecksum() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

		worldClient.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = worldClient.getServerWorldFileTracker();
		serverWorldFileTracker.requestServerWorldFileChecksum(worldClient);

		Assert.assertEquals(serverWorldFileTracker.getChecksum(), checksum);

		worldClient.shutdown();
	}

	@Test(timeOut = 5000)
	public void testRequestServerWorldFile() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

		worldClient.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = worldClient.getServerWorldFileTracker();
		serverWorldFileTracker.requestServerWorldFileChecksum(worldClient);
		serverWorldFileTracker.requestServerWorldFile(worldClient);

		Assert.assertFalse(serverWorldFileTracker.isWaitingForChecksum());
		Assert.assertFalse(serverWorldFileTracker.isWaitingForFile());
		Assert.assertEquals(serverWorldFileTracker.getChecksum(), checksum);
		Assert.assertEquals(serverWorldFileTracker.getBytes(), file);

		worldClient.shutdown();
	}

	@Test(timeOut = 5000)
	public void testWriteServerWorldToFile() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		worldClient.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = worldClient.getServerWorldFileTracker();
		serverWorldFileTracker.requestServerWorldFileChecksum(worldClient);
		serverWorldFileTracker.requestServerWorldFile(worldClient);
		serverWorldFileTracker.writeReceivedWorldToFile();

		Path writtenFile = new File("resources/ServerWorldFileTracker.xml").toPath();
		byte[] readBytes = Files.readAllBytes(writtenFile);
		Assert.assertEquals(readBytes, file);

		worldClient.shutdown();
	}

	@Test(timeOut = 5000)
	public void testReadWorldFromServer() throws Exception {
		worldClient.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = worldClient.getServerWorldFileTracker();
		serverWorldFileTracker.requestServerWorldFileChecksum(worldClient);
		serverWorldFileTracker.requestServerWorldFile(worldClient);

		World world = serverWorldFileTracker.readWorldFromServer();
		Assert.assertNotNull(world);

		worldClient.shutdown();
	}

	@Test(timeOut = 5000)
	public void testReadWorldFromFile() throws Exception {
		worldClient.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = worldClient.getServerWorldFileTracker();
		serverWorldFileTracker.requestServerWorldFileChecksum(worldClient);
		serverWorldFileTracker.requestServerWorldFile(worldClient);
		serverWorldFileTracker.writeReceivedWorldToFile();

		World world = serverWorldFileTracker.readWorldFromFile();
		Assert.assertNotNull(world);

		worldClient.shutdown();
	}

	@BeforeMethod
	public void setUpWorldClient() throws Exception {
		cleanResources();
		prepareWorldClient();
	}

	@AfterMethod
	public void cleanResources() {
		try
		{
			removeRecursive(new File("resources/").toPath());
		} catch (IOException ignored) { }
		if (worldClient != null)
			worldClient.shutdown();
	}

	private void prepareWorldClient() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

		MessageInputStream inputStream = Mockito.mock(MessageInputStream.class);
		MessageOutputStream outputStream = Mockito.mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(inputStream, outputStream);
		ClientUser clientUser = new ClientUser("Foo", "Bar");
		WorldClient wc = new WorldClient(messageIO, clientUser, new File("resources/ServerWorldFileTracker.xml"));

		Message firstConnectResponse = messageRegistry.createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", 123);
		Message blankMessage = messageRegistry.createMessage("BlankMessage");
		Message worldChecksumResponse = messageRegistry.createMessage("WorldChecksumResponse");
		Message[] blankMessageSpam = new Message[1500];
		for (int i = 0; i < blankMessageSpam.length; i++) { blankMessageSpam[i] = blankMessage; }
		worldChecksumResponse.setArgument("checksum", checksum);
		Message worldFileResponse = messageRegistry.createMessage("WorldFileResponse");
		worldFileResponse.setArgument("fileBytes", file);
		Message aesMessage = getAesKeyMessage(messageRegistry, wc);

		Mockito.when(inputStream.readMessage()).
			  thenReturn(firstConnectResponse, blankMessageSpam).
			  thenReturn(aesMessage, blankMessageSpam).
			  thenReturn(worldChecksumResponse, blankMessageSpam).
			  thenReturn(worldFileResponse, blankMessageSpam);
		this.worldClient = wc;
	}

	private Message getAesKeyMessage(MessageRegistry messageRegistry, WorldClient wc) throws Exception {
		byte[] clientKey = wc.getPublicKey().getEncoded();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		byte[] aesKeyBytes = keyGenerator.generateKey().getEncoded();
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientKey));
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedAESKey = cipher.doFinal(aesKeyBytes);
		Message aesMessage = messageRegistry.createMessage("AESKeyMessage");
		aesMessage.setArgument("key", encryptedAESKey);
		return aesMessage;
	}
}
