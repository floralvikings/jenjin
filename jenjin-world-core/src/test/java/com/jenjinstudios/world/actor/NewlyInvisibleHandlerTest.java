package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;
import com.jenjinstudios.world.object.WorldObject;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Test the NewlyInvisibleHandler class.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleHandlerTest
{
	/**
	 * Test the handle method.
	 */
	@Test
	public void testHandle() {
		NewlyInvisibleEvent event = mock(NewlyInvisibleEvent.class);
		Actor viewing = mock(Actor.class);
		Actor newlyInvisible = mock(Actor.class);
		Vision vision = mock(Vision.class);
		Collection<WorldObject> newlyInvisibles = Collections.singleton(newlyInvisible);

		when(viewing.getVision()).thenReturn(vision);
		when(event.getViewing()).thenReturn(viewing);
		when(event.getNewlyInvisible()).thenReturn(newlyInvisibles);

		NodeEventHandler handler = new NewlyInvisibleHandler();
		handler.handle(event);

		verify(vision).removeVisibleObject(newlyInvisible);
	}
}
