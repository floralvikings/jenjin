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
	private static World world = new World();

	@Test
	public void testReset() {
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.FRONT);
		actor.setAngle(angle);
		actor.reset();
		Assert.assertEquals(actor.getStateChanges().get(0).angle, angle);
	}

	@Test
	public void testSetUp() {
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
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.FRONT);
		actor.setAngle(angle);
		world.update();
		long l = System.currentTimeMillis();
		wait(100);
		world.update();
		l = System.currentTimeMillis() - l;
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, Actor.MOVE_SPEED * ((double) l / 1000), 0.1);
	}

	@Test
	public void testStepToNullLocation() throws InterruptedException {
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
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one MOVE_SPEED forward
		world.update();
		Assert.assertNotNull(actor.getForcedState());
	}

	private void wait(int waitTime) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < waitTime) Thread.sleep(1);
	}
}
