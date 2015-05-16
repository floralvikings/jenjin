package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ActorTest
{
	private static final World world = WorldUtils.createDefaultWorld();

	@Test
	public void testStepToNullLocation() throws InterruptedException {
		Actor actor = new Actor("Actor");
		world.getWorldObjects().add(actor);
		world.update();
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.getGeometry2D().setOrientation(angle);
		world.update();
		Thread.sleep(1000); // Sleep to move one DEFAULT_MOVE_SPEED forward
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getGeometry2D().getPosition());
		Assert.assertEquals(distance, 0, 0.1);
	}

	@Test
	public void testGetForcedState() throws InterruptedException {
		Actor actor = new Actor("Actor");
		world.getWorldObjects().add(actor);
		Angle angle = new Angle(0.0, Angle.BACK);
		actor.getGeometry2D().setOrientation(angle);
		Thread.sleep(1000); // Sleep to move one DEFAULT_MOVE_SPEED backward
		world.update();
		Assert.assertNotNull(actor.getForcedState());
	}

}
