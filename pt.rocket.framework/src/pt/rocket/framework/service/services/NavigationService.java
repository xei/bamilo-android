package pt.rocket.framework.service.services;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import android.content.Context;
import de.akquinet.android.androlog.Log;

/**
 * Service that manages the navigation list components.
 * 
 * @author GuilhermeSilva
 * 
 */
public class NavigationService extends DarwinService {

	private static final String TAG = NavigationService.class.getSimpleName();

	/**
	 * Constructor for the Navigation Service.
	 */
	public NavigationService() {
		super(EnumSet.of(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT),
				EnumSet.noneOf(EventType.class));
	}

	/**
	 * Function used to to get the navigation list components. This function
	 * triggers the GetNavigationListComponentsCompletedEvent when finished.
	 * 
	 * @param event
	 */
	public void getNavigationListComponents(final RequestEvent event) {
		getServerNavigationListComponents(event);
	}


	/**
	 * Returns the server stored navigation list components and triggers the
	 * GetNavigationListComponentsCompletedEvent.
	 * 
	 * @param event
	 */
	private static void getServerNavigationListComponents(final RequestEvent event) {
		RestServiceHelper.requestGet(event.eventType.action,
				new ResponseReceiver<List<NavigationListComponent>>(event) {

					@Override
					public List<NavigationListComponent> parseResponse(JSONObject metadataObject)
							throws JSONException {
						ArrayList<NavigationListComponent> components = new ArrayList<NavigationListComponent>();
						JSONArray dataArray = metadataObject
								.getJSONArray(RestConstants.JSON_DATA_TAG);
						NavigationListComponent component;
						int dataArrayLenght = dataArray.length();
						for (int i = 0; i < dataArrayLenght; ++i) {
							component = new NavigationListComponent();
							component.initialize(dataArray.getJSONObject(i));
							components.add(component);
						}

						components.add(new NavigationListComponent(0, null, "loginout", null));
						return components;
					}
				}, event.metaData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
	 * .event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		getNavigationListComponents(event);
	}

}
