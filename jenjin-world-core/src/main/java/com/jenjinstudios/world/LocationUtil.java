package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

/**
 * @author Caleb Brinkman
 */
public class LocationUtil
{
	public static Vector2D getNorthEastCorner(Location location) {
		return new Vector2D((location.X_COORDINATE + 1) * Location.SIZE - 1,
			(location.Y_COORDINATE + 1) * Location.SIZE - 1);
	}

	public static Vector2D getNorthWestCorner(Location location) {
		return new Vector2D(location.X_COORDINATE * Location.SIZE, (location.Y_COORDINATE + 1) * Location.SIZE - 1);
	}

	public static Vector2D getSouthEastCorner(Location location) {
		return new Vector2D((location.X_COORDINATE + 1) * Location.SIZE - 1, location.Y_COORDINATE * Location.SIZE);
	}

	public static Vector2D getSouthWestCorner(Location location) {
		return new Vector2D(location.X_COORDINATE *
			Location.SIZE, location.Y_COORDINATE * Location.SIZE);
	}
}
