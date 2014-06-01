package com.jenjinstudios.world;

import java.util.TreeMap;

/**
 * Used to store WorldObjects.
 * @author Caleb Brinkman
 */
// TODO Change to use HashMap
public class WorldObjectTree extends TreeMap<Integer, WorldObject>
{
	@Override
	public WorldObject remove(Object key) {
		WorldObject r = super.remove(key);
		if (r != null)
		{
			r.setLocation(null);
		}
		return r;
	}

	@Override
	public void clear() {
		Object[] set = keySet().toArray();
		for (Object i : set)
		{
			remove(i);
		}
	}
}
