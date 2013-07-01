/**
 * Abstract class that is implemented by all the events triggered by the the framework service. 
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

import pt.rocket.framework.ErrorCode;

/**
 * Abstract class that specifies the events that are return from api calls.
 * @author GuilhermeSilva
 *
 */
public class ResponseEvent implements IEvent{
	
	public final ErrorCode errorCode;
	public final EventType type;
	public final RequestEvent request;
	public final Map<String, ? extends List<String>> errorMessages;
	public final String warning;
		
    /**
     * @param success of the request.
     * @param errorCode
     */
    public ResponseEvent(RequestEvent request, String warning) {
        this(request, ErrorCode.NO_ERROR, warning);
    }
    
    /**
     * @param errorCode
     * @param success of the request.
     */
    protected ResponseEvent(RequestEvent request, ErrorCode errorCode, String warning) {
    	this(request, errorCode, Collections.<String, List<String>>emptyMap(), warning);
    }
    
	protected ResponseEvent(RequestEvent request, ErrorCode errorCode,
			Map<String, ? extends List<String>> errorMessages, String warning) {
		this.request = request;
		this.type = request.getType();
		this.errorCode = errorCode;
		this.errorMessages = errorMessages;
		this.warning = warning;
	}
    
    /* (non-Javadoc)
     * @see pt.rocket.framework.event.IEvent#getType()
     */
    @Override
    public final EventType getType() {
    	return type;
    }
    
    public boolean getSuccess() {
    	return errorCode == ErrorCode.NO_ERROR;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResponseEvent [errorCode=" + errorCode + ", type=" + type
				+ ", errorMessages=" + errorMessages
				+ "]";
	}
}
