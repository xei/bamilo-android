/**
 * 
 */
package pt.rocket.framework.event;


/**
 * @author nutzer2
 *
 */
public abstract class MetaRequestEvent<T> extends RequestEvent {
	
	public final T value;
	
	/**
	 * 
	 */
	public MetaRequestEvent(EventType type, T value) {
		super(type);
		this.value = value;
	}

}
