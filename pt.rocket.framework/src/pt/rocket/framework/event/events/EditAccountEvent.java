/**
 * Event triggered when a user changes its password.
 * 
 * @author Nuno Castro
 * 
 * @version 1.01
 * 
 * 2012/08/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.MetaRequestEvent;
import android.content.ContentValues;

/**
 * Event used to edit the customer account
 * @author nunocastro
 *
 */
public class EditAccountEvent extends MetaRequestEvent<ContentValues> {

    private static final EventType type = EventType.EDIT_ACCOUNT_EVENT;

    
    public EditAccountEvent(ContentValues values) {
        super(type, values);
    }
}
