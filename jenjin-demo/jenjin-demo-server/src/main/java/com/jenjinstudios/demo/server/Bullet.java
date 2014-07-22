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
	public static final double MAX_RANGE = 250;
	private boolean updatedOnce;
	private Vector2D startVector;
	private Player playerFiring;

	public Bullet(Player playerFiring) {
		super("Bullet");
		this.playerFiring = playerFiring;
		double targetAngle = playerFiring.getAngle().getAbsoluteAngle();
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 10);
		setResourceID(1);
	}

	public Vector2D calculateStartVector() {
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
		if (!updatedOnce)
		{
			startVector = calculateStartVector();
			if (startVector != null)
			{
				setVector2D(startVector);
				updatedOnce = true;
			} else
			{
				getWorld().scheduleForRemoval(this);
			}
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
		incrementDeathCounter(actor);
		incrementKillCounter();
		getWorld().scheduleForRemoval(this);
	}

	private void incrementKillCounter() {
		Object killsProperty = playerFiring.getProperty("kills");
		int kills = killsProperty != null ? (int) killsProperty : 0;
		kills++;
		playerFiring.setProperty("kills", kills);
	}

	private void incrementDeathCounter(Actor actor) {
		if (actor instanceof Player)
		{
			Object deathsProperty = ((Player) actor).getProperty("deaths");
			int deaths = deathsProperty != null ? (int) deathsProperty : 0;
			deaths++;
			((Player) actor).setProperty("deaths", deaths);
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
