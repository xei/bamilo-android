/**
 * 
 */
package pt.rocket.framework.event;


/**
 * Class the represents a request with a specifyied metadata type
 * @author nutzer2
 *
 */
public abstract class MetaRequestEvent<T> extends RequestEvent {
	
	public final T value;
	
	/**
	 * Basic constructor without value
	 */
	public MetaRequestEvent(EventType type) {
		super(type);
		value = null;
	}
	
	/**
	 * Value constructor
	 */
	public MetaRequestEvent(EventType type, T value) {
		super(type);
		this.value = value;
	}

}
