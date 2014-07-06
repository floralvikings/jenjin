package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
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
}
