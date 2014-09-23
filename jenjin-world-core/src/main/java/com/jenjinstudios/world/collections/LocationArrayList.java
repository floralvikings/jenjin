package com.jenjinstudios.world.collections;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.util.LocationUtils;

import java.util.ArrayList;

/**
 * An ArrayList designed to hold locations with unique coordinates.  Adding a Location to this collection with
 * coordinates equal to those of a Location that already exists in the collection replaces the existing Location.
 * @author Caleb Brinkman
 */
public class LocationArrayList extends ArrayList<Location>
{
	@Override
	public boolean add(Location location) {
		int existingCoordinateIndex = -1;
		boolean r;
		for (int i = 0; i < size(); i++)
		{
			if (LocationUtils.coordinatesEqual(location, get(i)))
			{
				existingCoordinateIndex = i;
			}
		}
		if (existingCoordinateIndex > -1)
		{
			set(existingCoordinateIndex, location);
			r = true;
		} else
		{
			r = super.add(location);
		}
		return r;
	}
}
