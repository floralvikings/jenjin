package com.jenjinstudios.demo.server;

import com.jenjinstudios.demo.server.event.Collision;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.task.WorldObjectTaskAdapter;

import java.util.Objects;

import static com.jenjinstudios.world.math.Angle.FRONT;

/**
 * Represents a bullet in the game world.
 *
 * @author Caleb Brinkman
 */
public class Bullet extends WorldObject
{
	private static final double RANGE = 100;
	private static final double BULLET_SPEED = 90.0d;
	private final Vector startVector;

	/**
	 * Construct a new Bullet, fired from the specified actor.
	 *
	 * @param actorFiring The actor which fired the bullet.
	 */
	public Bullet(WorldObject actorFiring) {
		super("Bullet");
		getGeometry().setSize(new Vector2D(1.0, 1.0));
		getGeometry().setPosition(actorFiring.getGeometry().getPosition());
		double targetAngle = actorFiring.getGeometry().getOrientation().getAbsoluteAngle();
		getGeometry().setOrientation(new Angle(targetAngle, FRONT));
		getGeometry().setSpeed(BULLET_SPEED);
		getIdentification().setTypeId(1);
		startVector = getGeometry().getPosition();

		addTask(new BulletCollision(actorFiring));

		addTask(new CheckRangeTask());

	}

	private class CheckRangeTask extends WorldObjectTaskAdapter
	{

		@Override
		public void onPreUpdate(Node node) {
			if(node instanceof WorldObject) {
				WorldObject worldObject = (WorldObject) node;
				Angle orientation = worldObject.getGeometry().getOrientation();
				Vector position = worldObject.getGeometry().getPosition();
				if(!orientation.isNotIdle() || (startVector.getDistanceToVector(position) > RANGE)) {
					worldObject.getParent().removeChildRecursively(worldObject);
				}

			}
		}
	}

	private static class BulletCollision extends Collision
	{
		private static final float FLOAT_COMPARE_TOLERANCE = 0.01F;
		private final WorldObject actorFiring;

		private BulletCollision(WorldObject actorFiring) {
			this.actorFiring = actorFiring;
		}

		@Override
		public void onCollision(WorldObject object, WorldObject collider) {
			if(!Objects.equals(collider, actorFiring)) {
				Angle idle = new Angle();
				double comparison = idle.getAbsoluteAngle()
					  - collider.getGeometry().getOrientation().getAbsoluteAngle();
				boolean floatsEqual = Math.abs(comparison) < FLOAT_COMPARE_TOLERANCE;
				if (floatsEqual) {
					idle = idle.reverseAbsoluteAngle();
				}
				collider.getGeometry().setPosition(Vector2D.ORIGIN);
				collider.getGeometry().setOrientation(idle);
				collider.getParent().removeChildRecursively(collider);
			}
		}

	}
}
