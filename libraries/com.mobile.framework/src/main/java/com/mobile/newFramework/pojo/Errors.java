package com.mobile.newFramework.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;


/**
 * Helpers class responsible for parsing the error messages from the api. Use the method createErrorMessageMap to parse the json object into an string -> string array map.
 *
 */
public class Errors implements Parcelable {
	private final static String TAG = Errors.class.getSimpleName();

	public final static String CODE_CUSTOMER_NOT_LOGGED_IN = "CUSTOMER_NOT_LOGGED_IN";
	public final static String CODE_REGISTER_CUSTOMEREXISTS = "CUSTOMER_CREATE_FAILED_EXISTS";
	public final static String CODE_ORDER_PRODUCT_SOLD_OUT = "ORDER_PRODUCT_SOLD_OUT";
	public final static String CODE_PRODUCT_ADD_OVERQUANTITY = "SR_ORDER_PRODUCT_ERROR_ADDING_STOCK_ABOVE_ALLOWED_QUANTITY";
	public final static String CODE_ORDER_PRODUCT_ERROR_ADDING = "ORDER_PRODUCT_ERROR_ADDING";
	public final static String CODE_ERROR_ADDING_ITEM = "ERROR_ADDING_ITEM";
	

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

		if(map.isEmpty()){
			parseSuccessObject(messagesObject, map);
		}

		// Parsing Validate Messages
		validateMessages = null;
		if (parseValidateObjectWithObject(messagesObject) || parseValidateObjectWithObjectWithArray(messagesObject) || parseValidateArray(messagesObject)) {
			map.put(RestConstants.JSON_VALIDATE_TAG, validateMessages);
		}

		dumpMessages();
		return map;
	}

	private static void parseSuccessObject(JSONObject messagesObject, HashMap<String, List<String>> map) {
		JSONObject errorObject = messagesObject.optJSONObject(RestConstants.JSON_ERROR_TAG);
		if(errorObject != null){
			Iterator<?> iter = errorObject.keys();
			while (iter.hasNext()) {
				List<String> tmp = new LinkedList<>();
				String key = iter.next().toString();
				tmp.add(errorObject.optString(key));
				map.put(key,tmp);
			}
		}
	}

	public static void dumpMessages() {
		if (errorMessages != null) {
			Print.d(TAG, "dumpMessages: ");
			for (String error : errorMessages) {
				Print.d(TAG, "dumpMessages = " + error);
			}
		}

		if (validateMessages != null) {
			Print.d(TAG, "dumpMessages: validate");
			for (String validate : validateMessages) {
				Print.d(TAG, "dumpMessages: validates = " + validate);
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
			Print.d(TAG, "tried to parse messages error array with object - not successful - ignoring");
			return false;
		}

		ArrayList<HashMap<String, String>> fullErrorMessages = parseArrayWithObject(errorArray);
		if (fullErrorMessages == null) {
			return false;
		}

		errorMessages = extractErrorMessagesOnly(fullErrorMessages);
		return !errorMessages.isEmpty();

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
			Print.d(TAG, "tried to parse messages validate object with object - not successful - ignoring");
			return false;
		}

		HashMap<String, HashMap<String, String>> fullValidateMessages = parseObjectWithObject(validateObject);

		validateMessages = extractValidateMessagesOnly(fullValidateMessages);
		return !validateMessages.isEmpty();

	}
	
	private static boolean parseValidateObjectWithObjectWithArray(JSONObject messagesObject) {
		JSONObject validateObject = messagesObject.optJSONObject(RestConstants.JSON_VALIDATE_TAG);
		if (validateObject == null)
			return false;
		
		HashMap<String, ArrayList<String>> fullValidateMessages = parseObjectWithArray(validateObject);
		
		validateMessages = extractValidateMessageOnly(fullValidateMessages);
		return !validateMessages.isEmpty();

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
	
	
    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeStringList(errorMessages);
	    dest.writeStringList(validateMessages);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private Errors(Parcel in) {
		errorMessages = new ArrayList<String>();
		in.readStringList(errorMessages);
		validateMessages = new ArrayList<String>();
		in.readStringList(validateMessages);
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Errors> CREATOR = new Parcelable.Creator<Errors>() {
        public Errors createFromParcel(Parcel in) {
            return new Errors(in);
        }

        public Errors[] newArray(int size) {
            return new Errors[size];
        }
    };

}
