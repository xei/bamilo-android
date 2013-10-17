package pt.rocket.framework.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;


/**
 * Helpers class responsible for parsing the error messages from the api. Use the method createErrorMessageMap to parse the json object into an string -> string array map.
 *
 */
public class Errors {
	private final static String TAG = Errors.class.getSimpleName();
	
//	public static final String JSON_VALIDATE_TAG = "validate";
//	public static final String JSON_ERROR_TAG = "error";

	public final static String CODE_LOGIN_FAILED = "CUSTOMER_LOGIN_FAILED";
	public final static String CODE_LOGIN_CHECK_PASSWORD = "CUSTOMER_LOGIN_CHECK_EMAIL_PASSWORD";
	public final static String CODE_LOGOUT_FAILED = "CUSTOMER_LOGOUT_FAILED";
	public final static String CODE_LOGOUT_NOTLOGGED_IN = "CUSTOMER_NOT_LOGGED_IN";
	public final static String CODE_REGISTER_CUSTOMEREXISTS = "CUSTOMER_CREATE_FAILED_EXISTS";
	public final static String CODE_FORGOTPW_NOSUCH_CUSTOMER = "CUSTOMER_SET_PASSWORD_RESTORE_DATA_FAILURE";
	public final static String CODE_ORDER_PRODUCT_SOLD_OUT = "ORDER_PRODUCT_SOLD_OUT";
	public final static String CODE_CUSTOMER_NOT_LOGGED_ID = "CUSTOMER_NOT_LOGGED_IN";
	public final static String CODE_FORM_VALIDATION_FAILED = "FORM_VALIDATION_FAILED";
	
	
	public final static String CODE_PRODUCT_ADD_OVERQUANTITY = "SR_ORDER_PRODUCT_ERROR_ADDING_STOCK_ABOVE_ALLOWED_QUANTITY";
	public final static String CODE_ORDER_PRODUCT_ERROR_ADDING = "ORDER_PRODUCT_ERROR_ADDING";

//	private final static String JSON_SUCCESS_TAG = "success";
//	private final static String JSON_MESSAGE_IN_MESSAGES_TAG = "message";

	private static List<String> errorMessages;
	private static List<String> validateMessages;

	public static HashMap<String, List<String>> createErrorMessageMap(JSONObject messagesObject) {

		HashMap<String, List<String>> map = new HashMap<String, List<String>>();

		// Parsing Error Messages
		errorMessages = null;
		if ( messagesObject == null) {
			return map;
		} else if (parseErrorArrayWithObject(messagesObject) || parseErrorArray(messagesObject)
				|| parseSuccessArray(messagesObject)) {
			map.put(RestConstants.JSON_ERROR_TAG, errorMessages);
		}

		// Parsing Validate Messages
		validateMessages = null;
		if (parseValidateObjectWithObject(messagesObject) || parseValidateObjectWithObjectWithArray(messagesObject) || parseValidateArray(messagesObject)) {
			map.put(RestConstants.JSON_VALIDATE_TAG, validateMessages);
		}

		dumpMessages();
		return map;
	}

	public static void dumpMessages() {
		if (errorMessages != null) {
			Log.d(TAG, "dumpMessages: error ");
			for (String error : errorMessages) {
				Log.d(TAG, "dumpMessages: error = " + error);
			}
		}

		if (validateMessages != null) {
			Log.d(TAG, "dumpMessages: validate");
			for (String validate : validateMessages) {
				Log.d(TAG, "dumpMessages: validates = " + validate);
			}
		}
	}

	private static boolean parseTagArray(JSONObject messagesObject, String tag) {
		JSONArray errorsObject = messagesObject.optJSONArray(tag);
		if (errorsObject == null) {
			// Log.d(TAG, "tried to parse messages " + tag + " array - not successful - ignoring");
			return false;
		}

		errorMessages = parseArray(errorsObject);
		return true;
	}

	private static boolean parseErrorArray(JSONObject messagesObject) {
		return parseTagArray(messagesObject, RestConstants.JSON_ERROR_TAG);
	}

	private static boolean parseValidateArray(JSONObject messagesObject) {
		JSONArray validateObject = messagesObject.optJSONArray( RestConstants.JSON_VALIDATE_TAG);
		if (validateObject == null) {
			// Log.d(TAG, "tried to parse messages validate array - not successful - ignoring");
			return false;
		}

		validateMessages = parseArray(validateObject);
		return true;
	}

	private static boolean parseSuccessArray(JSONObject messagesObject) {
		return parseTagArray(messagesObject, RestConstants.JSON_SUCCESS_TAG);
	}

	private static boolean parseErrorArrayWithObject(JSONObject messagesObject) {
		JSONArray errorArray = messagesObject.optJSONArray(RestConstants.JSON_ERROR_TAG);
		if (errorArray == null) {
			Log.d(TAG, "tried to parse messages error array with object - not successful - ignoring");
			return false;
		}

		ArrayList<HashMap<String, String>> fullErrorMessages = parseArrayWithObject(errorArray);
		if (fullErrorMessages == null) {
			return false;
		}

		errorMessages = extractErrorMessagesOnly(fullErrorMessages);
		if (errorMessages.isEmpty())
			return false;

		return true;
	}

