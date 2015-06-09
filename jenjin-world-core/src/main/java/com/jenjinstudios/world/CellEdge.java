package com.jenjinstudios.world;

import static com.jenjinstudios.world.CellEdge.Direction.*;

/**
 * Represents an edge between two cells.
 *
 * @author Caleb Brinkman
 */
public class CellEdge
{
	private final Cell origin;
	private final Cell destination;
	private final Direction direction;
	private double visionCost;

	/**
	 * Construct a LocationEdge from the origin location to the destination location.
	 *
	 * @param origin The origin location.
	 * @param destination The destination location.
	 */
	public CellEdge(Cell origin, Cell destination) {
		this.origin = origin;
		this.destination = destination;
		this.direction = calculateDirection(origin, destination);
		visionCost = 1;
	}

	private static Direction calculateDirection(Cell origin, Cell destination) {
		int xDiff = (int) Math.signum(origin.getPoint().getXCoordinate() - destination.getPoint().getXCoordinate());
		int yDiff = (int) Math.signum(origin.getPoint().getYCoordinate() - destination.getPoint().getYCoordinate());
		int zDiff = (int) Math.signum(origin.getPoint().getZCoordinate() - destination.getPoint().getZCoordinate());

		Direction direction;
		if (xDiff < 0) {
			direction = calcDirectionLesserX(yDiff, zDiff);
		} else {
			direction = (xDiff == 0) ? calcDirectionEqualX(yDiff, zDiff) : calcDirectionGreaterX(yDiff, zDiff);
		}
		return direction;
	}

	private static Direction calcDirectionLesserX(int yDiff, int zDiff) {
		Direction direction;
		if (yDiff < 0) {
			direction = calcDirectionLesserXLesserY(zDiff);
		} else {
			direction = (yDiff == 0) ? calcDirectionLesserXEqualY(zDiff) : calcDirectionLesserXGreaterY(zDiff);
		}
		return direction;
	}

