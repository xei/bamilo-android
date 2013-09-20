/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service;

import java.util.Set;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestListener;
import android.content.Context;

/**
 * Abstract class that defines the Service behavior.
 * Services listen to request events, get the data from the server and trigger the response events.
 * 
 * @author GuilhermeSilva
 * 
 */
public abstract class DarwinService extends RequestListener {

	//protected static final String JSON_DATA_TAG = "data";
	private Set<EventType> initializationEvents;
	private Set<EventType> handledEvents;

	/**
	 * 
	 * @param initializationEvents
	 *            EventTypes that are needed for initialization. Theses are events
	 *            that are that essential to this Service that it cannot work
	 *            probably without a valid response to that event.
	 * @param handledEvents
	 */
	protected DarwinService(Set<EventType> initializationEvents,
			Set<EventType> handledEvents) {
		super(false);
		this.initializationEvents = initializationEvents;
		this.handledEvents = handledEvents;
		handledEvents.addAll(initializationEvents);
	}
	
	public final void init(Context context) {
		EventManager.getSingleton().addRequestListener(this, handledEvents);
		onInit(context.getApplicationContext());
	}
	
	/**
	 * Initializes the darwin service witht he applicaotion context of the app
	 * @param context
	 */
	protected void onInit(Context context) {}

	/**
	 * Returns the events that are needed/used for initialization of the
	 * service.
	 * 
	 * @return The needed/used initialization events.
	 */
	public Set<EventType> getEventsForInitialization() {
		return initializationEvents;
	}

}
