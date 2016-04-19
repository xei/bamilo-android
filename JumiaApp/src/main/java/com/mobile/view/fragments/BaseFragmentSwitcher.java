package com.mobile.view.fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;

import java.util.Set;

/**
 * Base switcher fragment used to manage the switch between fragments and the respective arguments.
 * @author spereira
 */
public abstract class BaseFragmentSwitcher extends BaseFragment {

    protected String mTitle;
    protected String mContentId;

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
        mTitle = bundle.getString(UISwitcher.TITLE);
        mContentId = bundle.getString(UISwitcher.CONTENT_ID);
    }

    /**
     * Saves:<br>
     *     - title<br>
     *     - content id<br>
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UISwitcher.TITLE, mTitle);
        outState.putString(UISwitcher.CONTENT_ID, mContentId);
    }

    /*
     * ##### UI SWITCHER #####
     */

    /**
     * Class used to build the switch bundle for next fragment
     */
    protected final static class UISwitcher {

        private final static String TITLE = "title";
        private final static String CONTENT_ID = "content_id";

        private final BaseActivity mActivity;
        private final FragmentType mType;
        private Bundle mBundle;
        private boolean addToBackStack = true;

        /**
         * Constructor.
         */
        protected UISwitcher(@NonNull BaseActivity activity, @NonNull FragmentType type) {
            this.mActivity = activity;
            this.mType = type;
        }

        private Bundle bundle() {
            return this.mBundle == null ? this.mBundle = new Bundle() : this.mBundle;
        }

        /*
         * ###### BUILDER #####
         */

        /**
         * Add the title.<br>
         */
        protected UISwitcher addTitle(@NonNull String title) {
            this.bundle().putString(TITLE, title);
            return this;
        }

        /**
         * Add the content id to be loaded.
         */
        protected UISwitcher addContentId(@NonNull String id) {
            this.bundle().putString(CONTENT_ID, id);
            return this;
        }

        /**
         * Add custom string pair.
         */
        protected UISwitcher add(@NonNull String key, @NonNull String param) {
            this.bundle().putString(key, param);
            return this;
        }

        /**
         * Add custom parcelable pair.
         */
        protected UISwitcher add(@NonNull String key, @NonNull Parcelable param) {
            this.bundle().putParcelable(key, param);
            return this;
        }

        /**
         * Indicates to not add to back stack.
         */
        protected UISwitcher noBackStack() {
            this.addToBackStack = false;
            return this;
        }

        /**
         * Run the switch.
         */
        protected UISwitcher run() {
            mActivity.onSwitchFragment(mType, mBundle, addToBackStack);
            return this;
        }

    }

}
