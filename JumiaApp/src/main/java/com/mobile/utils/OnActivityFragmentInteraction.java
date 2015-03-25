package com.mobile.utils;

import android.os.Bundle;

/**
 * Constructor used to initialize the navigation list component and the autocomplete handler
 * 
 * @param userEvents
 * @author manuelsilva
 * @modified sergiopereira
 * 
 */
public interface OnActivityFragmentInteraction {
    public boolean allowBackPressed();
    public void notifyFragment(Bundle bundle);
}