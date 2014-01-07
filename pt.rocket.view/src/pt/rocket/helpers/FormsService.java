package pt.rocket.framework.service.services;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.events.GetFormsDatasetListEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.forms.FormData;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.net.Uri;
import de.akquinet.android.androlog.Log;

/**
 * Service used to manage the forms. This class manages the: - Register - Addresses - Login -
 * PasswordRecovery - Edit user - Payment Method.
 * 
 * When the user needs a form, the service checks if the local data has it, if it doesn't, load it
 * from the api. When the service starts it loads the data from the file. When a new form is loaded,
 * the data is saved to file. There is a key value map with the name of the form and the content.
 * 
 * @author GuilhermeSilva
 * 
 *         commented the getForm() and the save the form data when it gets the form.
 */
public class FormsService extends DarwinService {

	private static final String TAG = LogTagHelper.create(FormsService.class);

	//private static final String JSON_NAME_TAG = "name";

	/**
	 * Dictionary that keeps the action key and form data
	 */
	private Map<String, FormData> formDataRegistry;

	/**
	 * Dictionary that keeps the record of the forms and their respective action.
	 */
//	private Map<String, ArrayList<Form>> formRegistry = new HashMap<String, ArrayList<Form>>();

	/**
	 * 
	 */
	public FormsService() {
		super(EnumSet.of(EventType.INIT_FORMS), EnumSet.of(EventType.INIT_SHOP,
				EventType.GET_FORMS_DATASET_LIST_EVENT,
				EventType.GET_REGISTRATION_FORM_EVENT, EventType.GET_REGISTRATION_EDIT_FORM_EVENT,
				EventType.GET_FORGET_PASSWORD_FORM_EVENT, EventType.GET_LOGIN_FORM_EVENT,
				EventType.GET_CHANGE_PASSWORD_FORM_EVENT));
	}

	/**
	 * updates the formDataRegistry and loads the forms.
	 */
	private void initFormsRegistry(final RequestEvent event) {
		if (formDataRegistry != null) {
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseEvent(event, null));
			return;
		}
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<Void>(event) {
					@Override
					public Void parseResponse(JSONObject metadataObject)
							throws JSONException {
						formDataRegistry = new HashMap<String, FormData>();

						JSONArray dataArray = metadataObject
								.getJSONArray(RestConstants.JSON_DATA_TAG);
						int dataArrayLength = dataArray.length();
						for (int i = 0; i < dataArrayLength; ++i) {
							JSONObject formDataObject = dataArray
									.getJSONObject(i);
							FormData formData = new FormData();
							formData.initialize(formDataObject);
							formDataRegistry.put(formData.getAction(), formData);
						}
						return null;
					}
				}, event.metaData);

	}

	/**
	 * checks if the formregistry is valida and returns the correct form
	 * 
	 * @param id
	 *            of the form
	 * @param listener
	 *            that is waiting for the form.
	 */
	private void getForms(final RequestEvent event) {
//		if (formRegistry.containsKey(action)) {
//			EventManager.getSingleton().triggerResponseEvent(
//					new ResponseResultEvent<Form>(event, formRegistry.get(
//							action).get(0), null));
//			return;
//		}
		

		// Send error
		if ( null == formDataRegistry ) {
			Log.w(TAG, "FORM DATA REGISTRY IS NULL " + event.getType().toString());
			EventManager.getSingleton().triggerResponseEvent(new ResponseErrorEvent(event, ErrorCode.UNKNOWN_ERROR));
			return;
		}
		
		FormData formData = formDataRegistry.get(event.eventType.action);
		String url = formData.getUrl();
		RestServiceHelper.requestGet(Uri.parse(url),
				new ResponseReceiver<Form>(event) {

					@Override
					public Form parseResponse(JSONObject formMetadata)
							throws JSONException {
						final ArrayList<Form> forms = new ArrayList<Form>();
						JSONArray dataObject = formMetadata
								.getJSONArray(RestConstants.JSON_DATA_TAG);

						for (int i = 0; i < dataObject.length(); ++i) {
							Form form = new Form();
							JSONObject formObject = dataObject.getJSONObject(i);
							if (!form.initialize(formObject)) {
								Log.e(TAG,
										"Error initializing the form using the data");
							}
							forms.add(form);
						}
//						formRegistry.put(action, forms);
						if (forms.size() > 0) {
							return forms.get(0);
						}
						return null;
					}
				}, event.metaData);
	}

	/**
	 * Gets the DatasetList and triggers the GetDataSetCompletedEvent
	 * 
	 * @param key
	 *            of the dataset
	 * @param url
	 *            of the service
	 */
	public static void getDatasetList(final GetFormsDatasetListEvent event) {

		RestServiceHelper.requestGet(Uri.parse(event.url),
				new ResponseReceiver<Map<String, String>>(event) {
					@Override
					public Map<String, String> parseResponse(JSONObject metadataObject)
							throws JSONException {
						String JSON_ID_TAG = event.key;
						HashMap<String, String> values = new HashMap<String, String>();

						if (JSON_ID_TAG.substring(0, 3).equals("fk_")) {
							JSON_ID_TAG = JSON_ID_TAG.replace("fk_", "id_");
						}

						JSONArray dataObject = metadataObject
								.getJSONArray(RestConstants.JSON_DATA_TAG);

						String key = "";
						String value = "";

						for (int i = 0; i < dataObject.length(); i++) {
							JSONObject datasetObject = dataObject
									.getJSONObject(i);

							key = String.valueOf(datasetObject.optInt(
									JSON_ID_TAG, 0));
							value = datasetObject.optString(RestConstants.JSON_NAME_TAG);

							values.put(key, value);
						}
						return values;
					}
				}, event.metaData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		switch (event.getType()) {
		case INIT_SHOP:
			formDataRegistry = null;
			break;
		case GET_FORMS_DATASET_LIST_EVENT:
			getDatasetList((GetFormsDatasetListEvent) event);
			break;
		case GET_REGISTRATION_FORM_EVENT:
		case GET_REGISTRATION_EDIT_FORM_EVENT:
		case GET_CHANGE_PASSWORD_FORM_EVENT:
		case GET_FORGET_PASSWORD_FORM_EVENT:
		case GET_LOGIN_FORM_EVENT:
			getForms(event);
			break;
		case INIT_FORMS:
			initFormsRegistry(event);
			break;
		}
	}
	
}
