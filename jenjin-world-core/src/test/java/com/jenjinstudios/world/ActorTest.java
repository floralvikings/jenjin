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
	private static final World world = new World();

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
		waitOneSecond();
		world.update();
		l = System.currentTimeMillis() - l;
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, actor.getMoveSpeed() * ((double) l / 1000), distance * 0.1);
	}

	@Test
	public void testStepToNullLocation() throws InterruptedException {
		Actor actor = new Actor("Actor");
		world.addObject(actor);
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one DEFAULT_MOVE_SPEED forward
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
		Thread.sleep(1000); // Sleep to move one DEFAULT_MOVE_SPEED backward
		world.update();
		Assert.assertNotNull(actor.getForcedState());
	}

	private void waitOneSecond() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 1000) Thread.sleep(1);
	}
}
