/**
 * EventManager class.
 * Manages all the game events. 
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/05/04
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Event Manager class.
 * 
 * @author GuilhermeSilva
 */
public class EventManager {
	private final static String TAG = LogTagHelper.create(EventManager.class);
	private final static boolean logRegisterVerbose = false;
	private final static boolean logTriggerVerbose = false;
	private Map<EventType, Set<ResponseListener>> responseListenerMap;
	private Map<EventType, Set<RequestListener>> requestListenerMap;

	public EventManager() {
		responseListenerMap = new HashMap<EventType, Set<ResponseListener>>();
		requestListenerMap = new HashMap<EventType, Set<RequestListener>>();
	}

	/**
	 * Adds an event listener to the registry.
	 * 
	 * @param eventType
	 *            of the event that is going to be triggered, normally accessed
	 *            by using EVENTCLASS.getType()
	 * @param eventListener
	 *            for the event
	 * @return returns true, if the listener is added to the ArrayList and false
	 *         if the list already contains the listener
	 */
	public void addResponseListener(EventType eventType,
			ResponseListener eventListener) {
		addListener(eventType, eventListener, responseListenerMap);

	}

	public void addResponseListener(ResponseListener eventListener,
			Set<EventType> eventTypes) {
		for (EventType type : eventTypes) {
			addResponseListener(type, eventListener);
			
		}
	}

	public void addRequestListener(EventType eventType,
			RequestListener eventListener) {
		addListener(eventType, eventListener, requestListenerMap);
	}

	public void addRequestListener(RequestListener eventListener,
			Set<EventType> eventTypes) {
		for (EventType type : eventTypes) {
			addRequestListener(type, eventListener);
		}
	}

	public static <EL extends EventListener<? extends IEvent>, L extends ArrayList<EL>> void addListener(
			EventType eventType, EL eventListener, Map<EventType, Set<EL>> map) {
		if (logRegisterVerbose)
			Log.v(TAG, "addEventListener: type = " + eventType.name()
					+ " object = " + eventListener);
		Set<EL> listeners;
		synchronized (map) {
			listeners = map.get(eventType);
			if (listeners == null) {
				listeners = new CopyOnWriteArraySet<EL>();
				map.put(eventType, listeners);
			}else{
			}
		}

		listeners.add(eventListener);

	}

	public void removeRequestListener(EventType eventType,
			RequestListener eventListener) {
		removeListener(eventType, eventListener, requestListenerMap);
	}

	/**
	 * Removes the listener for the provided events.
	 * 
	 * @param listener
	 *            The listener to be unregistered from notification about
	 *            rovided events.
	 * @param eventTypes
	 *            A list of events the listener doesn't want to be informed
	 *            anymore.
	 */
	public void removeResponseListener(Object listener,
			Set<EventType> eventTypes) {
		synchronized (responseListenerMap) {
			for (EventType eventType : eventTypes) {
				Set<ResponseListener> listeners = responseListenerMap
						.get(eventType);
				if (listeners != null) {
					listeners.remove(listener);
				}
			}
		}
	}

	/**
	 * Removes the listener for all events. If you know which events the
	 * listener is registered to, use
	 * {@link #removeRequestListener(EventType, RequestListener)} instead!
	 * 
	 * @param listener
	 *            The listener to be unregistered from every event notification.
	 */
	public void removeResponseListener(Object listener) {
		removeResponseListener(listener, responseListenerMap.keySet());
	}

	private static <EL extends EventListener<?>> void removeListener(
			EventType eventType, Object eventListener,
			Map<EventType, ? extends Set<EL>> map) {
		if (logRegisterVerbose)
			Log.v(TAG, "removeEventListener: type = " + eventType.name()
					+ " object = " + eventListener);
		Set<EL> listeners;
		synchronized (map) {
			listeners = map.get(eventType);
		}
		if (listeners != null)
			listeners.remove(eventListener);
	}

	/**
	 * Triggers an event.
	 * 
	 * @param event
	 *            that is going to be triggered
	 * @return true if any listener processed the vent and false if otherwise
	 */
	public void triggerResponseEvent(ResponseEvent event) {
		Log.d(TAG, "trigger response for " + event.getType());
		triggerEvent(event, responseListenerMap);
	}

	public void triggerRequestEvent(RequestEvent event) {
		Log.d(TAG, "trigger request for " + event.getType());
		triggerEvent(event, requestListenerMap);
	}

	public void triggerRequestEvent(RequestEvent event,
			ResponseListener resultListener) {
		addResponseListener(event.getType(), resultListener);
		triggerRequestEvent(event);
	}

	private static <T extends IEvent, EL extends EventListener<T>> void triggerEvent(
			final T event, final Map<EventType, ? extends Set<EL>> map) {
		Set<? extends EventListener<T>> eventListeners;
		synchronized (map) {
			eventListeners = map.get(event.getType());
		}
		if (eventListeners == null) {
			Log.w(TAG,
					"Triggering events without listeners don't make sense.\nThere is no registered listener for event type "
							+ event.getType());
			return;
		}else{
		}
		for (EventListener<T> listener : eventListeners) {
			if (logTriggerVerbose)
				Log.d(TAG, "informing " + listener.getClass().getSimpleName());
			if (listener != null)
				listener.handleEvent(event);
			if (listener.removeAfterHandlingEvent()) {
				removeListener(event.getType(), listener, map);
			}
		}
	}

	/**
	 * Singleton of the EventManager class, Is private so that no component has
	 * a opportunity of corrupting it
	 */
	private static EventManager singleton = null;

	/**
	 * Gets EventManager's singleton
	 * 
	 * @return the eventManager singleton
	 */
	public synchronized static EventManager getSingleton() {
		if (singleton == null) {
			singleton = new EventManager();
		}
		return singleton;
	}
}