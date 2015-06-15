package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.Actor;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used for basic line-of-sight calculations.
 *
 * @author Caleb Brinkman
 */
public class BasicSightCalculator implements SightCalculator
{
	private final Actor actor;

	/**
	 * Construct a new SightCalculator that calculates sight within the given zone.
	 *
	 * @param actor The zone.
	 */
	public BasicSightCalculator(Actor actor) {
		this.actor = actor;
	}

	@Override
	public Collection<WorldObject> getVisibleObjects() {
		Collection<WorldObject> objects = new LinkedList<>();
		getVisibleCells().forEach(cell -> objects.addAll(cell.getChildren()));
		return objects;
	}

	@Override
	public Collection<Cell> getVisibleCells() {
		Zone zone = actor.getParent().getParent();
		Collection<Cell> visibles = new LinkedList<>();
		int range = (short) (actor.getVision().getRadius() / 2);
		short negativeRange = (short) -range;
		for (short x = negativeRange; x <= range; x++) {
			for (short y = negativeRange; y <= range; y++) {
				for (short z = negativeRange; z <= range; z++) {
					Cell cell = zone.getCell(x, y, z);
					if (cell != null) {
						visibles.add(cell);
					}
				}
			}
		}
		return visibles;
	}

}
