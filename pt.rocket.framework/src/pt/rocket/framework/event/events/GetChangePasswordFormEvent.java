/**
 * 
 * @author Nuno Castro
 * 
 * @version 1.01
 * 
 * 2012/08/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.IEvent;

/**
 * Event used to get the change password form from the mobile api
 * 
 * @author nunocastro
 *
 */
public class GetChangePasswordFormEvent implements IEvent {

    private static final EventType type = EventType.GET_CHANGE_PASSWORD_FORM_EVENT;

    /**
     * @return the event type
     */
    public static EventType getEventType() {
        return type;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.event.IEvent#getType()
     */
    @Override
    public EventType getType() {
        return type;
    }

	@Override
	public String getMD5Hash() {
		return null;
	}
}
