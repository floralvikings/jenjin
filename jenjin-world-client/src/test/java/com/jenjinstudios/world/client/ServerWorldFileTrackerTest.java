package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.ChecksumUtil;
import junit.framework.Assert;
import org.mockito.Mockito;
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
	private static final String validWorldString =
		  "<world>\n" +
				"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
				"       <location x=\"1\" y=\"1\" walkable=\"false\" />\n" +
				"    </zone>\n" +
				"</world>";

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

	@Test
	public void testGetServerWorldFile() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

		WorldClient wc = getPreparedWorldClient(file, checksum);

		wc.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = wc.getServerWorldFileTracker();
		serverWorldFileTracker.getServerWorldFile();

		Assert.assertFalse(serverWorldFileTracker.isWaitingForChecksum());
		Assert.assertFalse(serverWorldFileTracker.isWaitingForFile());
		Assert.assertEquals(serverWorldFileTracker.getChecksum(), checksum);
		Assert.assertEquals(serverWorldFileTracker.getBytes(), file);

		try
		{
			removeRecursive(new File("resources/").toPath());
		} catch (IOException ignored) { }
	}

	@Test
	public void testReadWorldFile() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

		WorldClient wc = getPreparedWorldClient(file, checksum);

		wc.blockingStart();

		ServerWorldFileTracker serverWorldFileTracker = wc.getServerWorldFileTracker();
		serverWorldFileTracker.getServerWorldFile();

		World world = serverWorldFileTracker.readWorldFile();
		Assert.assertNotNull(world);

		try
		{
			removeRecursive(new File("resources/").toPath());
		} catch (IOException ignored) { }
	}

	private WorldClient getPreparedWorldClient(byte[] file, byte[] checksum) throws Exception {
		MessageRegistry messageRegistry = new MessageRegistry();
		MessageInputStream inputStream = Mockito.mock(MessageInputStream.class);
		MessageOutputStream outputStream = Mockito.mock(MessageOutputStream.class);
		MessageIO messageIO = new MessageIO(inputStream, outputStream, messageRegistry);
		ClientUser clientUser = new ClientUser("Foo", "Bar");
		WorldClient wc = new WorldClient(messageIO, clientUser, new File("resources/ServerWorldFileTracker.xml"));

		Message firstConnectResponse = messageRegistry.createMessage("FirstConnectResponse");
		firstConnectResponse.setArgument("ups", 123);
		Message blankMessage = messageRegistry.createMessage("BlankMessage");
		Message worldChecksumResponse = messageRegistry.createMessage("WorldChecksumResponse");
		Message[] blankMessageSpam = new Message[1000];
		for (int i = 0; i < blankMessageSpam.length; i++) { blankMessageSpam[i] = blankMessage; }
		worldChecksumResponse.setArgument("checksum", checksum);
		Message worldFileResponse = messageRegistry.createMessage("WorldFileResponse");
		worldFileResponse.setArgument("fileBytes", file);
		Message aesMessage = getAesKeyMessage(messageRegistry, wc);

		Mockito.when(inputStream.readMessage()).
			  thenReturn(firstConnectResponse, blankMessageSpam).
			  thenReturn(aesMessage, blankMessageSpam).
			  thenReturn(worldChecksumResponse, blankMessageSpam).
			  thenReturn(worldFileResponse);
		return wc;
	}

	private Message getAesKeyMessage(MessageRegistry messageRegistry, WorldClient wc) throws Exception {
		byte[] clientKey = wc.getClientPublicKey().getEncoded();
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
