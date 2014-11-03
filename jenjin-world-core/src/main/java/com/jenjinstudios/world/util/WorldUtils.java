package com.jenjinstudios.world.util;

import com.jenjinstudios.world.World;

/**
 * @author Caleb Brinkman
 */
public class WorldUtils
{

	public static World createDefaultWorld() { return new World(ZoneUtils.createDefautZone()); }

}
