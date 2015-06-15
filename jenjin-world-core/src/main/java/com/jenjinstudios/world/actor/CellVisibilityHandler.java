package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;
import com.jenjinstudios.world.math.BasicSightCalculator;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;

/**
 * Recalculates the cells visible to an object when it moves from one cell to another.
 *
 * @author Caleb Brinkman
 */
public class CellVisibilityHandler implements NodeEventHandler<CellChangeEvent>
{
	@Override
	public void handle(CellChangeEvent event) {
		Actor actor = event.getActor();
		Vision vision = actor.getVision();
		SightCalculator sightCalculator = new BasicSightCalculator(actor);
		Collection<WorldObject> visibles = sightCalculator.getVisibleObjects();
		visibles.forEach(vision::addVisibleObject);
	}
}