	private static ArrayList<String> extractErrorMessagesOnly(ArrayList<HashMap<String, String>> errorMessages) {
		ArrayList<String> messagesOnly = new ArrayList<String>();

		for (HashMap<String, String> messageMap : errorMessages) {
			messagesOnly.add(messageMap.get(RestConstants.JSON_MESSAGE_IN_MESSAGES_TAG));
		}

		return messagesOnly;
	}

	private static boolean parseValidateObjectWithObject(JSONObject messagesObject) {
		JSONObject validateObject = messagesObject.optJSONObject(RestConstants.JSON_VALIDATE_TAG);
		if (validateObject == null) {
			Log.d( TAG, "tried to parse messages validate object with object - not successful - ignoring" );
			return false;
		}

		HashMap<String, HashMap<String, String>> fullValidateMessages = parseObjectWithObject(validateObject);

		validateMessages = extractValidateMessagesOnly(fullValidateMessages);
		if (validateMessages.isEmpty())
			return false;

		return true;
	}
	
	private static boolean parseValidateObjectWithObjectWithArray(JSONObject messagesObject) {
		JSONObject validateObject = messagesObject.optJSONObject(RestConstants.JSON_VALIDATE_TAG);
		if (validateObject == null)
			return false;
		
		HashMap<String, ArrayList<String>> fullValidateMessages = parseObjectWithArray(validateObject);
		
		validateMessages = extractValidateMessageOnly(fullValidateMessages);
		if ( validateMessages.isEmpty())
			return false;
		
		return true;
	}
	

	private static ArrayList<String> extractValidateMessagesOnly(
			HashMap<String, HashMap<String, String>> validateMessages) {
		ArrayList<String> messagesOnly = new ArrayList<String>();
		for( Entry<String, HashMap<String, String>> entry: validateMessages.entrySet()) {
			HashMap<String, String> map = entry.getValue();
			for( String key: map.keySet()) {
				String message = map.get(key);
				if ( messagesOnly.contains(message))
					continue;
				
				messagesOnly.add( message );
			}	
		}

		return messagesOnly;
	}

	private static List<String> extractValidateMessageOnly(
			HashMap<String, ArrayList<String>> validateMessages) {
		ArrayList<String> messagesOnly = new ArrayList<String>();
		for( Entry<String, ArrayList<String>> fields: validateMessages.entrySet()) {
			for( String message: fields.getValue()) {
				if ( messagesOnly.contains(message))
					continue;
				
				messagesOnly.add( message ); 
			}
		}
		
		return messagesOnly;
	}

	private static HashMap<String, HashMap<String, String>> parseObjectWithObject(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		HashMap<String, HashMap<String, String>> content = new HashMap<String, HashMap<String, String>>();
		Iterator<?> iter = jsonObject.keys();
		while (iter.hasNext()) {
			String key = iter.next().toString();
			JSONObject value = jsonObject.optJSONObject(key);
			if (value == null)
				continue;

			content.put(key, parseObjectWithStrings(value));
		}

		return content;
	}

	private static HashMap<String, ArrayList<String>> parseObjectWithArray(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		
		HashMap<String, ArrayList<String>> content = new HashMap<String, ArrayList<String>>();
		Iterator<?> iter = jsonObject.keys();
		while( iter.hasNext()) {
			String key = iter.next().toString();
			JSONArray value = jsonObject.optJSONArray(key);
			if (value == null)
				continue;
			
			content.put(key,  parseArray(value));
		}
		
		return content;
	}

	private static HashMap<String, String> parseObjectWithStrings(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		HashMap<String, String> content = new HashMap<String, String>();
		Iterator<?> iter = jsonObject.keys();
		while (iter.hasNext()) {
			String key = iter.next().toString();
			String value = jsonObject.optString(key);
			if (value == null)
				continue;
			content.put(key, value);
		}

		return content;
	}

	private static ArrayList<String> parseArray(JSONArray jsonArray) {
		if (jsonArray == null)
			return null;

		ArrayList<String> list = new ArrayList<String>();
		int arrayLength = jsonArray.length();
		for (int i = 0; i < arrayLength; ++i) {
			String value = jsonArray.optString(i);
			if (value == null)
				continue;

			list.add(value);
		}
		return list;
	}

	private static ArrayList<HashMap<String, String>> parseArrayWithObject(JSONArray jsonArray) {
		if (jsonArray == null)
			return null;

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		int arrayLength = jsonArray.length();
		for (int i = 0; i < arrayLength; ++i) {
			HashMap<String, String> object = parseObject(jsonArray.optJSONObject(i));
			if (object == null) {
				continue;
			}

			list.add(object);
		}
		return list;
	}

	private static HashMap<String, String> parseObject(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		HashMap<String, String> content = new HashMap<String, String>();
		Iterator<?> iter = jsonObject.keys();
		while (iter.hasNext()) {
			String key = iter.next().toString();
			String value = jsonObject.optString(key);
			if (value != null)
				content.put(key, value.toString());
		}
		return content;
	}

}
