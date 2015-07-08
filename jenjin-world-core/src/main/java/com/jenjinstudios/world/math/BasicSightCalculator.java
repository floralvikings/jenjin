package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.Actor;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Used for basic line-of-sight calculations.
 *
 * @author Caleb Brinkman
 */
public class BasicSightCalculator implements SightCalculator
{

	@Override
	public Collection<WorldObject> getVisibleObjects(Actor actor) {
		Collection<WorldObject> objects = new LinkedList<>();
		getVisibleCells(actor).forEach(cell -> objects.addAll(cell.getChildren()));
		return objects;
	}

	@Override
	public Collection<Cell> getVisibleCells(Actor actor) {
		Cell originCell = actor.getParent();
		short oX = originCell.getPoint().getXCoordinate();
		short oY = originCell.getPoint().getYCoordinate();
		short oZ = originCell.getPoint().getZCoordinate();
		Zone zone = originCell.getParent();
		short radius = (short) (actor.getVision().getRadius() - 1);
		Collection<Cell> visibles = new HashSet<>((int)Math.pow((radius - 1) * 2, 3));
		for (short dX = (short) -radius; dX <= radius; dX++) {
			for (short dY = (short) -radius; dY <= radius; dY++) {
				for (short dZ = (short) -radius; dZ <= radius; dZ++) {
					Cell cell = zone.getCell(oX + dX, oY + dY, oZ + dZ);
					if (cell != null) {
						visibles.add(cell);
					}
				}
			}
		}
		return visibles;
	}

}
