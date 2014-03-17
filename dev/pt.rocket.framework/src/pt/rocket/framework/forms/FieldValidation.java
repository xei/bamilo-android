/**
 * FieldValidation.java
 * form field validation class. Manages if the form field is required, the max and minimum number of characters and the regular expression.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.forms;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;

/**
 * Class that represents the form fiel validation parameters.
 * @author GuilhermeSilva
 *
 */
public class FieldValidation implements IJSONSerializable {
//	private static final String JSON_REQUIRED_TAG = "required";
//	private static final String JSON_MIN_TAG = "min";
//	private static final String JSON_MAX_TAG = "max";
//    private static final String JSON_REGEX_TAG = "regex";

	private static int MIN_CHARACTERS = 0;
	private static int MAX_CHARACTERS = 40;
	private static String DEFAULT_REGEX = "[0-9a-zA-Z-]*";

	public boolean required;
	public int min;
	public int max;
	public String regex;

	/**
	 * FormValidation empty constructor.
	 */
	public FieldValidation() {
		required = false;
		min = MIN_CHARACTERS;
		max = MAX_CHARACTERS;
		regex = DEFAULT_REGEX;
	}

	
	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) {

		required = jsonObject.optBoolean(RestConstants.JSON_REQUIRED_TAG, false);
		min = jsonObject.optInt(RestConstants.JSON_MIN_TAG, MIN_CHARACTERS);
		max = jsonObject.optInt(RestConstants.JSON_MAX_TAG, MAX_CHARACTERS);
        regex = jsonObject.optString(RestConstants.JSON_REGEX_TAG, DEFAULT_REGEX);
        
        if ( regex.substring(0, 2).equals("a/") ) {
            regex = regex.substring(2);
        }

        if ( regex.substring(0, 1).equals("/") ) {
            regex = regex.substring(1);
        }
        
        if ( regex.charAt( regex.length() - 1) == '/' ) {
            regex = regex.substring(0, regex.length() - 1);
        }        
        
		return true;
	}

	/**
	 * @return if the field is required.
	 */
	public boolean isRequired() {
		return required;
	}
	
	/*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(RestConstants.JSON_REQUIRED_TAG, required);
			jsonObject.put(RestConstants.JSON_MIN_TAG, min);
			jsonObject.put(RestConstants.JSON_MAX_TAG, max);
			jsonObject.put(RestConstants.JSON_REGEX_TAG, regex);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}
