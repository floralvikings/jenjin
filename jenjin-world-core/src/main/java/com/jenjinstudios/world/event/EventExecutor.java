package com.jenjinstudios.world.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class EventExecutor
{
	private final Map<String, InitializeEvent> initializeEvents = new HashMap<>();
	private final Map<String, PreUpdateEvent> preUpdateEvents = new HashMap<>();
	private final Map<String, UpdateEvent> updateEvents = new HashMap<>();
	private final Map<String, PostUpdateEvent> postUpdateEvents = new HashMap<>();
	private final Map<String, EventStack> eventStacks = new HashMap<>();

	private boolean initialized;


	protected void initialize() {
		synchronized (initializeEvents)
		{
			for (String key : initializeEvents.keySet())
			{
				initializeEvents.get(key).onInitialize();
			}
		}
		initialized = true;
	}

	public void preUpdate() {
		if (!initialized)
		{
			initialize();
		}
		synchronized (preUpdateEvents)
		{
			for (String key : preUpdateEvents.keySet())
			{
				preUpdateEvents.get(key).onPreUpdate();
			}
		}
	}

	public void update() {
		synchronized (updateEvents)
		{
			for (String key : updateEvents.keySet())
			{
				updateEvents.get(key).onUpdate();
			}
		}
	}

	public void postUpdate() {
		synchronized (postUpdateEvents)
		{
			for (String key : postUpdateEvents.keySet())
			{
				postUpdateEvents.get(key).onPostUpdate();
			}
		}
	}

	public void addInitializeEvent(String name, InitializeEvent event) {
		synchronized (initializeEvents)
		{
			initializeEvents.put(name, event);
		}
	}

	public void addPreUpdateEvent(String name, PreUpdateEvent event) {
		synchronized (preUpdateEvents)
		{
			preUpdateEvents.put(name, event);
		}
	}

	public void addUpdateEvent(String name, UpdateEvent event) {
		synchronized (updateEvents)
		{
			updateEvents.put(name, event);
		}
	}

	public void addPostUpdateEvent(String name, PostUpdateEvent event) {
		synchronized (postUpdateEvents)
		{
			postUpdateEvents.put(name, event);
		}
	}

	public void addEventStack(String name, EventStack eventStack) {
		synchronized (eventStacks)
		{
			eventStacks.put(name, eventStack);
		}
	}

	public InitializeEvent getInitializeEvent(String name) {
		synchronized (initializeEvents)
		{
			return initializeEvents.get(name);
		}
	}

	public PreUpdateEvent getPreUpdateEvent(String name) {
		synchronized (preUpdateEvents)
		{
			return preUpdateEvents.get(name);
		}
	}

	public UpdateEvent getUpdateEvent(String name) {
		synchronized (updateEvents)
		{
			return updateEvents.get(name);
		}
	}

	public PostUpdateEvent getPostUpdateEvent(String name) {
		synchronized (postUpdateEvents)
		{
			return postUpdateEvents.get(name);
		}
	}

	public EventStack getEventStack(String name) {
		synchronized (eventStacks)
		{
			return eventStacks.get(name);
		}
	}
}
