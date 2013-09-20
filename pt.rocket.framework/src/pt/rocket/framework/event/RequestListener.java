/**
 * 
 */
package pt.rocket.framework.event;

/**
 * Listener for request events
 * (ususally Darwin Services)
 * @author nutzer2
 *
 */
public abstract class RequestListener implements EventListener<RequestEvent> {
	
	private final boolean removeAfterHandlingEvent;
	
	/**
	 * Base constructor
	 */
	public RequestListener(boolean removeAfterHandlingEvent) {
		this.removeAfterHandlingEvent = removeAfterHandlingEvent;
	}
	
	/* (non-Javadoc)
	 * @see pt.rocket.framework.event.EventListener#removeAfterHandlingEvent()
	 */
	@Override
	public boolean removeAfterHandlingEvent() {
		return removeAfterHandlingEvent;
	}

}
