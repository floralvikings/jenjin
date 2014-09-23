package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;

/**
 * @author Caleb Brinkman
 */
public class LocationCalculator
{
	public static Location getObjectLocation(WorldObject worldObject) {
		Location loc = null;
		World world = worldObject.getWorld();
		if (world != null)
		{
			Zone zone = world.getZone(worldObject.getZoneID());
			if (zone != null)
			{
				loc = zone.getLocationForCoordinates(worldObject.getVector2D());
			}
		}
		return loc;
	}
}
