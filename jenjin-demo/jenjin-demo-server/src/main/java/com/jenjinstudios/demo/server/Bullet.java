package com.jenjinstudios.demo.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import java.util.Collection;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	private static final double MAX_RANGE = 250;
	private boolean initialized;
	private Vector2D startVector;
	private final DemoPlayer playerFiring;

	public Bullet(DemoPlayer playerFiring) {
		super("Bullet");
		this.playerFiring = playerFiring;
		double targetAngle = playerFiring.getAngle().getAbsoluteAngle();
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 3);
		setResourceID(1);
	}

	protected Vector2D calculateStartVector() {
		startVector = playerFiring.getVector2D();

		Location loc = getWorld().getLocationForCoordinates(getZoneID(), startVector);
		if (loc == null)
		{
			startVector = null;
		}
		return startVector;
	}

	@Override
	public void update() {
		super.update();
		if (!initialized)
		{
			initialize();
		} else
		{
			checkForHit();
		}
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
		if (getAngle().getRelativeAngle() == Angle.IDLE || distance > MAX_RANGE)
		{
			getWorld().scheduleForRemoval(this);
		}
	}

	private void initialize() {
		startVector = calculateStartVector();
		if (startVector != null)
		{
			setVector2D(startVector);
			initialized = true;
		} else
		{
			getWorld().scheduleForRemoval(this);
		}
	}

	protected void hitActor(Actor actor) {
		System.out.println("Hit");
		actor.setVector2D(Vector2D.ORIGIN);
		actor.forceIdle();
		if (actor instanceof DemoPlayer)
		{
			((DemoPlayer) actor).incrementDeathCounter();
		}
		playerFiring.incrementKillCounter();
		getWorld().scheduleForRemoval(this);
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
