/**
 * 
 */
package pt.rocket.framework.event;

import android.os.Bundle;
import pt.rocket.framework.ErrorCode;

/**
 * Response event that takes in consideration the type of data from the request
 * For example in the get products request, this result will return an array of products
 * @author nutzer2
 * 
 */
public class ResponseResultEvent<T> extends ResponseEvent implements IMetaData {

	/**
	 * Type of the result we are awating
	 */
	public final T result;
	
	public final Bundle metaData;

	/**
	 * Base constructor
	 * @param request the original request event
	 * @param result the data that the listener is awaiting
	 * @param warning warning message
	 * @param metaData metadata from the response
	 */
	public ResponseResultEvent(RequestEvent request, T result, String warning, Bundle metaData) {
		super(request, warning);
		this.result = result;
		this.metaData = metaData;
	}
	
	/**
	 * Base constructor
	 * @param request
	 * @param errorCode
	 * @param result
	 * @param metaData
	 */
	protected ResponseResultEvent(RequestEvent request, ErrorCode errorCode, T result, Bundle metaData) {
		super(request, errorCode, null);
		this.result = result;
		this.metaData = metaData;
	}

}
