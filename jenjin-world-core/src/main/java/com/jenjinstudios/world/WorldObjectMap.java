package com.jenjinstudios.world;

import java.util.HashMap;

/**
 * Used to store WorldObjects.
 * @author Caleb Brinkman
 */
public class WorldObjectMap extends HashMap<Integer, WorldObject>
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
}
