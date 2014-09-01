package com.jenjinstudios.demo.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;

import java.util.Collection;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	private static final double MAX_RANGE = 250;
	private final Player playerFiring;
	private final Vector2D startVector;

	public Bullet(Player playerFiring) {
		super("Bullet");
		this.playerFiring = playerFiring;
		setVector2D(playerFiring.getVector2D());
		startVector = getVector2D();
		double targetAngle = playerFiring.getAngle().getAbsoluteAngle();
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 3);
		setResourceID(1);
		setZoneID(playerFiring.getZoneID());
	}

	@Override
	public void update() {
		super.update();
		checkForHit();
	}

	protected void hitActor(Actor actor) {
		actor.setVector2D(Vector2D.ORIGIN);
		actor.forceIdle();
		getWorld().getWorldObjects().scheduleForRemoval(this);
	}

	private void checkForHit() {
		Location loc = getLocation();
		if (loc != null)
		{
			Collection<WorldObject> objects = loc.getObjects();
			if (objects.size() > 1)
			{
				tryHitActor(objects);
			}
		}
		double distance = getVector2D().getDistanceToVector(startVector);
		if (getAngle().getRelativeAngle() == Angle.IDLE)
		{
			getWorld().getWorldObjects().scheduleForRemoval(this);
		} else if (distance > MAX_RANGE)
		{
			getWorld().getWorldObjects().scheduleForRemoval(this);
		}
	}

	private void tryHitActor(Collection<WorldObject> objects) {
		for (WorldObject object : objects)
		{
			if (object != this && object instanceof Actor && !(object instanceof Bullet) && object != playerFiring)
			{
				hitActor((Actor) object);
				break;
			}
		}
	}
}
