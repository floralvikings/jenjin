package com.jenjinstudios.demo.server.event;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector;
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
	public void onPostUpdate(Node node) {
		if (node instanceof WorldObject) {
			WorldObject object = (WorldObject) node;
			Zone zone = object.getParent().getParent();
			zone.getWorldObjects().stream().
				  filter(collider -> checkForCollision(object, collider)).
				  forEach(collider -> onCollision(object, collider));
		}
	}

	private static boolean checkForCollision(WorldObject object, WorldObject collider) {
		boolean collision = false;
		if (checkRoughCollision(object, collider)) {
			collision = checkFinerCollision(object, collider);
		}
		return collision;
	}

	private static boolean checkFinerCollision(WorldObject object, WorldObject collider) {
		// TODO Probably shouldn't use JavaFX Rectangle; maybe a private inner class?
		Rectangle2D objectRect = getCollisionRect(object);
		Rectangle2D colliderRect = getCollisionRect(collider);
		return objectRect.intersects(colliderRect);
	}

	private static Rectangle2D getCollisionRect(WorldObject object) {
		double objectRad = calculateCollisionRad(object);
		Vector objectVector = object.getGeometry().getPosition();
		double objectX = objectVector.getXValue();
		double objectY = objectVector.getYValue();
		double objectMinX = objectX - objectRad;
		double objectMinY = objectY - objectRad;
		return new Rectangle2D(objectMinX, objectMinY, objectRad, objectRad);
	}

	private static double calculateCollisionRad(WorldObject object) {
		Vector size = object.getGeometry().getSize();
		return (size.getXValue() * size.getYValue()) / 2;
	}

	private static boolean checkRoughCollision(WorldObject object, WorldObject collider) {
		return !Objects.equals(collider, object) && collider.getParent().isAdjacentTo(object.getParent());
	}

	/**
	 * This method should be overridden in subclasses to specify what even should occur when a collision is triggered.
	 *
	 * @param object The object with which the collider collided.
	 * @param collider The object with wich the WorldObject watched by this Collision has collided.
	 */
	public abstract void onCollision(WorldObject object, WorldObject collider);


}
