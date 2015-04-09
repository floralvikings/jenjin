package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.StateChangeStack;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Test the WorldServerMessageFactory class.
 *
 * @author Caleb Brinkman
 */
public class WorldServerMessageFactoryTest
{

	/**
	 * Register XML messages for use in testing.
	 */
	@BeforeClass
	public void registerMessages() {
		MessageRegistry.getGlobalRegistry().register("Core Message Registry",
			  getClass().getClassLoader().getResourceAsStream("com/jenjinstudios/world/server/Messages.xml"));
	}

	/**
	 * Clear the message registry.
	 */
	@AfterClass
	public void clearMessageRegistry() {
		MessageRegistry.getGlobalRegistry().clear();
	}

	/**
	 * Test generation of an ObjectVisible Message.
	 */
	@Test
	public void testGenerateNewlyVisibleObjectMessage() {
		WorldObject object = mock(WorldObject.class);
		when(object.getName()).thenReturn("Foo");
		when(object.getVector2D()).thenReturn(Vector2D.ORIGIN);

		Message message = WorldServerMessageFactory.generateNewlyVisibleMessage(object);
		assertEquals(message.name, "ObjectVisibleMessage", "Message should be ObjectVisibleMessage");
		assertEquals(message.getArgument("name"), "Foo", "Argument should be Foo");
	}

	/**
	 * Test generation of an ActorVisibleMessage.
	 */
	@Test
	public void testGenerateNewlyVisibleActorMessage() {
		Actor actor = mock(Actor.class);
		when(actor.getName()).thenReturn("Foo");
		when(actor.getVector2D()).thenReturn(Vector2D.ORIGIN);
		when(actor.getAngle()).thenReturn(new Angle());
		when(actor.getWorld()).thenReturn(WorldUtils.createDefaultWorld());

		Message message = WorldServerMessageFactory.generateNewlyVisibleMessage(actor);
		assertEquals(message.name, "ActorVisibleMessage", "Message should be ActorVisibleMessage");
		assertEquals(message.getArgument("name"), "Foo", "Name should be Foo.");
		assertEquals(message.getArgument("relativeAngle"), Angle.IDLE, "Relative angle should be idle.");
	}

	/**
	 * Test generation of a StateChangeMessage.
	 */
	@Test
	public void testGenerateStateChangeMesage() {
		Actor actor = mock(Actor.class);
		StateChangeStack stack = mock(StateChangeStack.class);
		MoveState m = new MoveState(new Angle(), Vector2D.ORIGIN, 0);
		when(stack.getStateChanges()).thenReturn(Arrays.asList(m));
		when(actor.getEventStack(StateChangeStack.STACK_NAME)).thenReturn(stack);
		List<Message> messages = WorldServerMessageFactory.generateChangeStateMessages(actor);

		Message newState = messages.get(0);
		assertEquals(newState.getArgument("id"), actor.getId(), "Id should be actor id.");
		assertEquals(newState.getArgument("relativeAngle"), m.angle.getRelativeAngle(), "Angles should be equal.");
		assertEquals(newState.getArgument("absoluteAngle"), m.angle.getAbsoluteAngle(), "Angles should be equal.");
		assertEquals(newState.getArgument("timeOfChange"), m.timeOfChange, "Times of change should be equal.");
		assertEquals(newState.getArgument("xCoordinate"), m.position.getXCoordinate(), "Coordinates should be equal.");
		assertEquals(newState.getArgument("yCoordinate"), m.position.getYCoordinate(), "Coordinates should be equal.");
	}

	@Test
	public void testGenerateForcedStateMessage() {
		MoveState forcedState = new MoveState(new Angle(), Vector2D.ORIGIN, 0);
		Message message = WorldServerMessageFactory.generateForcedStateMessage(forcedState);
		assertEquals(message.name, "ForceStateMessage");
		assertEquals(message.getArgument("relativeAngle"), forcedState.angle.getRelativeAngle());
		assertEquals(message.getArgument("absoluteAngle"), forcedState.angle.getAbsoluteAngle());
		assertEquals(message.getArgument("xCoordinate"), forcedState.position.getXCoordinate());
		assertEquals(message.getArgument("yCoordinate"), forcedState.position.getYCoordinate());
		long result = 0L;
		assertEquals(message.getArgument("timeOfForce"), result);
	}

	@Test
	public void testGenerateActorMoveSpeedMessage() {
		Message message = WorldServerMessageFactory.generateActorMoveSpeedMessage(Actor.DEFAULT_MOVE_SPEED);
		assertEquals(message.name, "ActorMoveSpeed");
		assertEquals(message.getArgument("moveSpeed"), Actor.DEFAULT_MOVE_SPEED);
	}

	@Test
	public void testGenerateNewlyInvisibleMessage() {
		WorldObject o = mock(WorldObject.class);
		when(o.getId()).thenReturn(2468);
		Message message = WorldServerMessageFactory.generateNewlyInvisibleMessage(o);
		assertEquals(message.name, "ObjectInvisibleMessage");
		assertEquals(message.getArgument("id"), 2468);
	}

	@Test
	public void testGenerateWorldFileResponse() {
		byte[] fileBytes = {2, 4, 6, 8, 10};
		Message message = WorldServerMessageFactory.generateWorldFileResponse(fileBytes);
		assertEquals(message.name, "WorldFileResponse");
		assertEquals(message.getArgument("fileBytes"), fileBytes);
	}

	@Test
	public void testGenerateWorldChecksumResponse() {
		byte[] checksum = {2, 4, 6, 8, 10};
		Message message = WorldServerMessageFactory.generateWorldChecksumResponse(checksum);
		assertEquals(message.name, "WorldChecksumResponse");
		assertEquals(message.getArgument("checksum"), checksum);
	}

	@Test
	public void testGenerateWorldLoginResponse() {
		Message message = WorldServerMessageFactory.generateWorldLoginResponse();
		Assert.assertEquals(message.name, "WorldLoginResponse");
	}
}
