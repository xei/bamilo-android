package pt.rocket.utils;

import android.view.View.OnClickListener;

/**
 * Constructor used to initialize the navigation list component and the autocomplete handler
 * 
 * @param userEvents
 * @author manuelsilva
 * 
 */
public interface OnActivityFragmentInteraction {
    public void sendValuesToFragment(int identifier, Object values);
    public void sendPositionToFragment(int position);
    public void sendListener(int identifier, OnClickListener clickListener);
    public boolean allowBackPressed();
}