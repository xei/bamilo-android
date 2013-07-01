/**
 * 
 */
package pt.rocket.framework.event;

/**
 * @author nutzer2
 *
 */
public interface EventListener<T extends IEvent> {
	
	/**
	 * 
	 * @param event
	 */
	public void handleEvent(T event);
	
	public boolean removeAfterHandlingEvent();

}
