/**
 * IEvent.java
 * Event interface, contains the method getType(). It is also promoted that any class that inherits from IEvent provides the static function .getEventType(9 that returns the same value.
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

/**
 * 
 * Event interface, implements the method getType();
 * 
 * @author GuilhermeSilva
 * 
 */
public interface IEvent {
	/**
	 * Returns the type of the event.
	 * 
	 * @return Type of the event.
	 */
	public EventType getType();
}
