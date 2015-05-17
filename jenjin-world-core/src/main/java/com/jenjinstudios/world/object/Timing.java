package com.jenjinstudios.world.object;

/**
 * Used to specify and retrieve the time at which the last update started and
 * ended for a given WorldObject.
 *
 * @author Caleb Brinkman
 */
public class Timing
{
	private long lastUpdateStartTime;
	private long lastUpdateEndTime;

	/**
	 * Set the time at which the last update started.
	 *
	 * @param lastUpdateStartTime The time (in ms since the epoch) at which the
	 * last update started.
	 */
	public void setLastUpdateStartTime(long lastUpdateStartTime) {
		this.lastUpdateStartTime = lastUpdateStartTime;
	}

	/**
	 * Get the time at which the last update started.
	 *
	 * @return The time (in ms since the epoch) at which the last update
	 * started.
	 */
	public long getLastUpdateStartTime() { return lastUpdateStartTime; }

	/**
	 * Set the time at which the last update ended.
	 *
	 * @param lastUpdateEndTime The time (in ms since the epoch) at which the
	 * last update ended.
	 */
	public void setLastUpdateEndTime(long lastUpdateEndTime) {
		this.lastUpdateEndTime = lastUpdateEndTime;
	}

	/**
	 * Get the time at which the last update ended.
	 *
	 * @return The time (in ms since the epoch) at which the last update ended.
	 */
	public long getLastUpdateEndTime() { return lastUpdateEndTime; }
}
