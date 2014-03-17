package pt.rocket.framework.event.events;

import android.content.ContentValues;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

public class StoreEvent extends MetaRequestEvent<ContentValues> {
	/**
	 * Base constructor
	 * @param type type of the event
	 * @param value values that are passe through the http client
	 */
	public StoreEvent(EventType type, ContentValues value) {
		super(type, value);
	}
}
