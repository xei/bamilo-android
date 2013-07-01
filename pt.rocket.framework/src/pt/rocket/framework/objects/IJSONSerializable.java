/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This classes specifies the behavior of all the object that interact with JSON.
 */
public interface IJSONSerializable {
	/**
	 * List of string the represent the class properties in the Address Object
	 * Model.
	 */
	public static final String JSON_DATA_TAG = "data";
	
	public static final String JSON_ID_TAG = "id";

	/**
	 * @param jsonObject
	 *            JSONObject containing the parameters of the object
	 * @return Returns a boolean specifying if the initialization works, if
	 *         false, the initialization failed and the object is obsolete
	 * @throws JSONException 
	 */
	boolean initialize(JSONObject jsonObject) throws JSONException;

	/**
	 * @return Returns the JSONObject created from this object
	 */
	JSONObject toJSON();
}
