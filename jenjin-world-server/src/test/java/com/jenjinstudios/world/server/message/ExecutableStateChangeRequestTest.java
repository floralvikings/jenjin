package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldClientHandler;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequestTest
{
	private static MessageRegistry messageRegistry = new MessageRegistry();

	@Test
	public void testValidRequest() throws InterruptedException {
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = Mockito.mock(WorldClientHandler.class);
		Mockito.when(mock.getPlayer()).thenReturn(player);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.nanoTime());
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
		WorldClientHandler mock = Mockito.mock(WorldClientHandler.class);
		Mockito.when(mock.getPlayer()).thenReturn(player);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 1.0);
		request.setArgument("yCoordinate", 1.0);
		request.setArgument("timeOfChange", System.nanoTime());
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
		World world = new World();
		Player player = new Player("FooBar");
		world.addObject(player);
		WorldClientHandler mock = Mockito.mock(WorldClientHandler.class);
		Mockito.when(mock.getPlayer()).thenReturn(player);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.nanoTime() - 10000000);
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
		WorldClientHandler mock = Mockito.mock(WorldClientHandler.class);
		Mockito.when(mock.getPlayer()).thenReturn(player);
		Message request = messageRegistry.createMessage("StateChangeRequest");
		request.setArgument("relativeAngle", Angle.FRONT);
		request.setArgument("absoluteAngle", 0.0);
		request.setArgument("xCoordinate", 0.0);
		request.setArgument("yCoordinate", 0.0);
		request.setArgument("timeOfChange", System.nanoTime());
		Thread.sleep(2000);
		ExecutableStateChangeRequest executableStateChangeRequest = new ExecutableStateChangeRequest(mock, request);
		executableStateChangeRequest.runImmediate();
		executableStateChangeRequest.runDelayed();
		player.setUp();
		player.update();
		player.reset();

		Assert.assertEquals(player.getAngle(), new Angle(0.0, Angle.IDLE));
	}
}
