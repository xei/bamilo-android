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
package pt.rocket.forms;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents the form fiel validation parameters.
 * @author GuilhermeSilva
 *
 */
public class FieldValidation implements IJSONSerializable, Parcelable {
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


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MIN_CHARACTERS);
        dest.writeInt(MAX_CHARACTERS);
        dest.writeString(DEFAULT_REGEX);
        dest.writeBooleanArray(new boolean[] {required});
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeString(regex);
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private FieldValidation(Parcel in) {
        
        MIN_CHARACTERS = in.readInt();
        MAX_CHARACTERS = in.readInt();
        DEFAULT_REGEX = in.readString();
        in.readBooleanArray(new boolean[] {required});
        min = in.readInt();
        max = in.readInt();
        regex = in.readString();
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<FieldValidation> CREATOR = new Parcelable.Creator<FieldValidation>() {
        public FieldValidation createFromParcel(Parcel in) {
            return new FieldValidation(in);
        }

        public FieldValidation[] newArray(int size) {
            return new FieldValidation[size];
        }
    };
}
