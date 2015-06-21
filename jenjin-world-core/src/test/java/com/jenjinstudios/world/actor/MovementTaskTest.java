package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Geometry;
import com.jenjinstudios.world.math.Orientation;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.Timing;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Test the MovementTask class.
 *
 * @author Caleb Brinkman
 */
public class MovementTaskTest
{
	/** Test execution of the movement task within a cell. */
	@Test
	public void testMoveWithinCell()
	{
		Actor actor = mock(Actor.class);

		Movement movement = mock(Movement.class);
		Orientation movementOrientation = mock(Orientation.class);
		when(movement.getOrientation()).thenReturn(movementOrientation);
		when(movement.getSpeed()).thenReturn(10.0);
		when(actor.getMovement()).thenReturn(movement);

		Timing timing = mock(Timing.class);
		when(timing.getLastUpdateEndTime()).thenReturn(0L);
		when(timing.getLastUpdateStartTime()).thenReturn(100L);
		when(actor.getTiming()).thenReturn(timing);

		Geometry geometry = mock(Geometry.class);
		Orientation orientation = mock(Orientation.class);
		Vector vector = mock(Vector.class);
		when(vector.getVectorInDirection(anyDouble(), any(Orientation.class))).thenCallRealMethod();
		when(geometry.getOrientation()).thenReturn(orientation);
		when(geometry.getPosition()).thenReturn(vector);
		when(actor.getGeometry()).thenReturn(geometry);

		Cell cell = mock(Cell.class);
		Zone zone = mock(Zone.class);
		when(cell.getParent()).thenReturn(zone);
		when(actor.getParent()).thenReturn(cell);
		when(zone.getCell(any(Vector.class))).thenReturn(cell);

		MovementTask movementTask = new MovementTask();
		movementTask.onUpdate(actor);

		Vector expectedVector = new Vector(1.0, 0.0, 0.0);
		verify(geometry).setPosition(expectedVector);
	}

	/** Test the execution of the movement task when actor is idle. */
	@Test
	public void testMoveNowhere()
	{
		Actor actor = mock(Actor.class);

		Movement movement = mock(Movement.class);
		when(movement.getOrientation()).thenReturn(Orientation.NOWHERE);
		when(movement.getSpeed()).thenReturn(10.0);
		when(actor.getMovement()).thenReturn(movement);

		Timing timing = mock(Timing.class);
		when(timing.getLastUpdateEndTime()).thenReturn(0L);
		when(timing.getLastUpdateStartTime()).thenReturn(100L);
		when(actor.getTiming()).thenReturn(timing);

		Geometry geometry = mock(Geometry.class);
		Orientation orientation = mock(Orientation.class);
		Vector vector = mock(Vector.class);
		when(vector.getVectorInDirection(anyDouble(), any(Orientation.class))).thenCallRealMethod();
		when(geometry.getOrientation()).thenReturn(orientation);
		when(geometry.getPosition()).thenReturn(vector);
		when(actor.getGeometry()).thenReturn(geometry);

		Cell cell = mock(Cell.class);
		Zone zone = mock(Zone.class);
		when(cell.getParent()).thenReturn(zone);
		when(actor.getParent()).thenReturn(cell);
		when(zone.getCell(any(Vector.class))).thenReturn(cell);

		MovementTask movementTask = new MovementTask();
		movementTask.onUpdate(actor);

		verify(geometry, times(0)).setPosition(any(Vector.class));
	}

	/**
	 * Test movement between cells.
	 */
	@Test
	public void testMoveBetweenCells()
	{
		Actor actor = mock(Actor.class);

		Movement movement = mock(Movement.class);
		Orientation movementOrientation = mock(Orientation.class);
		when(movement.getOrientation()).thenReturn(movementOrientation);
		when(movement.getSpeed()).thenReturn(10.0);
		when(actor.getMovement()).thenReturn(movement);

		Timing timing = mock(Timing.class);
		when(timing.getLastUpdateEndTime()).thenReturn(0L);
		when(timing.getLastUpdateStartTime()).thenReturn(100L);
		when(actor.getTiming()).thenReturn(timing);

		Geometry geometry = mock(Geometry.class);
		Orientation orientation = mock(Orientation.class);
		Vector vector = mock(Vector.class);
		when(vector.getVectorInDirection(anyDouble(), any(Orientation.class))).thenCallRealMethod();
		when(geometry.getOrientation()).thenReturn(orientation);
		when(geometry.getPosition()).thenReturn(vector);
		when(actor.getGeometry()).thenReturn(geometry);

		Cell cellOne = mock(Cell.class);
		Cell cellTwo = mock(Cell.class);
		Zone zone = mock(Zone.class);
		when(cellOne.getParent()).thenReturn(zone);
		when(actor.getParent()).thenReturn(cellOne);
		when(zone.getCell(vector)).thenReturn(cellOne);
		when(zone.getCell(new Vector(1.0, 0.0, 0.0))).thenReturn(cellTwo);
		when(zone.areAdjacent(cellOne, cellTwo)).thenReturn(true);

		MovementTask movementTask = new MovementTask();
		movementTask.onUpdate(actor);

		verify(cellOne).removeChild(actor);
		verify(cellTwo).addChild(actor);
	}

	/**
	 * Test movement into non-adjacent (or null) cells.
	 */
	@Test
	public void testImpossibleMovement() {
		Actor actor = mock(Actor.class);

		Movement movement = mock(Movement.class);
		Orientation movementOrientation = mock(Orientation.class);
		when(movement.getOrientation()).thenReturn(movementOrientation);
		when(movement.getSpeed()).thenReturn(10.0);
		when(actor.getMovement()).thenReturn(movement);

		Timing timing = mock(Timing.class);
		when(timing.getLastUpdateEndTime()).thenReturn(0L);
		when(timing.getLastUpdateStartTime()).thenReturn(100L);
		when(actor.getTiming()).thenReturn(timing);

		Geometry geometry = mock(Geometry.class);
		Orientation orientation = mock(Orientation.class);
		Vector vector = mock(Vector.class);
		when(vector.getVectorInDirection(anyDouble(), any(Orientation.class))).thenCallRealMethod();
		when(geometry.getOrientation()).thenReturn(orientation);
		when(geometry.getPosition()).thenReturn(vector);
		when(actor.getGeometry()).thenReturn(geometry);

		Cell cellOne = mock(Cell.class);
		Cell cellTwo = mock(Cell.class);
		Zone zone = mock(Zone.class);
		when(cellOne.getParent()).thenReturn(zone);
		when(actor.getParent()).thenReturn(cellOne);
		when(zone.getCell(vector)).thenReturn(cellOne);
		when(zone.getCell(new Vector(1.0, 0.0, 0.0))).thenReturn(cellTwo);
		when(zone.areAdjacent(cellOne, cellTwo)).thenReturn(false);

		MovementTask movementTask = new MovementTask();
		movementTask.onUpdate(actor);

		verify(movement).setOrientation(Orientation.NOWHERE);
	}
}
