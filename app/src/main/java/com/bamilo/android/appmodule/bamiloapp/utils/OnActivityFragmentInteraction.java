package com.bamilo.android.appmodule.bamiloapp.utils;

import android.os.Bundle;

/**
 * Constructor used to initialize the navigation list component and the autocomplete handler
 * 
 * @author manuelsilva
 * @modified sergiopereira
 * 
 */
public interface OnActivityFragmentInteraction {
    boolean allowBackPressed();
    void notifyFragment(Bundle bundle);
}