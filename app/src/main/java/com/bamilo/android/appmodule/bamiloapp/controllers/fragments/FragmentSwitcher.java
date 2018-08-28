package com.bamilo.android.appmodule.bamiloapp.controllers.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;

import java.util.ArrayList;

/**
 * Class used to build the switch bundle for next fragment
 * @author spereira
 */
public class FragmentSwitcher {

    public final static String TITLE = "title";
    public final static String ID = "id";
    public final static String DATA = "data";
    public final static String ARRAY = "array";

    private final BaseActivity mActivity;
    private final FragmentType mType;
    private Bundle mBundle;
    private boolean addToBackStack = true;

    /**
     * Constructor.
     */
    public FragmentSwitcher(@NonNull BaseActivity activity, @NonNull FragmentType type) {
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
    public FragmentSwitcher addTitle(@NonNull String title) {
        this.bundle().putString(TITLE, title);
        return this;
    }

    /**
     * Add the content id to be loaded.
     */
    public FragmentSwitcher addId(@NonNull String id) {
        this.bundle().putString(ID, id);
        return this;
    }

    /**
     * Add the data to be loaded.
     */
    public FragmentSwitcher addData(@NonNull Parcelable data) {
        this.bundle().putParcelable(DATA, data);
        return this;
    }

    /**
     * Add the array to be loaded.
     */
    public FragmentSwitcher addArray(@NonNull ArrayList<? extends Parcelable> array) {
        this.bundle().putParcelableArrayList(ARRAY, array);
        return this;
    }

    /**
     * Add custom string pair.
     */
    public FragmentSwitcher add(@NonNull String key, @NonNull String param) {
        this.bundle().putString(key, param);
        return this;
    }

    /**
     * Add custom parcelable pair.
     */
    public FragmentSwitcher add(@NonNull String key, @NonNull Parcelable param) {
        this.bundle().putParcelable(key, param);
        return this;
    }

    /**
     * Indicates to not add to back stack.
     */
    public FragmentSwitcher noBackStack() {
        this.addToBackStack = false;
        return this;
    }

    /**
     * Run the switch.
     */
    public FragmentSwitcher run() {
        mActivity.onSwitchFragment(mType, mBundle, addToBackStack);
        return this;
    }

}
