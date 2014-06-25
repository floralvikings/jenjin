package com.jenjinstudios.server.net;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.Timer;

/**
 * @author Caleb Brinkman
 */
public class ServerUpdateTaskTest
{
	@Test
	public void testGetAverageUPS() throws Exception {
		AuthServer authServer = Mockito.mock(AuthServer.class);
		Mockito.when(authServer.getUps()).thenReturn(10);
		Mockito.when(authServer.getSyncedTasks()).thenReturn(new LinkedList());
		Mockito.when(authServer.getRepeatedTasks()).thenReturn(new LinkedList());
		ServerUpdateTask serverUpdateTask = new ServerUpdateTask(authServer);
		Timer loopTimer = new Timer("Foo", false);
		loopTimer.scheduleAtFixedRate(serverUpdateTask, 0, 100);
		Thread.sleep(1500);
		Assert.assertEquals(serverUpdateTask.getAverageUPS(), 10, 0.1);
	}
}
