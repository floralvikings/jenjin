package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;
import com.jenjinstudios.world.object.WorldObject;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Test the NewlyVisibleHandler class.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleHandlerTest
{
	/**
	 * Test the handle method.
	 */
	@Test
	public void testHandle() {
		NewlyVisibleEvent event = mock(NewlyVisibleEvent.class);
		Actor viewing = mock(Actor.class);
		Actor newlyVisible = mock(Actor.class);
		Vision vision = mock(Vision.class);
		Collection<WorldObject> newlyVisibles = Collections.singleton(newlyVisible);

		when(viewing.getVision()).thenReturn(vision);
		when(event.getViewing()).thenReturn(viewing);
		when(event.getNewlyVisible()).thenReturn(newlyVisibles);

		NodeEventHandler handler = new NewlyVisibleHandler();
		handler.handle(event);

		verify(vision).addVisibleObject(newlyVisible);
	}
}
