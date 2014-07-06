package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ActorTest
{
	@Test
	public void testReset() {
		World world = new World();
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.FRONT);
		actor.setAngle(angle);
		actor.reset();
		Assert.assertEquals(actor.getStateChanges().get(0).angle, angle);
	}

	@Test
	public void testSetUp() {
		World world = new World();
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.FRONT);
		actor.setAngle(angle);
		world.update();
		actor.setUp();
		Assert.assertEquals(actor.getStateChanges().size(), 0);
	}

	@Test
	public void testStep() throws InterruptedException {
		World world = new World();
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.FRONT);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one MOVE_SPEED forward
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, Actor.MOVE_SPEED, 0.1);
	}

	@Test
	public void testStepToNullLocation() throws InterruptedException {
		World world = new World();
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one MOVE_SPEED forward
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, 0, 0.1);
	}

	@Test
	public void testGetForcedState() throws InterruptedException {
		World world = new World();
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one MOVE_SPEED forward
		world.update();
		Assert.assertNotNull(actor.getForcedState());
	}
}
