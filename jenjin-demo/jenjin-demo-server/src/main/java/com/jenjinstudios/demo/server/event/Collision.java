package com.jenjinstudios.demo.server.event;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.task.WorldObjectTaskAdapter;
import javafx.geometry.Rectangle2D;

import java.util.Objects;

/**
 * PostUpdateEvent class which determines if a WorldObject is colliding with another WorldObject.
 *
 * @author Caleb Brinkman
 */
public abstract class Collision extends WorldObjectTaskAdapter
{
	@Override
	public void onPostUpdate(World world, WorldObject worldObject) {
		world.getWorldObjects().
			  stream().
			  filter(collider -> checkForCollision(worldObject, collider)).
			  forEach(collider -> onCollision(world, worldObject, collider));
	}

	private boolean checkForCollision(WorldObject target, WorldObject collider) {
		boolean collision = false;
		if (checkRoughCollision(target, collider)) {
			collision = checkFinerCollision(target, collider);
		}
		return collision;
	}

	private static boolean checkRoughCollision(WorldObject target, WorldObject collider) {
		// TODO Make this more granular.
		return !Objects.equals(collider, target) && (collider.getZoneID() == target.getZoneID());
	}

	private static boolean checkFinerCollision(WorldObject target, WorldObject collider) {
		// TODO Probably shouldn't use JavaFX Rectangle; maybe a private inner class?
		Rectangle2D objectRect = getCollisionRect(target);
		Rectangle2D colliderRect = getCollisionRect(collider);
		return objectRect.intersects(colliderRect);
	}

	private static Rectangle2D getCollisionRect(WorldObject obj) {
		double objectRad = calculateCollisionRad(obj);
		Vector2D objectVector = obj.getGeometry2D().getPosition();
		double objectX = objectVector.getXValue();
		double objectY = objectVector.getYValue();
		double objectMinX = objectX - objectRad;
		double objectMinY = objectY - objectRad;
		return new Rectangle2D(objectMinX, objectMinY, objectRad, objectRad);
	}

	private static double calculateCollisionRad(WorldObject collider) {
		Vector2D size = collider.getGeometry2D().getSize();
		return (size.getXValue() * size.getYValue()) / 2;
	}

	/**
	 * This method should be overridden in subclasses to specify what even should occur when a collision is triggered.
	 *
	 * @param world The world in which the collision takes place
	 * @param target The object with which the collider collided.
	 * @param collided The object with wich the WorldObject watched by this Collision has collided.
	 *
	 */
	public abstract void onCollision(World world,
									 WorldObject target,
									 WorldObject collided);
}
