package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import com.jenjinstudios.world.server.WorldServer;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequestTest
{
	private static MessageRegistry messageRegistry = MessageRegistry.getInstance();

	@Test
	public void testValidRequest() throws InterruptedException {
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = mock(WorldClientHandler.class);
		when(mock.getPlayer()).thenReturn(player);
		WorldServer worldServer = mock(WorldServer.class);
		when(mock.getServer()).thenReturn(worldServer);
		when(worldServer.getUps()).thenReturn(50);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.currentTimeMillis());
		ExecutableStateChangeRequest executableStateChangeRequest = new ExecutableStateChangeRequest(mock, request);
		executableStateChangeRequest.runImmediate();
		executableStateChangeRequest.runDelayed();

		player.setUp();
		player.update();
		player.reset();

		Assert.assertEquals(player.getAngle(), new Angle(0.0, Angle.FRONT));
	}

	@Test
	public void testInvalidRequestCoordinates() {
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = mock(WorldClientHandler.class);
		when(mock.getPlayer()).thenReturn(player);
		WorldServer worldServer = mock(WorldServer.class);
		when(mock.getServer()).thenReturn(worldServer);
		when(worldServer.getUps()).thenReturn(50);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 10.0);
		request.setArgument("yCoordinate", 10.0);
		request.setArgument("timeOfChange", System.currentTimeMillis());
		ExecutableStateChangeRequest executableStateChangeRequest = new ExecutableStateChangeRequest(mock, request);
		executableStateChangeRequest.runImmediate();
		executableStateChangeRequest.runDelayed();

		player.setUp();
		player.update();
		player.reset();

		Assert.assertEquals(player.getAngle(), new Angle(0.0, Angle.IDLE));
	}

	@Test
	public void testInvalidRequestTime() {
		// Functionally the same as testing excessive delay.
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = mock(WorldClientHandler.class);
		when(mock.getPlayer()).thenReturn(player);
		WorldServer worldServer = mock(WorldServer.class);
		when(mock.getServer()).thenReturn(worldServer);
		when(worldServer.getUps()).thenReturn(50);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.currentTimeMillis() - 2000);
		ExecutableStateChangeRequest executableStateChangeRequest = new ExecutableStateChangeRequest(mock, request);
		executableStateChangeRequest.runImmediate();
		executableStateChangeRequest.runDelayed();

		player.setUp();
		player.update();
		player.reset();

		Assert.assertEquals(player.getAngle(), new Angle(0.0, Angle.IDLE));
	}

	@Test
	public void testExcessiveDelay() throws InterruptedException {
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = mock(WorldClientHandler.class);
		WorldServer worldServer = mock(WorldServer.class);
		when(mock.getServer()).thenReturn(worldServer);
		when(worldServer.getUps()).thenReturn(50);
		when(mock.getPlayer()).thenReturn(player);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.currentTimeMillis());
		Thread.sleep(1200);
		ExecutableStateChangeRequest executableStateChangeRequest = new ExecutableStateChangeRequest(mock, request);
		executableStateChangeRequest.runImmediate();
		executableStateChangeRequest.runDelayed();
		player.setUp();
		player.update();
		player.reset();

		Assert.assertEquals(player.getAngle(), new Angle(0.0, Angle.IDLE));
	}
}
