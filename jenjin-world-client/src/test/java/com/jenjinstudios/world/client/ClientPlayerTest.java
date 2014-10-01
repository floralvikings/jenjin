package com.jenjinstudios.world.client;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientPlayerTest
{
	@Test(timeOut = 5000)
	public void testSetAngle() {
		World world = WorldUtils.createDefaultWorld();
		Actor player = new Actor("ClientActor");
		world.getWorldObjects().scheduleForAddition(player);
		world.update();
		Angle angle = new Angle(Angle.LEFT, Angle.FRONT);
		player.setAngle(angle);
		world.update();
		Assert.assertEquals(player.getAngle(), angle);
	}

	@Test(timeOut = 5000)
	public void testSetAngleToForcedPosition() {
		World world = WorldUtils.createDefaultWorld();
		Actor player = new Actor("ClientActor");
		world.getWorldObjects().scheduleForAddition(player);
		world.update();
		player.forceIdle();
		Angle angle = player.getAngle();
		player.setAngle(angle);
		Assert.assertEquals(player.getAngle(), angle);
	}

	@Test(timeOut = 5000)
	public void testForcePosition() {
		World world = WorldUtils.createDefaultWorld();
		Actor player = new Actor("ClientActor");
		player.setId(0);
		world.getWorldObjects().scheduleForAddition(player);
		world.update();
		player.forceIdle();
		Assert.assertNotNull(player.getForcedState());
	}

	@Test(timeOut = 5000)
	public void testStep() throws InterruptedException {
		World world = WorldUtils.createDefaultWorld();
		Actor player = new Actor("ClientActor");
		world.getWorldObjects().scheduleForAddition(player);
		world.update();
		Angle angle = new Angle(0, Angle.FRONT);
		player.setAngle(angle);
		world.update();
		long l = System.currentTimeMillis();
		waitOneSecond();
		l = System.currentTimeMillis() - l;
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(player.getVector2D());
		double expectedDistance = player.getMoveSpeed() * ((double) l / 1000);
		Assert.assertEquals(distance, expectedDistance, expectedDistance * 0.1);
	}

	private void waitOneSecond() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 1000) Thread.sleep(1);
	}
}
