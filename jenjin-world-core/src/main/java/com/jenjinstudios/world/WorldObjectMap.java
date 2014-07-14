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
		// FIXME This really could be a O(log(n)) method.  Someone should get on that.
		int currentKey = 0;
		while (containsKey(currentKey))
		{
			currentKey++;
		}
		return currentKey;
	}
}
