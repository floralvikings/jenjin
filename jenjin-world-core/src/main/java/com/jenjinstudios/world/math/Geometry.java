package com.jenjinstudios.world.math;

import com.jenjinstudios.world.object.WorldObject;

/**
 * Represents the spatial properties of a {@link WorldObject}, including
 * {@code orientation},
 * {@code position}, {@code size} and {@code speed}.
 *
 * @author Caleb Brinkman
 */
public interface Geometry
{
	/**
	 * Get the position represented in this Geometry.
	 *
	 * @return The position.
	 */
	Vector getPosition();

	/**
	 * Get the orientation represented by this geometry.
	 *
	 * @return The orientation represented by this geometry.
	 */
	Angle getOrientation();

	/**
	 * Get the size represented in this Geometry.
	 *
	 * @return The size.
	 */
	Vector getSize();

	/**
	 * Set the position represented in this geometry.
	 *
	 * @param position The position.
	 */
	void setPosition(Vector position);

	/**
	 * Set the orientation represented in this geometry.
	 *
	 * @param orientation The orientation.
	 */
	void setOrientation(Angle orientation);

	/**
	 * Set the size represented in this geometry.
	 *
	 * @param size The size.
	 */
	void setSize(Vector size);

	/**
	 * Get the movement speed of the object with this geometry, in units per second.
	 *
	 * @return The movement speed represented in this geometry, in units per second.
	 */
	double getSpeed();

	/**
	 * Set the movement speed represented in this geometry, in units per second.
	 *
	 * @param speed The speed represented in this geometry, in units per second.
	 */
	void setSpeed(Double speed);
}
