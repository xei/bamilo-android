/**
 * 
 */
package pt.rocket.framework.event;

import java.util.List;
import java.util.Map;


import pt.rocket.framework.ErrorCode;

/**
 * @author nutzer2
 * 
 */
public class ResponseErrorEvent extends ResponseEvent {

	/**
	 * @param errorCode
	 * @param type
	 */
	public ResponseErrorEvent(RequestEvent request, ErrorCode errorCode) {
		super(request, errorCode, null);
	}

	/**
	 * 
	 */
	public ResponseErrorEvent(RequestEvent request,
			Map<String, ? extends List<String>> errorMessages) {
		super(request, ErrorCode.REQUEST_ERROR, errorMessages, null);
	}

}
