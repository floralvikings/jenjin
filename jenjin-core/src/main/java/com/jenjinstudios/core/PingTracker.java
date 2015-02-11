package com.jenjinstudios.core;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used to track the average latency between two connections.
 *
 * @author Caleb Brinkman
 */
public class PingTracker
{
    private final Collection<Long> pingTimes = new LinkedList<>();

	/**
	 * Add a "ping" time to the list maintained by this object.  This should be the amount of time, in milliseconds, a
	 * ping took to travel from one connection to another and back.
	 *
	 * @param pingTime The time taken for a ping to travel from one connection to another and back.
	 */
	public void addPingTime(long pingTime) { pingTimes.add(pingTime); }

	/**
	 * Get the average of all ping times added to this tracker.
	 *
	 * @return The average of all ping times added to this tracer.
	 */
	public long getAveragePingTime() {
		long total = 0;
		int num;
		synchronized (pingTimes)
		{
			num = pingTimes.size();
			for (long l : pingTimes) total += l;
		}
        return total / ((num > 0) ? num : 1);
    }
}
