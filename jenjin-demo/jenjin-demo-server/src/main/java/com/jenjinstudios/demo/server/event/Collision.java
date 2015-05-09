package com.jenjinstudios.demo.server.event;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.event.PostUpdateEvent;
import com.jenjinstudios.world.math.Vector2D;
import javafx.geometry.Rectangle2D;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostUpdateEvent class which determines if a WorldObject is colliding with another WorldObject.
 *
 * @author Caleb Brinkman
 */
public abstract class Collision implements PostUpdateEvent
{
	/** The name of the property in the WorldObject that indicates size. */
	public static final String SIZE_PROPERTY = "ObjectSize";
	private static final double DEFAULT_OBJECT_SIZE = Location.SIZE;
	private static final Logger LOGGER = Logger.getLogger(Collision.class.getName());
	private final WorldObject worldObject;

	/**
	 * Consctruct a new Collision which will check for collisions with the specified WorldObject.
	 *
	 * @param worldObject The WorldObject which will be checked for collisions.
	 */
	public Collision(WorldObject worldObject) { this.worldObject = worldObject; }

	@Override
	public void onPostUpdate() {
		worldObject.getWorld().getWorldObjects().stream().filter(this::checkForCollision).forEach(this::onCollision);
	}

	private boolean checkForCollision(WorldObject collider) {
		boolean collision = false;
		if (checkRoughCollision(collider)) {
			collision = checkFinerCollision(collider);
		}
		return collision;
	}

	private boolean checkRoughCollision(WorldObject collider) {
		// TODO Make this more granular.
		return !Objects.equals(collider, worldObject) && (collider.getZoneID() == worldObject.getZoneID());
	}

	private boolean checkFinerCollision(WorldObject collider) {
		// TODO Probably shouldn't use JavaFX Rectangle; maybe a private inner class?
		Rectangle2D objectRect = getCollisionRect(worldObject);
		Rectangle2D colliderRect = getCollisionRect(collider);
		return objectRect.intersects(colliderRect);
	}

	private static Rectangle2D getCollisionRect(WorldObject obj) {
		double objectRad = calculateCollisionRad(obj);
		Vector2D objectVector = obj.getVector2D();
		double objectX = objectVector.getXCoordinate();
		double objectY = objectVector.getYCoordinate();
		double objectMinX = objectX - objectRad;
		double objectMinY = objectY - objectRad;
		return new Rectangle2D(objectMinX, objectMinY, objectRad, objectRad);
	}

	private static double calculateCollisionRad(WorldObject collider) {
		Object o = collider.getProperties().get(SIZE_PROPERTY);
		double rad;
		try {
			rad = ((o != null) ? (double) o : DEFAULT_OBJECT_SIZE) / 2;
		} catch (ClassCastException ex) {
			LOGGER.log(Level.WARNING, "Object size not instance of double: " + collider + ", " + o, ex);
			rad = DEFAULT_OBJECT_SIZE / 2;
		}
		return rad;
	}

	/**
	 * This method should be overridden in subclasses to specify what even should occur when a collision is triggered.
	 *
	 * @param collided The object with wich the WorldObject watched by this Collision has collided.
	 */
	public abstract void onCollision(WorldObject collided);
}
