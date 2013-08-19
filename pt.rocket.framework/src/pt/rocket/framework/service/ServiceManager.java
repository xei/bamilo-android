/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.RequestListener;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.events.GetCallToOrderPhoneEvent;
import pt.rocket.framework.event.events.InitShopEvent;
import pt.rocket.framework.event.events.InitializeEvent;
import pt.rocket.framework.service.services.ApiService;
import pt.rocket.framework.service.services.CategoryService;
import pt.rocket.framework.service.services.ConfigurationService;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.service.services.FormsService;
import pt.rocket.framework.service.services.NavigationService;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.service.services.ShoppingCartService;
import pt.rocket.framework.service.services.TeasersService;
import pt.rocket.framework.utils.SingletonMap;
import android.content.Context;
import android.util.Log;

/**
 * Service Manager class. Manages all the services from the framework.
 * 
 * @author GuilhermeSilva
 * 
 */
public class ServiceManager extends RequestListener {

	public static final SingletonMap<DarwinService> SERVICES = new SingletonMap<DarwinService>(
			new ApiService(), new NavigationService(), new FormsService(),
			new TeasersService(), new CategoryService(), new ProductService(),
			new CustomerAccountService(), new ShoppingCartService(), new ConfigurationService() );
	private static final EnumSet<EventType> INIT_EVENTS = EnumSet
			.noneOf(EventType.class);
	static {
		for (DarwinService service : SERVICES.values()) {
			INIT_EVENTS.addAll(service.getEventsForInitialization());
		}
	}

	/**
	 * Initializes all the events.
	 */
	private ServiceManager() {
		super(false);
		EventManager.getSingleton().addRequestListener(EventType.INITIALIZE,
				this);
	}

	/**
	 * Singleton instance
	 */
	private static ServiceManager INSTANCE = null;

	public static ServiceManager init(Context context, int shopId) {
		if (INSTANCE == null ) {
			INSTANCE = new ServiceManager();
			INSTANCE.initServices(context.getApplicationContext());
		}
		EventManager.getSingleton().triggerRequestEvent(
				new InitShopEvent(shopId));
		return INSTANCE;
	}

	public static synchronized ServiceManager getSingleton() {
		if (INSTANCE == null) {
			throw new RuntimeException(
					"ServiceManager needs to be initialized!");
		}
		return INSTANCE;
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
		initServices((InitializeEvent) event);
	}

	private void initServices(Context context) {
		for (DarwinService service : SERVICES.values()) {
		    Log.i("DarwinService", "code1service : "+service.toString());
			service.init(context);
		}
	}

	private void initServices(InitializeEvent event) {
		EnumSet<EventType> initEvents = event.value;
		initEvents.addAll(INIT_EVENTS);
		EventManager.getSingleton().addResponseListener(
				new InitResponseListener(initEvents, event), initEvents);
		for (EventType eventType : initEvents) {
			RequestEvent reqEvent = new RequestEvent(eventType);
			reqEvent.metaData.putAll( event.metaData );
			EventManager.getSingleton().triggerRequestEvent( reqEvent );
		}
	}

	private class InitResponseListener implements ResponseListener {

		private EnumSet<EventType> initEvents;
		private ErrorCode errorCode = ErrorCode.NO_ERROR;
		private RequestEvent request;
		private Map<String, ? extends List<String>> errorMessages;

		/**
		 * 
		 */
		public InitResponseListener(EnumSet<EventType> initializationEvents,
				RequestEvent request) {
			initEvents = EnumSet.copyOf(initializationEvents);
			this.request = request;
		}

		@Override
		public boolean removeAfterHandlingEvent() {
			return true;
		}

		@Override
		public void handleEvent(ResponseEvent event) {
			initEvents.remove(event.getType());
			if (!event.getSuccess()) {
				errorCode = event.errorCode;
				if (errorCode == ErrorCode.REQUEST_ERROR) {
					errorMessages = event.errorMessages;
				}
			}

			if (initEvents.isEmpty()) {
				ResponseEvent response;
				switch (errorCode) {
				case NO_ERROR:
					response = new ResponseEvent(request, null);
					break;
				case REQUEST_ERROR:
					response = new ResponseErrorEvent(request, errorMessages);
					break;
				default:
					response = new ResponseErrorEvent(request, errorCode);
				}
				EventManager.getSingleton().triggerResponseEvent(response);
			}
		}

	}

}
