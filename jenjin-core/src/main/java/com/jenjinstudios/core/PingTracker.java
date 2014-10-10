package com.jenjinstudios.core;

import java.util.LinkedList;
import java.util.List;

public class PingTracker
{
	private final List<Long> pingTimes;

	public PingTracker() {
		pingTimes = new LinkedList<>();
	}

	public void addPingTime(long pingTime) { pingTimes.add(pingTime); }

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
