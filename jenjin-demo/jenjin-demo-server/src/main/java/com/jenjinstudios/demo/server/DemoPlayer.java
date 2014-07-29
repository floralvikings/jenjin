package com.jenjinstudios.demo.server;

import com.jenjinstudios.world.server.Player;

/**
 * @author Caleb Brinkman
 */
public class DemoPlayer extends Player
{
	private int kills = 0;
	private int deaths = 0;

	public DemoPlayer() { super("DEMO_PLAYER"); }

	public int getKills() { return kills; }

	public int getDeaths() { return deaths; }

	public void incrementKillCounter() { kills++; }

	public void incrementDeathCounter() { deaths++; }

	public double getKillDeathRatio() { return kills / ((deaths > 0) ? deaths : 1); }
}
