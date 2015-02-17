package com.mobile.utils;

import android.view.View.OnClickListener;

import com.mobile.controllers.fragments.FragmentType;


/**
 * Interface to communicate with the activity
 * If another type of fragment is created, add the identifier to {@link FragmentType}
 * @author manuelsilva
 *
 */
public interface OnFragmentActivityInteraction {
    public void onFragmentSelected(FragmentType fragmentIdentifier);
    public void onFragmentElementSelected(int position);
    public void sendClickListenerToActivity(OnClickListener clickListener);
    public void sendValuesToActivity(int identifier, Object values);
}