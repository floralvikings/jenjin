package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientActorTest
{
	@Test
	public void testUpdate() {
		World world = new World();
		ClientActor actor = new ClientActor(0, "ClientActor");
		world.addObject(actor);
		actor.update();
		Assert.assertNotEquals(actor.getLastStepTime(), 0);
	}

	@Test
	public void testStep() throws InterruptedException {
		World world = new World();
		ClientActor actor = new ClientActor(0, "ClientActor");
		world.addObject(actor);
		Angle angle = new Angle(0, Angle.FRONT);
		actor.setAngle(angle);
		world.update();
		Thread.sleep(1000);
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, ClientActor.MOVE_SPEED, 0.1);
	}
}
