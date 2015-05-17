package com.jenjinstudios.world.client;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.object.Actor;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class WorldClientUpdaterTest
{
	@Test
	public void testRun() {
		WorldClient worldClient = mock(WorldClient.class);
		Actor clientPlayer = mock(Actor.class);
		World world = mock(World.class);
		WorldObjectList worldObjectMap = new WorldObjectList(world);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);

		when(worldClient.getWorld()).thenReturn(world);
		when(worldClient.getPlayer()).thenReturn(clientPlayer);

		WorldClientUpdater worldClientUpdater = new WorldClientUpdater(worldClient);
		int rand = (int) (Math.random() * 100);
		for (int i = 0; i < rand; i++)
		{
			worldClientUpdater.run();
		}

		verify(world, times(rand)).update();
	}
}
