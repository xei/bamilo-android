/**
 * 
 */
package pt.rocket.framework.service.services;

import java.util.EnumSet;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;


import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.objects.Promotion;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;

/**
 * @author nunocastro
 *
 */
public class ConfigurationService extends DarwinService {

	private static final String TAG = LogTagHelper.create(ApiService.class);
	private static String phoneNumber = "";
	
	//private static final String JSON_PHONE_TAG = "metadata";
	
	public ConfigurationService() {		
		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.GET_CALL_TO_ORDER_PHONE, EventType.GET_PROMOTIONS));
	}

	@Override
	public void handleEvent(RequestEvent event) {
		switch ( event.getType() ) {
		case GET_CALL_TO_ORDER_PHONE:
			getCallToOrderNumberFromApi( event );
			break;
		case GET_PROMOTIONS:
			getActivePromotions(event);
			break;
		default:
			Log.i("","ConfigurationService: NOT HANDLED EVENT -> " + event.getType());
			break;
		}
		
	}
	
	private void getCallToOrderNumberFromApi(final RequestEvent event) {
		/*
		 * Retrieves the number to call when the user presses the 'Call To Order' button on the product page 
		 */
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<String>(event) {

					@Override
					public String parseResponse(JSONObject metadataObject) throws JSONException {						
						String phone = "";
						if ( null != metadataObject ) {
							phone = metadataObject.optString(RestConstants.JSON_CALL_PHONE_TAG).split("-")[0];
						}
						
						return phone;
					}
				}, event.metaData);		
	}
	
	private void getActivePromotions(final RequestEvent event) {
		/*
		 * Retrieves the Promotions if defined in bob.
		 */
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<Promotion>(event) {

					@Override
					public Promotion parseResponse(JSONObject metadataObject) throws JSONException {
						Promotion promo = new Promotion();
						if ( null != metadataObject ) {
							promo.initialize(metadataObject.getJSONObject(RestConstants.JSON_DATA_TAG));
						}
						
						return promo;
					}
				}, event.metaData);		
	}

}
