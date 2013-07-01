/**
 * 
 */
package pt.rocket.framework.event;

import android.os.Bundle;
import pt.rocket.framework.ErrorCode;

/**
 * @author nutzer2
 * 
 */
public class ResponseResultEvent<T> extends ResponseEvent implements IMetaData {

	public final T result;
	
	public final Bundle metaData;

	/**
	 * @param type
	 */
	public ResponseResultEvent(RequestEvent request, T result, String warning, Bundle metaData) {
		super(request, warning);
		this.result = result;
		this.metaData = metaData;
	}
	
	protected ResponseResultEvent(RequestEvent request, ErrorCode errorCode, T result, Bundle metaData) {
		super(request, errorCode, null);
		this.result = result;
		this.metaData = metaData;
	}

}
