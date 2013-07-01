package pt.rocket.framework.event.events;

import android.content.ContentValues;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

public class StoreEvent extends MetaRequestEvent<ContentValues> {
	/**
	 * @param type
	 * @param value
	 */
	public StoreEvent(EventType type, ContentValues value) {
		super(type, value);
	}
}
