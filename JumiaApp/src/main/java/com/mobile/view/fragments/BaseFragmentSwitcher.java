package com.mobile.view.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentSwitcher;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;

import java.util.Set;

/**
 * Base switcher fragment used to manage the switch between fragments and the respective arguments.
 * @author spereira
 */
public abstract class BaseFragmentSwitcher extends BaseFragment {

    protected String mTitle;
    protected String mId;
    protected Parcelable mData;

    /*
     * ##### CONSTRUCTORS #####
     */

    public BaseFragmentSwitcher(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState);
    }

    public BaseFragmentSwitcher(Boolean isNestedFragment, @LayoutRes int layoutResId) {
        super(isNestedFragment, layoutResId);
    }

    public BaseFragmentSwitcher(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState, titleCheckout);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get static page key from arguments
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        if (bundle != null) {
            onCreateInstanceState(bundle);
        }
    }

    /**
     * Method used to create variables from savedInstanceState or getArguments()
     * Creates:
     *     - title<br>
     *     - content id<br>
     */
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        mTitle = bundle.getString(FragmentSwitcher.TITLE);
        mId = bundle.getString(FragmentSwitcher.ID);
        mData = bundle.getParcelable(FragmentSwitcher.DATA);
    }

    /**
     * Saves:<br>
     *     - title<br>
     *     - content id<br>
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FragmentSwitcher.TITLE, mTitle);
        outState.putString(FragmentSwitcher.ID, mId);
        outState.putParcelable(FragmentSwitcher.DATA, mData);
    }

    /*
     * ##### UI SWITCHER #####
     */

    /**
     * Get the ui switcher
     */
    protected FragmentSwitcher onSwitchTo(@NonNull FragmentType type) {
        return new FragmentSwitcher(getBaseActivity(), type);
    }

}
