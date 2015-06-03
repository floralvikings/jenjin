package com.jenjinstudios.world.server.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.event.NewlyInvisibleEvent;
import com.jenjinstudios.world.event.NewlyVisibleEvent;
import com.jenjinstudios.world.event.WorldEventHandler;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.server.message.WorldServerMessageFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles adding an removing observers to and from objects as they become
 * visible and invisible.
 *
 * @author Caleb Brinkman
 */
public class StateObserverTracker
{
	private final Map<WorldObject, VisibleStateChangeObserver> observers;
	private final NewlyInvisibleEventHandler newlyInvisibleEventHandler;
	private final NewlyVisibleEventHandler newlyVisibleEventHandler;
	private final MessageContext context;

	/**
	 * Construct a new StateObserverTracker that will send messages over the
	 * given message context.
	 *
	 * @param context The message context.
	 */
	public StateObserverTracker(MessageContext context) {
		this.context = context;
		newlyInvisibleEventHandler = new NewlyInvisibleEventHandler();
		newlyVisibleEventHandler = new NewlyVisibleEventHandler();
		observers = new HashMap<>(10);
	}

	/**
	 * Return the event handler used to send newly invisible messages to
	 * clients.
	 *
	 * @return The event handler used to send newly invisible messages.
	 */
	public NewlyInvisibleEventHandler getNewlyInvisibleEventHandler() {
		return newlyInvisibleEventHandler;
	}

	/**
	 * Return the event handler used to send newly visible messages to clients.
	 *
	 * @return The event handler used to send newly visible messages.
	 */
	public NewlyVisibleEventHandler getNewlyVisibleEventHandler() {
		return newlyVisibleEventHandler;
	}

	/**
	 * Responsible for sending messages to the client when objects become
	 * invisible.
	 *
	 * @author Caleb Brinkman
	 */
	public class NewlyInvisibleEventHandler
		  extends WorldEventHandler<NewlyInvisibleEvent>
	{
		@Override
		public void handle(NewlyInvisibleEvent event) {
			event.getNewlyInvisible().forEach(o -> {
				Message msg = WorldServerMessageFactory.generateNewlyInvisibleMessage(o);
				context.enqueue(msg);
				o.removeObserver(observers.get(o));
				observers.remove(o);
			});
		}
	}

	/**
	 * Responsible for sending messages to the client when objects become
	 * visible.
	 *
	 * @author Caleb Brinkman
	 */
	public class NewlyVisibleEventHandler
		  extends WorldEventHandler<NewlyVisibleEvent>
	{
		@Override
		public void handle(NewlyVisibleEvent event) {
			event.getNewlyVisible().forEach(o -> {
				Message msg = WorldServerMessageFactory.generateNewlyVisibleMessage(o);
				context.enqueue(msg);
				VisibleStateChangeObserver observer = new VisibleStateChangeObserver(context);
				observer.registerEventHandler(new VisibleStateChangeHandler());
				o.addObserver(observer);
				observers.put(o, observer);
			});
		}
	}
}
