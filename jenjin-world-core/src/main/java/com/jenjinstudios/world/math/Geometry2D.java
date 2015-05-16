package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;

/**
 * Represents the spatial properties of a 2-dimensional WorldObject, including {@code position}, {@code orientation},
 * {@code size} and {@code speed}.
 *
 * @author Caleb Brinkman
 */
public class Geometry2D implements Geometry
{
	private static final double DEFAULT_SIZE = Location.SIZE / 2.0;
	private Vector2D position = new Vector2D(Vector.ORIGIN);
	private Vector2D size = new Vector2D(DEFAULT_SIZE, DEFAULT_SIZE);
	private Angle orientation = new Angle(0.0);
	private double speed;

	@Override
	public Vector2D getPosition() { return position; }

	@Override
	public Angle getOrientation() { return orientation; }

	@Override
	public Vector2D getSize() { return size; }

	@Override
	public void setPosition(Vector position) { this.position = new Vector2D(position); }

	@Override
	public void setOrientation(Angle orientation) { this.orientation = orientation; }

	@Override
	public void setSize(Vector size) { this.size = new Vector2D(size); }

	@Override
	public double getSpeed() { return speed; }

	@Override
	public void setSpeed(Double speed) { this.speed = speed; }
}
