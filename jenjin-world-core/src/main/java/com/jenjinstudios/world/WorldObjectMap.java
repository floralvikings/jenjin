package com.jenjinstudios.world;

import java.util.*;

/**
 * Used to store WorldObjects.
 * @author Caleb Brinkman
 */
public class WorldObjectMap extends TreeMap<Integer, WorldObject>
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

	public int getAvailableId() {
		int currentKey = 0;
		while (containsKey(currentKey))
		{
			currentKey++;
		}
		return currentKey;
	}
}
