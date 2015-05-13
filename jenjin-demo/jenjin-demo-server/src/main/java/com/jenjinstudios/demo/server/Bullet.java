package com.jenjinstudios.demo.server;

import com.jenjinstudios.demo.server.event.Collision;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.task.WorldObjectTaskAdapter;

import java.util.Objects;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * Represents a bullet in the game world.
 *
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	private static final double RANGE = 100;
	private final Vector2D startVector;

	/**
	 * Construct a new Bullet, fired from the specified actor.
	 *
	 * @param actorFiring The actor which fired the bullet.
	 */
	public Bullet(Actor actorFiring) {
		super("Bullet");
		getProperties().put(Collision.SIZE_PROPERTY, 1.0);
		setVector2D(actorFiring.getVector2D());
		double targetAngle = actorFiring.getAngle().getAbsoluteAngle();
		setAngle(new Angle(targetAngle, FRONT));
		setMoveSpeed(Actor.DEFAULT_MOVE_SPEED * 3);
		setResourceID(1);
		setZoneID(actorFiring.getZoneID());
		startVector = getVector2D();

		addTask(new BulletCollision(actorFiring));

		addTask(new CheckRangeTask());

	}

	private class CheckRangeTask extends WorldObjectTaskAdapter
	{
		@Override
		public void onPreUpdate(World world, WorldObject worldObject) {
			if (!worldObject.getAngle().isNotIdle() ||
				  (startVector.getDistanceToVector(worldObject.getVector2D()) > RANGE))
			{
				world.getWorldObjects().remove(worldObject);
			}
		}
	}

	private class BulletCollision extends Collision
	{
		private static final float FLOAT_COMPARE_TOLERANCE = 0.01F;
		private final Actor actorFiring;

		private BulletCollision(Actor actorFiring) {
			this.actorFiring = actorFiring;
		}

		@Override
		public void onCollision(WorldObject target, WorldObject collided) {
			if (!(collided instanceof Bullet) && (collided instanceof Actor) && !Objects.equals(collided,
				  actorFiring))
			{
				Actor actor = (Actor) collided;
				Angle idle = new Angle();
				double comparison = idle.getAbsoluteAngle() - collided.getAngle().getAbsoluteAngle();
				boolean floatsEqual = Math.abs(comparison) < FLOAT_COMPARE_TOLERANCE;
				if (floatsEqual) {
					idle = idle.reverseAbsoluteAngle();
				}
				long forceTime = collided.getWorld().getLastUpdateCompleted();
				actor.setVector2D(Vector2D.ORIGIN);
				actor.setForcedState(new MoveState(idle, Vector2D.ORIGIN, forceTime));
				getWorld().getWorldObjects().remove(Bullet.this);
			}
		}
	}
}
