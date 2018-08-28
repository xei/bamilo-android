package com.bamilo.android.appmodule.bamiloapp.view.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentSwitcher;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;

import java.util.ArrayList;
import java.util.Set;

/**
 * Base fragment used to manage the switch between fragments and the respective arguments.
 * @author spereira
 */
public abstract class BaseFragmentAutoState extends BaseFragment {

    protected String mArgTitle;
    protected String mArgId;
    protected Parcelable mArgData;
    protected ArrayList<? extends Parcelable> mArgArray;
    protected Bundle mSavedState;

    /*
     * ##### CONSTRUCTORS #####
     */

    public BaseFragmentAutoState(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState);
    }

    public BaseFragmentAutoState(Boolean isNestedFragment, @LayoutRes int layoutResId) {
        super(isNestedFragment, layoutResId);
    }

    public BaseFragmentAutoState(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState, titleCheckout);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Save saved state for forms
        mSavedState = savedInstanceState;
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
     *     - parcelable object<br>
     *     - array list of parcelables<br>
     */
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        mArgTitle = bundle.getString(FragmentSwitcher.TITLE);
        mArgId = bundle.getString(FragmentSwitcher.ID);
        mArgData = bundle.getParcelable(FragmentSwitcher.DATA);
        mArgArray = bundle.getParcelableArrayList(FragmentSwitcher.ARRAY);
    }

    /**
     * Saves:<br>
     *     - title<br>
     *     - content id<br>
     *     - parcelable object<br>
     *     - array list of parcelables<br>
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FragmentSwitcher.TITLE, mArgTitle);
        outState.putString(FragmentSwitcher.ID, mArgId);
        outState.putParcelable(FragmentSwitcher.DATA, mArgData);
        outState.putParcelableArrayList(FragmentSwitcher.ARRAY, mArgArray);
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
