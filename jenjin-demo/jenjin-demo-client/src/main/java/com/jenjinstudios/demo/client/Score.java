package com.jenjinstudios.demo.client;

/**
 * @author Caleb Brinkman
 */
public class Score
{
	private final int kills;
	private final int deaths;
	private final double ratio;
	private final String name;

	public Score(String name, int kills, int deaths) {
		this.kills = kills;
		this.deaths = deaths;
		this.ratio = kills / ((deaths > 0) ? deaths : 1);
		this.name = name;
	}

	public String toString() { return kills + " Kills / " + deaths + " Deaths = " + ratio + " Held By " + name; }
}
