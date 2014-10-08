package com.jenjinstudios.demo.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	public Bullet(Actor actorFiring) {
		super("Bullet");
		setVector2D(actorFiring.getVector2D());
		double targetAngle = actorFiring.getAngle().getAbsoluteAngle();
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 3);
		setResourceID(1);
		setZoneID(actorFiring.getZoneID());
	}

	@Override
	public void update() {
		super.update();
		checkForHit();
	}

	private void checkForHit() {
		// TODO rewrite with proper collision
	}

}
