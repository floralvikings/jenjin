package com.jenjinstudios.world;

import java.util.HashSet;
import java.util.TreeMap;

/**
 * Used to store WorldObjects.
 * @author Caleb Brinkman
 */
public class WorldObjectMap extends TreeMap<Integer, WorldObject>
{
	private HashSet<Integer> reservedIds = new HashSet<>();

	@Override
	public WorldObject remove(Object key) {
		WorldObject r = super.remove(key);
		if (r != null)
		{
			r.setLocation(null);
		}
		return r;
	}

	public void reserveId(int i) { reservedIds.add(i); }

	public void put(int key, WorldObject value) {
		reservedIds.remove(key);
		super.put(key, value);
	}

	public int getAvailableId() {
		// FIXME This really could be a O(log(n)) method.  Someone should get on that.
		int currentKey = 0;
		while (containsKey(currentKey) || reservedIds.contains(currentKey))
		{
			currentKey++;
		}
		return currentKey;
	}


}
