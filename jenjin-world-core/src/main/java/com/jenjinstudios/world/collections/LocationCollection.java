package com.jenjinstudios.world.collections;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.util.LocationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An ArrayList designed to hold locations with unique coordinates.  Adding a Location to this collection with
 * coordinates equal to those of a Location that already exists in the collection replaces the existing Location.
 * @author Caleb Brinkman
 */
public class LocationCollection extends ArrayList<Location>
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
			super.set(existingCoordinateIndex, location);
			r = true;
		} else
		{
			r = super.add(location);
		}
		return r;
	}

	@Override
	public boolean addAll(Collection<? extends Location> c) {
		Location[] locs = new Location[c.size()];
		c.toArray(locs);

		for (Location l : locs)
		{
			add(l);
		}

		return locs.length != 0;
	}

	public void addAll(Location[] toAdd) {
		for (Location location : toAdd)
		{
			add(location);
		}
	}

	public Location getLocationWithXY(int x, int y) {
		Location loc = null;
		Optional<Location> f = stream().filter(l -> l.getX() == x && l.getY() == y).findFirst();
		if (f.isPresent())
		{
			loc = f.get();
		}
		return loc;
	}

	public boolean containsLocationWithXY(int x, int y) {
		return stream().filter(l -> l.getX() == x && l.getY() == y).findFirst().isPresent();
	}

	@Override
	public void add(int index, Location location) {
		throw new UnsupportedOperationException("Adding locations at specific indices is not supported.");
	}

	@Override
	public boolean remove(Object location) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}

	@Override
	public void clear() { super.clear(); }

	@Override
	public boolean addAll(int index, Collection<? extends Location> c) {
		throw new UnsupportedOperationException("Adding locations at specific indices is not supported.");
	}

	@Override
	public Location set(int index, Location element) {
		throw new UnsupportedOperationException("To replace a Location, use \"LocationCollection.add(Location l)\"" +
			  "where the x and y coordinates of \"l\" are equal to those of the location you wish to replace.");
	}

	@Override
	public Location remove(int index) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}

	@Override
	public boolean removeIf(Predicate<? super Location> filter) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Locations cannot be removed from a LocationCollection");
	}
}
