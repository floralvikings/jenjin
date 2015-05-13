package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.WorldServerMessageContext;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the ExecutableStateChangeRequest class.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeRequestTest
{
	/**
	 * Register messages for testing.
	 */
	@BeforeClass
	public void registerMessages()
	{
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", stream);
		InputStream coreStream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", coreStream);
	}

	/**
	 * Clear messages after testing.
	 */
	@AfterClass
	public void clearRegistry()
	{
		MessageRegistry.getGlobalRegistry().clear();
	}

	/**
	 * Test a valid state change request.
	 *
	 * @throws InterruptedException If there's an exception.
	 */
	@Test
	public void testValidRequest() throws InterruptedException {
		World world = WorldUtils.createDefaultWorld();
		Player player = new Player("FooBar");
		world.getWorldObjects().add(player);
		world.update();

		Message request = mock(Message.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(context.getUser()).thenReturn(player);
		when(context.getWorld()).thenReturn(world);
		when(request.getArgument("relativeAngle")).thenReturn(Angle.FRONT);
		when(request.getArgument("absoluteAngle")).thenReturn(0.0);
		when(request.getArgument("xCoordinate")).thenReturn(0.0);
		when(request.getArgument("yCoordinate")).thenReturn(0.0);
		when(request.getArgument("timeOfChange")).thenReturn(System.currentTimeMillis());

		ExecutableStateChangeRequest exec = new ExecutableStateChangeRequest(request, context);
		exec.execute();
		world.update();

		Assert.assertEquals(player.getOrientation(), new Angle(0.0, Angle.FRONT), "Player should be at angle 0.0 and " +
			  "idle");
	}

	/**
	 * Test a state change request with invalid coordinates.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testInvalidRequestCoordinates() throws Exception {
		World world = WorldUtils.createDefaultWorld();
		Player player = new Player("FooBar");

		// Add player and update world
		world.getWorldObjects().add(player);
		world.update();

		WorldServerMessageContext context = mock(WorldServerMessageContext.class);
		when(context.getUser()).thenReturn(player);
		when(context.getWorld()).thenReturn(world);

		// Create a state change request, with coordinates set further than the allowed error
		Message request = mock(Message.class);
		when(request.getArgument("relativeAngle")).thenReturn(Angle.FRONT);
		when(request.getArgument("absoluteAngle")).thenReturn(0.0);
		when(request.getArgument("xCoordinate")).thenReturn(15.0);
		when(request.getArgument("yCoordinate")).thenReturn(15.0);
		when(request.getArgument("timeOfChange")).thenReturn(System.currentTimeMillis());

		// Create the executable state change request
		ExecutableStateChangeRequest exec = new ExecutableStateChangeRequest(request, context);
		exec.execute();

		world.update();

		Assert.assertEquals(player.getOrientation(), new Angle(0.0, Angle.IDLE), "Player should be at 0.0 and idle.");
	}

	/**
	 * Test a state change request with an invalid request time.
	 */
	@Test
	public void testInvalidRequestTime() {
		// Functionally the same as testing excessive delay.
		World world = WorldUtils.createDefaultWorld();
		Player player = new Player("FooBar");
		world.getWorldObjects().add(player);
		world.update();

		Message request = mock(Message.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(context.getUser()).thenReturn(player);
		when(context.getWorld()).thenReturn(world);
		when(request.getArgument("relativeAngle")).thenReturn(Angle.FRONT);
		when(request.getArgument("absoluteAngle")).thenReturn(0.0);
		when(request.getArgument("xCoordinate")).thenReturn(0.0);
		when(request.getArgument("yCoordinate")).thenReturn(0.0);
		when(request.getArgument("timeOfChange")).thenReturn(System.currentTimeMillis() - 2000);
		ExecutableStateChangeRequest exec = new ExecutableStateChangeRequest(request, context);
		exec.execute();
		world.update();

		Assert.assertEquals(player.getOrientation(), new Angle(0.0, Angle.IDLE), "Player should be at angle 0.0 and idle");
	}

	/**
	 * Test a state change request with an excessive amount of delay (lag).
	 *
	 * @throws InterruptedException If there's an exception.
	 */
	@Test
	public void testExcessiveDelay() throws InterruptedException {
		World world = WorldUtils.createDefaultWorld();
		Player player = new Player("FooBar");
		world.getWorldObjects().add(player);
		world.update();

		Message request = mock(Message.class);
		WorldServerMessageContext context = mock(WorldServerMessageContext.class);

		when(context.getUser()).thenReturn(player);
		when(context.getWorld()).thenReturn(world);
		when(request.getArgument("relativeAngle")).thenReturn(Angle.FRONT);
		when(request.getArgument("absoluteAngle")).thenReturn(0.0);
		when(request.getArgument("xCoordinate")).thenReturn(0.0);
		when(request.getArgument("yCoordinate")).thenReturn(0.0);
		when(request.getArgument("timeOfChange")).thenReturn(System.currentTimeMillis());
		Thread.sleep(1200);
		ExecutableStateChangeRequest exec = new ExecutableStateChangeRequest(request, context);
		exec.execute();
		world.update();

		Assert.assertEquals(player.getOrientation(), new Angle(0.0, Angle.IDLE), "Player should be at angle 0.0 and idle.");
	}
}
