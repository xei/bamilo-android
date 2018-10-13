package com.bamilo.android.appmodule.bamiloapp.view.fragments.order;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.framework.service.objects.orders.OrderTrackerItem;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UITabLayoutUtils;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragmentAutoState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Fragment used to show the online returns reason.
 * @author spereira
 */
public class OrderReturnStepsMain extends BaseFragmentAutoState {

    public static final int CONDITIONS = -1;
    public static final int REASON = 0;
    public static final int METHOD = 1;
    public static final int REFUND = 2;
    public static final int FINISH = 3;
    @IntDef({CONDITIONS, REASON, METHOD, REFUND, FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReturnStepType {}

    private int mStep = REASON;
    private Bundle mSubmittedData;
    private ArrayList<SavedState> mSavedState = new ArrayList<>(IntConstants.TAB_MAX_STEPS);
    private TabLayout mTabLayout;

    /**
     * Empty constructor
     */
    public OrderReturnStepsMain() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.MY_ORDERS,
                R.layout._def_order_return_step_main,
                R.string.my_orders_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * ##### LIFECYCLE #####
     */

    @Override
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        super.onCreateInstanceState(bundle);
        // Get submitted data
        mSubmittedData = bundle.getBundle(ConstantsIntentExtra.ARG_1);
        // Get current step
        mStep = bundle.getInt(ConstantsIntentExtra.ARG_2, REASON);
        // Get fragment states
        ArrayList<SavedState> state = bundle.getParcelableArrayList(ConstantsIntentExtra.ARG_3);
        if (CollectionUtils.isNotEmpty(state)) {
            mSavedState = state;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set tab
        mTabLayout = view.findViewById(R.id.order_return_main_tabs);
        // Set tabs
        UITabLayoutUtils.fillReturnTabLayout(mTabLayout, this);
        // Validate state
        if (CollectionUtils.isEmpty(this.mArgArray)) {
            showFragmentErrorRetry();
        }
        // Case step
        else if (!hasChildFragmentAttached(mStep)) {
            onSwitchStep(mStep);
        }
        // Has child fragment attached
        else {
            UITabLayoutUtils.setSelectedTab(mTabLayout, mStep);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save data
        outState.putBundle(ConstantsIntentExtra.ARG_1, mSubmittedData);
        // Save step
        outState.putInt(ConstantsIntentExtra.ARG_2, mStep);
        // Save state
        if (CollectionUtils.isNotEmpty(mSavedState)) {
            outState.putParcelableArrayList(ConstantsIntentExtra.ARG_3, mSavedState);
        }
    }

    /*
     * ##### STEP VALUES #####
     */

    /**
     * Get return items
     */
    @SuppressWarnings("all")
    public ArrayList<OrderTrackerItem> getReturnItems() {
        return (ArrayList<OrderTrackerItem>) this.mArgArray;
    }

    /**
     * Get order number
     */
    public String getOrderNumber() {
        return this.mArgId;
    }

    /**
     * Validate submitted values
     */
    public boolean hasSubmittedValuesToFinish() {
        return mSubmittedData != null &&
                mSubmittedData.containsKey(String.valueOf(REASON)) &&
                mSubmittedData.containsKey(String.valueOf(METHOD)) &&
                mSubmittedData.containsKey(String.valueOf(REFUND));
    }

    /**
     * Save submitted values
     */
    public synchronized void saveSubmittedValuesFromStep(@ReturnStepType int mStep, @NonNull ContentValues values) {
        if (mSubmittedData == null) {
            mSubmittedData = new Bundle();
        }
        mSubmittedData.putParcelable(String.valueOf(mStep), values);
    }

    /**
     * Get submitted values
     */
    public ContentValues getSubmittedValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.putAll(getSubmittedValuesForStep(REASON));
        contentValues.putAll(getSubmittedValuesForStep(METHOD));
        contentValues.putAll(getSubmittedValuesForStep(REFUND));
        return contentValues;
    }

    /**
     * Get submitted values for step
     */
    public ContentValues getSubmittedValuesForStep(@ReturnStepType int mStep) {
        return mSubmittedData != null ? (ContentValues) mSubmittedData.getParcelable(String.valueOf(mStep)) : null;
    }

    /*
     * ##### LISTENERS #####
     */

    @Override
    public void onClick(View view) {
        // Get tag from view
        int tStep = (int) view.getTag();
        // Validate
        if(tStep < mStep || hasSubmittedValuesToFinish()) {
            onSwitchStep(tStep);
        }
        // Super
        else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        // Restart
        showUnexpectedErrorWarning();
        getBaseActivity().onBackPressed();
    }

    /*
     * ##### BACK #####
     */

    @Override
    public boolean allowBackPressed() {
        // Validate step
        if(mStep > REASON) {
            onSwitchStep(mStep -1);
            return true;
        } else {
            return super.allowBackPressed();
        }
    }

    /*
     * ##### SWITCH #####
     */

    /**
     * Goto next/back step.<br>
     *  - On replacement, the save fragment state is performed in our side, because is not performed by child fragment manager.<br>
     *  The save state was based in the FragmentStatePagerAdapter implementation<br>
     *  - On rotation, the fragment state is performed by child fragment manager.<br>
     * @author sergio pereira
     */
    public synchronized void onSwitchStep(int next) {
        // Animation, first time and others
        int aType = next > mStep ? FragmentController.SLIDE_IN : FragmentController.SLIDE_OUT;
        if (next == mStep && next == REASON) {
            aType = FragmentController.FADE_IN_SLIDE_TO_LEFT;
        }
        // Save state
        saveFragmentInstantState(mStep, mSavedState);
        // Save next
        mStep = next;
        // Create instance
        Fragment fragment = createChildFragment(mStep, mSavedState);
        // Replace
        new FragmentController.Transaction(this, R.id.order_return_main_container, fragment)
                .useChildFragmentManager()
                .addTag(String.valueOf(mStep))
                .addAnimation(aType, ShopSelector.isRtl())
                .allowStateLoss()
                .commit();
        // Update tab
        UITabLayoutUtils.setSelectedTab(mTabLayout, mStep);
    }

    /**
     * Create child fragment for respective step loading the saved state.
     */
    public Fragment createChildFragment(int position, @NonNull ArrayList<SavedState> states) {
        Fragment fragment = newStepInstance(position);
        if (states.size() > position) {
            Fragment.SavedState state = states.get(position);
            if (state != null) {
                fragment.setInitialSavedState(state);
            }
        }
        return fragment;
    }

    /**
     * Save the current state
     */
    public void saveFragmentInstantState(int position, @NonNull ArrayList<SavedState> states) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(String.valueOf(position));
        if (fragment != null) {
            SavedState state = getChildFragmentManager().saveFragmentInstanceState(fragment);
            if (position == CollectionUtils.size(states)) {
                states.add(position, state);
            } else {
                states.set(position, state);
            }
        }
    }

    /**
     * Create step
     */
    @SuppressLint("SwitchIntDef")
    public Fragment newStepInstance(@ReturnStepType int position) {
        Class<? extends BaseFragment> fClass;
        switch (position) {
            case REASON:
                fClass = OrderReturnStep1Reason.class;
                break;
            case METHOD:
                fClass = OrderReturnStep2Method.class;
                break;
            case REFUND:
                fClass = OrderReturnStep3Refund.class;
                break;
            case FINISH:
            default:
                fClass = OrderReturnStep4Finish.class;
                break;
        }
        return newInstance(getContext(), fClass, null);
    }

    /**
     * Validate if parent has child fragment attached
     */
    private boolean hasChildFragmentAttached(int step) {
        return getChildFragmentManager().findFragmentByTag(String.valueOf(step)) != null;
    }

}
