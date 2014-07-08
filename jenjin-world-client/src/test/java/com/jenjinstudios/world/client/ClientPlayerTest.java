package com.jenjinstudios.world.client;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ClientPlayerTest
{
	@Test
	public void testSetAngleNoUpdate() {
		World world = new World();
		ClientPlayer player = new ClientPlayer(0, "ClientActor");
		world.addObject(player);
		Angle angle = new Angle(Angle.LEFT, Angle.FRONT);
		player.setAngle(angle);
		Assert.assertNotEquals(player.getAngle(), angle);
	}

	@Test
	public void testSetAngle() {
		World world = new World();
		ClientPlayer player = new ClientPlayer(0, "ClientActor");
		world.addObject(player);
		Angle angle = new Angle(Angle.LEFT, Angle.FRONT);
		player.setAngle(angle);
		world.update();
		Assert.assertEquals(player.getAngle(), angle);
	}

	@Test
	public void testSetAngleToForcedPosition() {
		World world = new World();
		ClientPlayer player = new ClientPlayer(0, "ClientActor");
		world.addObject(player);
		player.forcePosition();
		Angle angle = player.getAngle();
		player.setAngle(angle);
		Assert.assertEquals(player.getAngle(), angle);
	}

	@Test
	public void testForcePosition() {
		World world = new World();
		ClientPlayer player = new ClientPlayer(0, "ClientActor");
		world.addObject(player);
		player.forcePosition();
		Assert.assertNotNull(player.getForcedState());
	}

	@Test
	public void testStep() throws InterruptedException {
		World world = new World();
		ClientPlayer player = new ClientPlayer(0, "ClientActor");
		world.addObject(player);
		Angle angle = new Angle(0, Angle.FRONT);
		player.setAngle(angle);
		world.update();
		long l = System.currentTimeMillis();
		wait(100);
		l = System.currentTimeMillis() - l;
		world.update();
		double distance = Vector2D.ORIGIN.getDistanceToVector(player.getVector2D());
		Assert.assertEquals(distance, ClientActor.MOVE_SPEED * ((double) l / 1000), 0.1);
	}

	private void wait(int waitTime) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < waitTime) Thread.sleep(1);
	}
}
