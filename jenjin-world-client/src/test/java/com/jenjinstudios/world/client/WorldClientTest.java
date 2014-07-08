package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.MessageIO;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageInputStream;
import com.jenjinstudios.core.io.MessageOutputStream;
import com.jenjinstudios.core.io.MessageRegistry;
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
public class WorldClientTest
{
	private static final MessageRegistry messageRegistry = new MessageRegistry();
	private static final String validWorldString =
		  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<world>\n" +
				"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
				"        <location walkable=\"false\" x=\"1\" y=\"1\"/>\n" +
				"    </zone>\n" +
				"</world>\n";

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
	public void testSendBlockingLoginRequest() throws Exception {
		WorldClient wc = getPreparedWorldClient();
		wc.blockingStart();
		wc.sendBlockingWorldFileRequest();
		wc.sendBlockingLoginRequest();
		Assert.assertNotNull(wc.getPlayer());
		wc.shutdown();
	}

	@Test(timeOut = 5000)
	public void testSendBlockingWorldFileRequest() throws Exception {
		WorldClient wc = getPreparedWorldClient();
		wc.blockingStart();
		wc.sendBlockingWorldFileRequest();
		Assert.assertNotNull(wc.getWorld());
		wc.shutdown();
	}

	@BeforeMethod
	@AfterMethod
	public void cleanResources() {
		try
		{
			removeRecursive(new File("resources/").toPath());
		} catch (IOException ignored) { }
	}

	private WorldClient getPreparedWorldClient() throws Exception {
		byte[] file = validWorldString.getBytes(StandardCharsets.UTF_8);
		byte[] checksum = ChecksumUtil.getMD5Checksum(file);

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
		Message worldLoginResponse = getWorldLoginResponse();
		Message worldLogoutResponse = messageRegistry.createMessage("WorldLogoutResponse");
		worldLogoutResponse.setArgument("success", true);


		Mockito.when(inputStream.readMessage()).
			  thenReturn(firstConnectResponse, blankMessageSpam).
			  thenReturn(aesMessage, blankMessageSpam).
			  thenReturn(worldChecksumResponse, blankMessageSpam).
			  thenReturn(worldFileResponse, blankMessageSpam).
			  thenReturn(worldLoginResponse, blankMessageSpam);
		return wc;
	}

	private Message getWorldLoginResponse() {
		Message worldLoginResponse = messageRegistry.createMessage("WorldLoginResponse");
		worldLoginResponse.setArgument("id", 0);
		worldLoginResponse.setArgument("xCoordinate", 0.0d);
		worldLoginResponse.setArgument("yCoordinate", 0.0d);
		worldLoginResponse.setArgument("zoneNumber", 0);
		worldLoginResponse.setArgument("success", true);
		worldLoginResponse.setArgument("loginTime", 123l);
		return worldLoginResponse;
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
