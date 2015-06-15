package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.event.NodeEventHandler;
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
	private final SightCalculator sightCalculator;

	/**
	 * Construct a new CellVisibilityHandler that will utilize the given SightCalculator to determine cell visibility.
	 *
	 * @param sightCalculator The sight calculator used to determine cell visibility.
	 */
	public CellVisibilityHandler(SightCalculator sightCalculator) { this.sightCalculator = sightCalculator; }

	@Override
	public void handle(CellChangeEvent event) {
		Actor actor = event.getActor();
		Vision vision = actor.getVision();
		Collection<WorldObject> visibles = sightCalculator.getVisibleObjects(actor);
		visibles.forEach(vision::addVisibleObject);
	}
}
