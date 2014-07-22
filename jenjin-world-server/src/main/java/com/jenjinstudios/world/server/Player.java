package com.jenjinstudios.world.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;

import java.util.TreeMap;

/**
 * The player class represents a player in the server-side world.
 * @author Caleb Brinkman
 */
public class Player extends Actor
{
	private final TreeMap<String, Object> properties;
	private Angle pendingAngle;

	public Player(String username) {
		super(username);
		setPendingAngle(getAngle());
		properties = new TreeMap<>();
	}

	public Angle getPendingAngle() { return pendingAngle; }

	public void setPendingAngle(Angle pendingAngle) { this.pendingAngle = pendingAngle; }

	public void setProperty(String property, Object value) { properties.put(property, value); }

	public Object getProperty(String property) { return properties.get(property); }

	@Override
	public void setUp() {
		super.setUp();
		setPendingAngle(getAngle());
	}
}
