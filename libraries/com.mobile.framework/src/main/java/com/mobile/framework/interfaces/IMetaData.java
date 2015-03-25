package com.mobile.framework.interfaces;

/**
 * Class used to define the variables for the request event or response event metadata
 * @author nutzer2
 *
 */
public interface IMetaData {
	
	/**
	 * Sets the request to not cache the response in the httpclient
	 */
	public static final String MD_IGNORE_CACHE = "ignore_cache";
	
	public static final Boolean TRUE = true;
	public static final Boolean FALSE = false;

	public static final String URI = "uri";
	
	public static final String LOCATION = "location";
}
