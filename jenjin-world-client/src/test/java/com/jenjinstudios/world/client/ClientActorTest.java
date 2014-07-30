package com.jenjinstudios.world.client;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientActorTest
{
	@Test
	public void testUpdate() {
		World world = Mockito.mock(World.class);
		ClientActor actor = new ClientActor(0, "ClientActor");
		actor.setWorld(world);
		actor.update();
		Assert.assertNotEquals(actor.getLastStepTime(), 0);
	}

	@Test
	public void testStep() throws InterruptedException {
		World world = new World();
		ClientActor actor = new ClientActor(0, "ClientActor");
		world.update();
		world.addObject(actor);
		Angle angle = new Angle(0, Angle.FRONT);
		actor.setAngle(angle);
		world.update();
		long l = System.currentTimeMillis();
		waitOneSecond();
		world.update();
		l = System.currentTimeMillis() - l;
		double distance = Vector2D.ORIGIN.getDistanceToVector(actor.getVector2D());
		Assert.assertEquals(distance, actor.getMoveSpeed() * ((double) l / 1000), 0.1);
	}

	private void waitOneSecond() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 1000) Thread.sleep(1);
	}
}
