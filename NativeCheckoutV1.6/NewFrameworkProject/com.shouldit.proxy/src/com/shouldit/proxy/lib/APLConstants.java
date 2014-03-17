package com.shouldit.proxy.lib;

public class APLConstants
{
	/**
	 * Broadcasted intent when updates on the proxy status are available    
	 * */
	public static final String APL_UPDATED_PROXY_STATUS_CHECK = "com.shouldit.proxy.lib.PROXY_CHECK_STATUS_UPDATE";
	public static final String ProxyStatus = "ProxyStatus"; 
	
//	public enum ProxyStatusCodes
//	{
//		NOT_CHECKED,
//		FOUND_PROBLEM_CHECKING,
//		PROXY_ENABLED,
//		PROXY_ADDRESS_VALID,
//		PROXY_REACHABLE,
//		WEB_REACHABILE,
//		CONFIGURATION_OK
//	}
	
	public enum ProxyStatusErrors
	{
		PROXY_NOT_ENABLED,
		PROXY_NOT_REACHABLE,
		PROXY_ADDRESS_NOT_VALID,
		WEB_NOT_REACHABLE,
		NO_ERRORS
	}
	
	public enum ProxyStatusProperties
	{
		PROXY_ENABLED,
		PROXY_VALID_ADDRESS,
		PROXY_REACHABLE,
		WEB_REACHABLE
	}
	
	public enum CheckStatusValues
	{
		NOT_CHECKED,
		CHECKING,
		CHECKED
	}
}
