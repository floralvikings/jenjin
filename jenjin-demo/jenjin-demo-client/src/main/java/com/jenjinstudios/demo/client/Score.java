package com.jenjinstudios.demo.client;

/**
 * @author Caleb Brinkman
 */
public class Score
{
	public final int kills;
	public final int deaths;
	public final double ratio;
	public final String name;

	public Score(String name, int kills, int deaths) {
		this.kills = kills;
		this.deaths = deaths;
		this.ratio = kills / ((deaths > 0) ? deaths : 1);
		this.name = name;
	}

	public String toString() { return kills + " Kills / " + deaths + " Deaths = " + ratio + " Held By " + name; }
}
