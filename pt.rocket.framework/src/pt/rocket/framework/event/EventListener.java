/**
 * 
 */
package pt.rocket.framework.event;

/**
 * Interface that defines the listener for an event
 * @author nutzer2
 *
 */
public interface EventListener<T extends IEvent> {
	
	/**
	 * Main function used to handle an event.
	 * As soon as the event is triggered by the eventmanager this class is called.
	 * This class should check the event type and then use it when necessary
	 * @param event
	 */
	public void handleEvent(T event);
	
	/**
	 * Determines if the class should be removed from the event listeners registry after handling an event.
	 * @return true if the class should be removed or false if otherwise.
	 */
	public boolean removeAfterHandlingEvent();

}
