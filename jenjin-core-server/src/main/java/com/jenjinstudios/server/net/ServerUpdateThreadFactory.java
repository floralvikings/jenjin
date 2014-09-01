package com.jenjinstudios.server.net;

import java.util.concurrent.ThreadFactory;

/**
 * @author Caleb Brinkman
 */
public class ServerUpdateThreadFactory implements ThreadFactory
{
	/**
	 * Constructs a new {@code Thread}.  Implementations may also initialize priority, name, daemon status, {@code
	 * ThreadGroup}, etc.
	 * @param r a runnable to be executed by new thread instance
	 * @return constructed thread, or {@code null} if the request to create a thread is rejected
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName("Server Update Thread");
		return thread;
	}
}
