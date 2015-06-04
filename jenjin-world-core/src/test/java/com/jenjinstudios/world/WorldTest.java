package com.jenjinstudios.world;

import com.jenjinstudios.world.task.NodeTask;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Test the world class.
 *
 * @author Caleb Brinkman
 */
public class WorldTest
{
	/**
	 * Test the preUpdate method.
	 */
	@Test
	public void testPreUpdate() {
		Zone zone = mock(Zone.class);
		NodeTask task = mock(NodeTask.class);
		when(zone.getTasks()).thenReturn(Collections.singleton(task));
		doAnswer(CALLS_REAL_METHODS).when(zone).preUpdate();

		World world = new World();
		world.addZone(zone);

		world.preUpdate();

		ArgumentCaptor<Zone> zoneArgumentCaptor = ArgumentCaptor.forClass(Zone.class);
		ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);

		verify(task).onPreUpdate(zoneArgumentCaptor.capture());
		verify(task, times(0)).onPreUpdate(nodeArgumentCaptor.capture());
	}

	/**
	 * Test the preUpdate method.
	 */
	@Test
	public void testUpdate() {
		Zone zone = mock(Zone.class);
		NodeTask task = mock(NodeTask.class);
		when(zone.getTasks()).thenReturn(Collections.singleton(task));
		doAnswer(CALLS_REAL_METHODS).when(zone).update();

		World world = new World();
		world.addZone(zone);

		world.update();

		ArgumentCaptor<Zone> zoneArgumentCaptor = ArgumentCaptor.forClass(Zone.class);
		ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);

		verify(task).onUpdate(zoneArgumentCaptor.capture());
		verify(task, times(0)).onUpdate(nodeArgumentCaptor.capture());
	}

	/**
	 * Test the preUpdate method.
	 */
	@Test
	public void testPostUpdate() {
		Zone zone = mock(Zone.class);
		NodeTask task = mock(NodeTask.class);
		when(zone.getTasks()).thenReturn(Collections.singleton(task));
		doAnswer(CALLS_REAL_METHODS).when(zone).postUpdate();

		World world = new World();
		world.addZone(zone);

		world.postUpdate();

		ArgumentCaptor<Zone> zoneArgumentCaptor = ArgumentCaptor.forClass(Zone.class);
		ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);

		verify(task).onPostUpdate(zoneArgumentCaptor.capture());
		verify(task, times(0)).onPostUpdate(nodeArgumentCaptor.capture());
	}
}
