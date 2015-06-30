package com.jenjinstudios.world.actor;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the NewlyInvisibleObserver class.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleObserverTest
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
			  thenReturn(Collections.singleton(visible)).
			  thenReturn(Collections.emptyList());

		NewlyInvisibleObserver observer = new NewlyInvisibleObserver();
		NewlyInvisibleEvent event01 = observer.observePostUpdate(actor);
		Assert.assertNull(event01, "No event should have been raised.");
		NewlyInvisibleEvent event02 = observer.observePostUpdate(actor);
		Assert.assertNotNull(event02, "Event should have been raised.");
		Assert.assertEquals(event02.getNewlyInvisible().size(), 1, "Should be one newly invisible object");
		Assert.assertTrue(event02.getNewlyInvisible().contains(visible), "Should contain newly invisible object");
	}
}
