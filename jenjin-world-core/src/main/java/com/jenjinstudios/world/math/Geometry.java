package com.jenjinstudios.world.math;


/**
 * Represents the spatial properties of a node, including
 * {@code orientation},
 * {@code position}, {@code size} and {@code speed}.
 *
 * @author Caleb Brinkman
 */
public class Geometry
{
	private Vector position;
	private Orientation orientation;
	private Vector size;

	/**
	 * Get the position represented in this Geometry.
	 *
	 * @return The position.
	 */
	public Vector getPosition() { return position; }

	/**
	 * Get the orientation represented by this geometry.
	 *
	 * @return The orientation represented by this geometry.
	 */
	public Orientation getOrientation() { return orientation; }

	/**
	 * Get the size represented in this Geometry.
	 *
	 * @return The size.
	 */
	public Vector getSize() { return size; }

	/**
	 * Set the position represented in this geometry.
	 *
	 * @param position The position.
	 */
	public void setPosition(Vector position) { this.position = position; }

	/**
	 * Set the orientation represented in this geometry.
	 *
	 * @param orientation The orientation.
	 */
	public void setOrientation(Orientation orientation) { this.orientation = orientation; }

	/**
	 * Set the size represented in this geometry.
	 *
	 * @param size The size.
	 */
	public void setSize(Vector size) { this.size = size; }

}
