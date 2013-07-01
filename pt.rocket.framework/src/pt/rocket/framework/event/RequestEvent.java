/**
 * 
 */
package pt.rocket.framework.event;

import java.util.HashMap;

import android.os.Bundle;

/**
 * @author nutzer2
 * 
 */
public class RequestEvent implements IEvent, IMetaData {

	public final EventType eventType;

	public final Bundle metaData = new Bundle();

	/**
	 * 
	 */
	public RequestEvent(EventType eventType) {
		this.eventType = eventType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.event.IEvent#getType()
	 */
	@Override
	public final EventType getType() {
		return eventType;
	}

}
