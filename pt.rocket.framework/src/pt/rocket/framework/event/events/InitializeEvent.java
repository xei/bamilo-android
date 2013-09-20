/**
 * 
 */
package pt.rocket.framework.event.events;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to initiate the services
 * @author nutzer2
 *
 */
public class InitializeEvent extends MetaRequestEvent<EnumSet<EventType>> {

	/**
	 * @param type
	 * @param value
	 */
	public InitializeEvent(EnumSet<EventType> initEvents) {
		super(EventType.INITIALIZE, initEvents);
	}

}
