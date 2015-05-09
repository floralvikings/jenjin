package com.jenjinstudios.demo.server;

import com.jenjinstudios.demo.server.event.Collision;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * @author Caleb Brinkman
 */
public class Bullet extends Actor
{
	public static final double RANGE = 100;
	private final Vector2D startVector;

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

		addPostUpdateEvent("Collision", new BulletCollision(actorFiring));

		addPreUpdateEvent("RangeStop", () -> {
			if (!getAngle().isNotIdle() || startVector.getDistanceToVector(getVector2D()) > RANGE)
			{
				getWorld().getWorldObjects().remove(Bullet.this);
			}
		});

	}

	private class BulletCollision extends Collision
	{
		private final Actor actorFiring;

		private BulletCollision(Actor actorFiring) {
			super(Bullet.this);
			this.actorFiring = actorFiring;
		}

		@Override
		public void onCollision(WorldObject collided) {
			if (!(collided instanceof Bullet) && collided instanceof Actor && collided != actorFiring) {
				Actor actor = (Actor) collided;
				Angle idle = new Angle();
				if (idle.getAbsoluteAngle() == collided.getAngle().getAbsoluteAngle()) {
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
