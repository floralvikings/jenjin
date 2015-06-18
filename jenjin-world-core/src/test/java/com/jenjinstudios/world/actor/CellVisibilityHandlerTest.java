package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.object.WorldObject;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Test the CellVisibilityHandler class.
 *
 * @author Caleb Brinkman
 */
public class CellVisibilityHandlerTest
{
	/**
	 * Test the handle method.
	 */
	@Test
	public void testHandle() {
		Vision vision = mock(Vision.class);
		Actor actor = mock(Actor.class);
		CellChangeEvent cellChangeEvent = mock(CellChangeEvent.class);
		Actor visible = mock(Actor.class);
		SightCalculator sightCalculator = mock(SightCalculator.class);
		List<WorldObject> list = Collections.singletonList(visible);
		when(actor.getVision()).thenReturn(vision);
		when(cellChangeEvent.getActor()).thenReturn(actor);
		when(sightCalculator.getVisibleObjects(actor)).thenReturn(list);

		NodeEventHandler handler = new CellVisibilityHandler(sightCalculator);
		handler.handle(cellChangeEvent);

		verify(vision).addVisibleObject(visible);
	}
}
