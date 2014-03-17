/**
 * 
 */
package pt.rocket.view;

import pt.rocket.utils.PreferenceListFragment;

/**
 * @author nutzer2
 * 
 */
public class PushPreferences extends PreferenceListFragment {
    
    /**
     * 
     */
    public PushPreferences() {
        super(R.xml.preferences_push);
        this.setRetainInstance(true);
    }
    
}
