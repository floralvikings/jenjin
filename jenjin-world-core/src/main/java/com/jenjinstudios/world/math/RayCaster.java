package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to cast rays through the cells of a Zone.
 *
 * @author Caleb Brinkman
 */
public class RayCaster
{
	private static final double EPSILON = 10.0e-6;
	private final Zone zone;

	/**
	 * Construct a new RayCaster that will cast rays in the given Zone.
	 *
	 * @param zone The Zone in which this RayCaster will cast rays.
	 */
	public RayCaster(Zone zone) { this.zone = zone; }

	/**
	 * Cast a ray from the specified origin cell, along the specified orientation, with the specified length, returning
	 * a Queue of cells starting with the origin and ending with the furthest cell reached.
	 * <p>
	 *
	 * @param origin The cell from which to cast the ray.
	 * @param direction The direction in which the ray should be cast.
	 * @param length The maximum length of the ray to cast.
	 *
	 * @return A Queue of cells along the ray, starting with {@code origin} and ending with the last cell reached.
	 */
	public Queue<Cell> castRay(Vector origin, Orientation direction, int length) {
		Queue<Cell> ray = new LinkedList<>();
		Cell originCell = zone.getCell(origin);
		ray.add(originCell);

		RayData rayData = new RayData(origin, direction);

		length /= Math.sqrt(rayData.dValues().xValSq() + rayData.dValues().yValSq() + rayData.dValues().zValSq());

		if (rayData.dValues().notAllZero()) {
			Collection<Cell> temp = castRay(length, rayData);
			ray.addAll(temp);
		}

		return ray;
	}

	private Collection<Cell> castRay(int length, RayData data) {
		Collection<Cell> temp = new LinkedList<>();
		boolean inRange = true;
		while (validStep(data.coords(), data.step()) && withinZone(data.coords()) && inRange) {
			inRange = (data.tMax().getX() < data.tMax().getY())
				  ? xzCast(length, data, temp)
				  : yzCast(length, data, temp);
		}
		return temp;
	}

	private boolean yzCast(int length, RayData data, Collection<Cell> temp) {
		boolean inRange;
		if (data.tMax().getY() < data.tMax().getZ()) {
			inRange = data.tMax().getY() <= length;
			if (inRange) {
				data.coords().addY(data.step().getY());
				data.tMax().addY(data.tDelda().getY());
				temp.add(zone.getCell(data.coords().shortX(), data.coords().shortY(), data.coords().shortZ()));
			}
		} else {
			inRange = data.tMax().getZ() <= length;
			if (inRange) {
				data.coords().addZ(data.step().getZ());
				data.tMax().addZ(data.tDelda().getZ());
				temp.add(zone.getCell(data.coords().shortX(), data.coords().shortY(), data.coords().shortZ()));
			}
		}
		return inRange;
	}

	private boolean xzCast(int length, RayData data, Collection<Cell> temp) {
		boolean inRange;
		if (data.tMax().getX() < data.tMax().getZ()) {
			inRange = data.tMax().getX() <= length;
			if (inRange) {
				data.coords().addX(data.step().getX());
				data.tMax().addX(data.tDelda().getX());
				temp.add(zone.getCell(data.coords().shortX(), data.coords().shortY(), data.coords().shortZ()));
			}
		} else {
			inRange = data.tMax().getZ() <= length;
			if (inRange) {
				data.coords().addZ(data.step().getZ());
				data.tMax().addZ(data.tDelda().getZ());
				temp.add(zone.getCell(data.coords().shortX(), data.coords().shortY(), data.coords().shortZ()));
			}
		}
		return inRange;
	}

	private boolean validStep(Triplet coords, Triplet step) {
		boolean xStep = (step.getX() > 0) ? (coords.getX() < zone.getDimensions().getWidth()) : (coords.getX() >= 0);
		boolean yStep = (step.getY() > 0) ? (coords.getY() < zone.getDimensions().getHeight()) : (coords.getY() >= 0);
		boolean zStep = (step.getZ() > 0) ? (coords.getZ() < zone.getDimensions().getDepth()) : (coords.getZ() >= 0);
		return xStep && yStep && zStep;
	}

	private boolean withinZone(Triplet coords) {
		boolean withinBounds = (coords.getX() >= 0) || (coords.getY() >= 0) || (coords.getZ() >= 0);
		withinBounds
			  |= (coords.getX() < zone.getDimensions().getWidth())
			  || (coords.getY() < zone.getDimensions().getHeight())
			  || (coords.getZ() < zone.getDimensions().getWidth());
		return withinBounds;
	}

	private static double intBound(double d, double ds) {
		if (ds < 0) {
			d = -d;
			ds = -ds;
		}
		d = modWithNegatives(d);
		return (1 - d) / ds;
	}

	private static double modWithNegatives(double value) { return ((value % 1) + 1) % 1; }

	private final class RayData
	{
		private final Triplet coords;
		private final Triplet step;
		private final Triplet tMax;
		private final Triplet tDelta;
		private final Triplet dValues;

		private RayData(Vector origin, Orientation direction) {
			Cell originCell = zone.getCell(origin);

			coords = new Triplet(
				  originCell.getPoint().getXCoordinate(),
				  originCell.getPoint().getYCoordinate(),
				  originCell.getPoint().getZCoordinate()
			);

			Vector directionalVector = direction.getDirectionalVector();
			dValues = new Triplet(
				  directionalVector.getXValue(),
				  directionalVector.getYValue(),
				  directionalVector.getZValue()
			);

			step = new Triplet(
				  Math.signum((Math.abs(dValues.getX()) > EPSILON) ? dValues.getX() : 0),
				  Math.signum((Math.abs(dValues.getY()) > EPSILON) ? dValues.getY() : 0),
				  Math.signum((Math.abs(dValues.getZ()) > EPSILON) ? dValues.getZ() : 0)
			);

			tMax = new Triplet(
				  intBound(origin.getXValue(), (Math.abs(dValues.getX()) > EPSILON) ? dValues.getX() : 0),
				  intBound(origin.getYValue(), (Math.abs(dValues.getY()) > EPSILON) ? dValues.getY() : 0),
				  intBound(origin.getZValue(), (Math.abs(dValues.getZ()) > EPSILON) ? dValues.getZ() : 0)
			);

			tDelta = new Triplet(
				  step.getX() / dValues.getX(),
				  step.getY() / dValues.getY(),
				  step.getZ() / dValues.getZ()
			);
		}

		public Triplet coords() { return coords; }

		public Triplet step() { return step; }

		public Triplet tMax() { return tMax; }

		public Triplet tDelda() { return tDelta; }

		public Triplet dValues() { return dValues; }
	}

	private static final class Triplet
	{
		private double xValue;
		private double yValue;
		private double zValue;


		private Triplet(double xValue, double yValue, double zValue) {
			this.xValue = xValue;
			this.yValue = yValue;
			this.zValue = zValue;
		}

		public double getX() { return xValue; }

		public void addX(double x) { xValue += x; }

		public double xValSq() { return xValue * xValue; }

		public double getY() { return yValue; }

		public void addY(double y) { yValue += y; }

		public double yValSq() { return yValue * yValue; }

		public double getZ() { return zValue; }

		public void addZ(double z) { zValue += z; }

		public double zValSq() { return zValue * zValue; }

		public boolean notAllZero() { return (xValue >= EPSILON) || (yValue >= EPSILON) || (zValue >= EPSILON); }

		public short shortX() { return (short) xValue; }

		public short shortY() { return (short) yValue; }

		public short shortZ() { return (short) zValue; }
	}
}
