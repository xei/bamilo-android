/** 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;

/**
 * Event used to get the suggestions that match a given query.
 * 
 * @author GuilhermeSilva
 * 
 */
public class GetSearchSuggestionsEvent extends MetaRequestEvent<String> {
	private static final EventType type = EventType.GET_SEARCH_SUGGESTIONS_EVENT;

	public GetSearchSuggestionsEvent(String searchQuery) {
		super(type, searchQuery);
	}

}