	private static Direction calcDirectionLesserXLesserY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = UP_NORTHEAST;
		} else {
			direction = (zDiff == 0) ? UP_EAST : UP_SOUTHEAST;
		}
		return direction;
	}

	private static Direction calcDirectionLesserXEqualY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = NORTHEAST;
		} else {
			direction = (zDiff == 0) ? EAST : SOUTHEAST;
		}
		return direction;
	}

	private static Direction calcDirectionLesserXGreaterY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = DOWN_NORTHEAST;
		} else {
			direction = (zDiff == 0) ? DOWN_EAST : DOWN_SOUTHEAST;
		}
		return direction;
	}

	private static Direction calcDirectionEqualX(int yDiff, int zDiff) {
		Direction direction;
		if (yDiff < 0) {
			direction = calcDirectionEqualXLesserY(zDiff);
		} else {
			direction = (yDiff == 0) ? calcDirectionEqualXEqualY(zDiff) : calcDirectionEqualXGreaterY(zDiff);
		}
		return direction;
	}

	private static Direction calcDirectionEqualXLesserY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = UP_NORTH;
		} else {
			direction = (zDiff == 0) ? UP : UP_SOUTH;
		}
		return direction;
	}

	private static Direction calcDirectionEqualXEqualY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = NORTH;
		} else {
			direction = (zDiff == 0) ? SELF : SOUTH;
		}
		return direction;
	}

	private static Direction calcDirectionEqualXGreaterY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = DOWN_NORTH;
		} else {
			direction = (zDiff == 0) ? DOWN : DOWN_SOUTH;
		}
		return direction;
	}

	private static Direction calcDirectionGreaterX(int yDiff, int zDiff) {
		Direction direction;
		if (yDiff < 0) {
			direction = calcDirectionGreaterXLesserY(zDiff);
		} else {
			direction = (yDiff == 0) ? calcDirectionGreaterXEqualY(zDiff) : calcDirectionGreaterXGreaterY(zDiff);
		}
		return direction;
	}

	private static Direction calcDirectionGreaterXLesserY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = UP_NORTHWEST;
		} else {
			direction = (zDiff == 0) ? UP_WEST : UP_SOUTHWEST;
		}
		return direction;
	}

	private static Direction calcDirectionGreaterXEqualY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = NORTHWEST;
		} else {
			direction = (zDiff == 0) ? WEST : SOUTHWEST;
		}
		return direction;
	}

	private static Direction calcDirectionGreaterXGreaterY(int zDiff) {
		Direction direction;
		if (zDiff < 0) {
			direction = DOWN_NORTHWEST;
		} else {
			direction = (zDiff == 0) ? DOWN_WEST : DOWN_SOUTHWEST;
		}
		return direction;
	}

	/**
	 * Get the origin location of this edge.
	 *
	 * @return The origin location of this edge.
	 */
	public Cell getOrigin() { return origin; }

	/**
	 * Get the destination location of this edge.
	 *
	 * @return The destination location of this edge.
	 */
	public Cell getDestination() { return destination; }

	/**
	 * Get the direction from the origin location to the destination location.
	 *
	 * @return The direction from the origin location to the destination location.
	 */
	public Direction getDirection() { return direction; }

	/**
	 * Get the "vision cost" of this cell edge; this cost is not an absolute, but a multiplier.  Therefore the default
	 * value is 1.0
	 *
	 * @return The vision cost of this cell edge.
	 */
	public double getVisionCost() {
		return visionCost;
	}

	/**
	 * Set the "vision cost" of this cell edge.
	 *
	 * @param visionCost The new vision cost multiplier.
	 */
	public void setVisionCost(double visionCost) {
		this.visionCost = visionCost;
	}

	@Override
	public int hashCode() {
		int result = origin.hashCode();
		result = (31 * result) + destination.hashCode();
		result = (31 * result) + direction.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		CellEdge other = (CellEdge) obj;

		if (!origin.equals(other.getOrigin())) return false;
		if (!destination.equals(other.getDestination())) return false;
		return direction == other.getDirection();

	}

	/**
	 * Speficies the direction between a source and target location.
	 */
	public enum Direction
	{
		/** Represents an edge that points to itself. */
		SELF(false),
		/** Represents an edge that points north. */
		NORTH(true),
		/** Represents an edge that points south. */
		SOUTH(true),
		/** Represents an edge that points east. */
		EAST(true),
		/** Represents an edge that points west. */
		WEST(true),
		/** Represents an edge that points northeast. */
		NORTHEAST(false),
		/** Represents an edge that points northwest. */
		NORTHWEST(false),
		/** Represents an edge that points southeast. */
		SOUTHEAST(false),
		/** Represents an edge that points southwest. */
		SOUTHWEST(false),
		/** Represents an edge that points straight up. */
		UP(true),
		/** Represents an edge that points up and to the north. */
		UP_NORTH(false),
		/** Represents an edge that points up and to the south. */
		UP_SOUTH(false),
		/** Represents an edge that points up and to the east. */
		UP_EAST(false),
		/** Represents an edge that points up and to the west. */
		UP_WEST(false),
		/** Represents an edge that points up and to the northeast. */
		UP_NORTHEAST(false),
		/** Represents an edge that points up and to the northwest. */
		UP_NORTHWEST(false),
		/** Represents an edge that points up and to the southeast. */
		UP_SOUTHEAST(false),
		/** Represents an edge that points up and to the southwest. */
		UP_SOUTHWEST(false),
		/** Represents an edge that points straight down. */
		DOWN(true),
		/** Represents an edge that points down and to the north. */
		DOWN_NORTH(false),
		/** Represents an edge that points down and to the south. */
		DOWN_SOUTH(false),
		/** Represents an edge that points down and to the east. */
		DOWN_EAST(false),
		/** Represents an edge that points down and to the west. */
		DOWN_WEST(false),
		/** Represents an edge that points down and to the northeast. */
		DOWN_NORTHEAST(false),
		/** Represents an edge that points down and to the northwest. */
		DOWN_NORTHWEST(false),
		/** Represents an edge that points down and to the southeast. */
		DOWN_SOUTHEAST(false),
		/** Represents an edge that points down and to the southwest. */
		DOWN_SOUTHWEST(false);

		private final boolean isCardinal;

		Direction(boolean isCardinal) { this.isCardinal = isCardinal; }

		/**
		 * Returns whether this direction is a "cardinal" direction (up, down, north, south, east, west).
		 *
		 * @return Whether this direction is a cardinal direction.
		 */
		public boolean isCardinal() { return isCardinal; }
	}
}
