package com.jenjinstudios.world.actor;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the NewlyVisibleObserver class.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleObserverTest
{
	/**
	 * Test the observePostUpdate method.
	 */
	@Test
	public void testObservePostUpdate()
	{
		Actor actor = mock(Actor.class);
		Actor visible = mock(Actor.class);
		Vision vision = mock(Vision.class);
		when(actor.getVision()).thenReturn(vision);
		when(vision.getVisibleObjects()).
			  thenReturn(Collections.emptyList()).
			  thenReturn(Collections.singleton(visible));

		NewlyVisibleObserver observer = new NewlyVisibleObserver();
		NewlyVisibleEvent event01 = observer.observePostUpdate(actor);
		Assert.assertNull(event01, "No event should have been raised.");
		NewlyVisibleEvent event02 = observer.observePostUpdate(actor);
		Assert.assertNotNull(event02, "Event should have been raised.");
		Assert.assertEquals(event02.getNewlyVisible().size(), 1, "Should be one newly visible object");
		Assert.assertTrue(event02.getNewlyVisible().contains(visible), "Should contain newly visible object");
		Assert.assertEquals(event02.getViewing(), actor, "Actor should be viewing.");
		NewlyVisibleEvent event03 = observer.observePostUpdate(actor);
		Assert.assertNull(event03, "No event should have been raised.");
	}
}
