package com.mobile.framework.interfaces;

import org.json.JSONObject;

/**
 * Interface to declare required methods upon json parsing
 * @author josedourado
 *
 */
public interface IJSONSerializable {
	
	/**
	 * Initializes a jsonobject to the needed objects
	 */
	boolean initialize(JSONObject obj);



}
