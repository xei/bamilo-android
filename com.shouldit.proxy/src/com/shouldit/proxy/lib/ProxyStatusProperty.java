package com.shouldit.proxy.lib;

import java.io.Serializable;

import com.shouldit.proxy.lib.APLConstants.ProxyStatusProperties;
import com.shouldit.proxy.lib.APLConstants.CheckStatusValues;

public class ProxyStatusProperty implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3857385153670591196L;
	
	public ProxyStatusProperties propertyName;
	public CheckStatusValues status;
	public Boolean result;
	
	public ProxyStatusProperty(ProxyStatusProperties name)
	{
		propertyName = name;
		status = CheckStatusValues.NOT_CHECKED;
		result = false;
	}
}
