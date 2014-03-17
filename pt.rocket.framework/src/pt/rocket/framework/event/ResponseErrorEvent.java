/**
 * 
 */
package pt.rocket.framework.event;

import java.util.List;
import java.util.Map;


import pt.rocket.framework.ErrorCode;

/**
 * Response Event error version.
 * This events are triggered when some error has occurred and the request listener has to return an error to the response listener
 * @author nutzer2
 * 
 */
public class ResponseErrorEvent extends ResponseEvent {

	/**
	 * Base constructor
	 * @param request original request event that was sent from the activity
	 * @param errorCode error code of the response
	 */
	public ResponseErrorEvent(RequestEvent request, ErrorCode errorCode) {
		super(request, errorCode, null);
	}

	/**
	 * Base constructor
	 * @param request original request event that was sent from the activity
	 * @param errorMessages error messages of the error
	 */
	public ResponseErrorEvent(RequestEvent request,
			Map<String, ? extends List<String>> errorMessages) {
		super(request, ErrorCode.REQUEST_ERROR, errorMessages, null);
	}

}
