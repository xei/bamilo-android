/**
 * 
 */
package pt.rocket.framework.event;

import android.os.Bundle;

/**
 * Event sent from the activity to the Service
 * @author nutzer2
 * 
 */
public class RequestEvent implements IEvent, IMetaData {

	public final EventType eventType;

	public final Bundle metaData = new Bundle();
	
	public String md5Hash;

	/**
	 * Base constructor
	 */
	public RequestEvent(EventType eventType) {
		this.eventType = eventType;
		this.md5Hash = null;
	}

	/**
	 * Base constructor
	 */
	public RequestEvent(EventType eventType, String md5Hash) {
		this.eventType = eventType;
		this.md5Hash = md5Hash;
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
	
	@Override
	public String getMD5Hash() {		
		return md5Hash;
	}

}
