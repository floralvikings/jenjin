package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.Vision;

/**
 * Handles a newly invisible object by adding removing it from the list of
 * visible objects in the viewing object.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleHandler
	  implements WorldEventHandler<NewlyInvisibleEvent>
{
	@Override
	public void handle(NewlyInvisibleEvent event) {
		Vision vision = event.getViewing().getVision();
		event.getNewlyInvisible().forEach(vision::removeVisibleObject);
	}
}
