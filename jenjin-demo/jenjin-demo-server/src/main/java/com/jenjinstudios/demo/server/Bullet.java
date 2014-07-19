package com.jenjinstudios.demo.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import java.util.Collection;

import static com.jenjinstudios.world.Location.SIZE;
import static com.jenjinstudios.world.math.Angle.*;

/**
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	public static final double MAX_RANGE = 100;
	private final Vector2D firedFrom;
	private final double targetAngle;
	private Vector2D startVector;
	private boolean updatedOnce;

	public Bullet(Vector2D firedFrom, double targetAngle) {
		super("Bullet");
		this.firedFrom = firedFrom;
		this.targetAngle = targetAngle;
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 1.5);
		setResourceID(1);
	}

	public Vector2D calculateStartVector(Vector2D firedFrom, double targetAngle) {
		Vector2D startVector = getVector2D();

		if (targetAngle == FRONT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() + SIZE, firedFrom.getYCoordinate());
		} else if (targetAngle == FRONT_LEFT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() + SIZE, firedFrom.getYCoordinate() + SIZE);
		} else if (targetAngle == LEFT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate(), firedFrom.getYCoordinate() + SIZE);
		} else if (targetAngle == BACK_LEFT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() - SIZE, firedFrom.getYCoordinate() + SIZE);
		} else if (targetAngle == BACK)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() - SIZE, firedFrom.getYCoordinate());
		} else if (targetAngle == BACK_RIGHT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() - SIZE, firedFrom.getYCoordinate() - SIZE);
		} else if (targetAngle == RIGHT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate(), firedFrom.getYCoordinate() - SIZE);
		} else if (targetAngle == FRONT_RIGHT)
		{
			startVector = new Vector2D(firedFrom.getXCoordinate() + SIZE, firedFrom.getYCoordinate() - SIZE);
		}
		Location loc = getWorld().getLocationForCoordinates(getZoneID(), startVector);
		if (loc == null) startVector = getVector2D();
		return startVector;
	}

	@Override
	public void update() {
		super.update();
		if (!updatedOnce)
		{
			startVector = calculateStartVector(firedFrom, targetAngle);
			setVector2D(startVector);
			//forcePosition();
			updatedOnce = true;
		} else
		{
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
	}

	public void hitActor(Actor actor) {
		actor.setVector2D(Vector2D.ORIGIN);
		actor.forceIdle();
		getWorld().scheduleForRemoval(this);
	}

	private void tryHitActor(Collection<WorldObject> objects) {
		for (WorldObject object : objects)
		{
			if (object != this && object instanceof Actor && !(object instanceof Bullet))
			{
				hitActor((Actor) object);
				break;
			}
		}
	}
}
