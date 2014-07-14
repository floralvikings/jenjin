package com.jenjinstudios.world;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class WorldObjectMapTest
{
	@Test
	public void testGetAvailableId() {
		WorldObject worldObject = Mockito.mock(WorldObject.class);
		WorldObjectMap worldObjectMap = new WorldObjectMap();

		worldObjectMap.put(0, worldObject);
		worldObjectMap.put(1, worldObject);
		worldObjectMap.put(2, worldObject);
		worldObjectMap.put(3, worldObject);
		worldObjectMap.put(5, worldObject);
		worldObjectMap.put(7, worldObject);
		worldObjectMap.put(11, worldObject);
		worldObjectMap.remove(3);

		int id = worldObjectMap.getAvailableId();
		Assert.assertEquals(id, 3);
	}
}
