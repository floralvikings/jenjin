package com.jenjinstudios.demo.server.event;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.event.PostUpdateEvent;
import com.jenjinstudios.world.math.Vector2D;
import javafx.geometry.Rectangle2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public abstract class Collision implements PostUpdateEvent
{
	public static final String SIZE_PROPERTY = "ObjectSize";
	public static final double DEFAULT_OBJECT_SIZE = Location.SIZE;
	private static final Logger LOGGER = Logger.getLogger(Collision.class.getName());
	private final WorldObject worldObject;

	public Collision(WorldObject worldObject) { this.worldObject = worldObject; }

	@Override
	public void onPostUpdate() {
		worldObject.getWorld().getWorldObjects().stream().filter(this::checkForCollision).forEach(this::onCollision);
	}

	private boolean checkForCollision(WorldObject collider) {
		boolean collision = false;
		if (checkRoughCollision(collider))
		{
			collision = checkFinerCollision(collider);
		}
		return collision;
	}

	private boolean checkRoughCollision(WorldObject collider) {
		// TODO Make this more granular.
		return collider != worldObject && collider.getZoneID() == worldObject.getZoneID();
	}

	private boolean checkFinerCollision(WorldObject collider) {
		// TODO Probably shouldn't use JavaFX Rectangle; maybe a private inner class?
		Rectangle2D objectRect = getCollisionRect(worldObject);
		Rectangle2D colliderRect = getCollisionRect(collider);
		return objectRect.intersects(colliderRect);
	}

	private Rectangle2D getCollisionRect(WorldObject obj) {
		double objectRad = calculateCollisionRad(obj);
		Vector2D objectVector = obj.getVector2D();
		double objectX = objectVector.getXCoordinate();
		double objectY = objectVector.getYCoordinate();
		double objectMinX = objectX - objectRad;
		double objectMinY = objectY - objectRad;
		return new Rectangle2D(objectMinX, objectMinY, objectRad, objectRad);
	}

	protected double calculateCollisionRad(WorldObject collider) {
		Object o = collider.getProperties().get(SIZE_PROPERTY);
		try
		{
			return (o != null ? (double) o : DEFAULT_OBJECT_SIZE) / 2;
		} catch (ClassCastException ex)
		{
			LOGGER.log(Level.WARNING, "Object size not instance of double: {0}, {1}", new Object[]{collider, o});
			return DEFAULT_OBJECT_SIZE / 2;
		}
	}

	public abstract void onCollision(WorldObject collided);
}
