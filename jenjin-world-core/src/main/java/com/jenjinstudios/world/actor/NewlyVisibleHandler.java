package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;

/**
 * Handles a newly visible object by adding it to the list of visible objects in
 * the target.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleHandler implements NodeEventHandler<NewlyVisibleEvent>
{
	@Override
	public void handle(NewlyVisibleEvent event) {
		Vision vision = event.getViewing().getVision();
		event.getNewlyVisible().forEach(vision::addVisibleObject);
	}
}
