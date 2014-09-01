package com.jenjinstudios.core;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class PingTracker
{
	/** The list of collected ping times. */
	private final List<Long> pingTimes;

	public PingTracker() {
		pingTimes = new LinkedList<>();
	}

	/**
	 * Add a ping time to the list.
	 * @param pingTime The time of the ping, in nanoseconds.
	 */
	public void addPingTime(long pingTime) { pingTimes.add(pingTime); }

	/**
	 * Get the average ping time, in nanoseconds.
	 * @return The average ping time between client and server, in nanoseconds.
	 */
	public long getAveragePingTime() {
		long total = 0;
		int num;
		synchronized (pingTimes)
		{
			num = pingTimes.size();
			for (long l : pingTimes) total += l;
		}
		return total / (num > 0 ? num : 1);
	}
}
