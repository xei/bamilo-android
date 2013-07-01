package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;

/**
 * Event used to get the address form dataset list
 * 
 * @author nunocastro
 * 
 */
public class GetFormsDatasetListEvent extends RequestEvent {

	public static final EventType type = EventType.GET_FORMS_DATASET_LIST_EVENT;

	public final String url;
	public final String key;

	public GetFormsDatasetListEvent(String key, String url) {
		super(type);
		this.url = url;
		this.key = key;
	}

}
