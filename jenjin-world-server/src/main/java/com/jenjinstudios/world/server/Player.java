package com.jenjinstudios.world.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;

/**
 * The player class represents a player in the server-side world.
 * @author Caleb Brinkman
 */
public class Player extends Actor
{
	private Angle pendingAngle;

	public Player(String username) {
		super(username);
		setPendingAngle(getAngle());
	}

	public Angle getPendingAngle() { return pendingAngle; }

	public void setPendingAngle(Angle pendingAngle) { this.pendingAngle = pendingAngle; }

	@Override
	public void setUp() {
		super.setUp();
		setPendingAngle(getAngle());
	}
}
