package com.jenjinstudios.server.net;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Caleb Brinkman
 */
public class ServerUpdateTaskTest
{
	@Test
	public void testGetAverageUPS() throws Exception {
		AuthServer authServer = Mockito.mock(AuthServer.class);
		Mockito.when(authServer.getUps()).thenReturn(5);
		Mockito.when(authServer.getSyncedTasks()).thenReturn(new LinkedList());
		Mockito.when(authServer.getRepeatedTasks()).thenReturn(new LinkedList());
		ServerUpdateTask serverUpdateTask = new ServerUpdateTask(authServer);
		ScheduledExecutorService loopTimer = Executors.newSingleThreadScheduledExecutor();
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, 200, TimeUnit.MILLISECONDS);
		Thread.sleep(2000);
		loopTimer.shutdownNow();
		Assert.assertEquals(serverUpdateTask.getAverageUPS(), 5, 1);
	}
}
