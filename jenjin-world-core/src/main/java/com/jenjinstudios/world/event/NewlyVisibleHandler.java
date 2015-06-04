package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.Vision;

/**
 * Handles a newly visible object by adding it to the list of visible objects in
 * the target.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleHandler
	  implements WorldEventHandler<NewlyVisibleEvent>
{
	@Override
	public void handle(NewlyVisibleEvent event) {
		Vision vision = event.getViewing().getVision();
		event.getNewlyVisible().forEach(vision::addVisibleObject);
	}
}
