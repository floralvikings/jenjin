package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.object.WorldObject;

/**
 * Represents a WorldObject which can move and see other objects.
 *
 * @author Caleb Brinkman
 */
public class Actor extends WorldObject
{
	private final Vision vision;
	private final Movement movement;

	/**
	 * Construct a new Actor with the given name.
	 *
	 * @param name The name of the actor.
	 */
	public Actor(String name) {
		super(name);
		movement = new Movement();
		vision = new Vision();
		addObserver(vision.getNewlyVisibleObserver());
		addObserver(vision.getNewlyInvisibleObserver());
		addTask(new MovementTask());
	}

	/**
	 * Get the Vision of this actor.
	 *
	 * @return The Vision of this actor.
	 */
	public Vision getVision() { return vision; }

	/**
	 * Get the movement object with details about how this actor is currently moving.
	 *
	 * @return The movement object with details about how this actor is currently moving.
	 */
	public Movement getMovement() { return movement; }
}
