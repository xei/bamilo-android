package com.mobile.view.fragments;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.tracking.gtm.GTMValues;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.BaseActivity;

import de.akquinet.android.androlog.Log;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/04/06
 */
public class MyAddressesSessionLogin extends SessionLoginFragment {

    private static final String TAG = LogTagHelper.create(SessionLoginFragment.class);

    /**
     *
     * @return
     */
    public static MyAddressesSessionLogin getInstance(Bundle bundle) {
        MyAddressesSessionLogin fragment = new MyAddressesSessionLogin();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MyAddressesSessionLogin() {
        super();
    }

    @Override
    protected void onLoginSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT: LOGIN_EVENT");

        BaseActivity baseActivity = getBaseActivity();
        JumiaApplication.INSTANCE.setLoggedIn(true);
        // Get Customer
        baseActivity.hideKeyboard();

        // NullPointerException on orientation change
        if (baseActivity != null && !cameFromRegister) {
            Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.CUSTOMER = customer;

            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
            params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutoLogin);
            params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, false);
            params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
            TrackerDelegator.trackLoginSuccessful(params);

            // Persist user email or empty that value after successfully login
            CustomerPreferences.setRememberedEmail(baseActivity,
                    rememberEmailCheck.isChecked() ? customer.getEmail() : null);
        }

        cameFromRegister = false;
        // Validate the next step
        if(bundle.containsKey(Constants.BUNDLE_NEXT_STEP_KEY) && bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY) instanceof FragmentType){
            FragmentType fragmentType = (FragmentType)bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            FragmentController.getInstance().popLastEntry(FragmentType.MY_ADDRESSES_LOGIN.toString());
            Bundle args = new Bundle();
            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
            if(fragmentType == FragmentType.CREATE_ADDRESS){
                fragmentType = FragmentType.MY_ACCOUNT_CREATE_ADDRESS;
                baseActivity.onSwitchFragment(fragmentType, args, FragmentController.ADD_TO_BACK_STACK);
            } else {
                baseActivity.onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);
            }
        } else if (nextFragmentType != null) {
            Log.d(TAG, "NEXT STEP: " + nextFragmentType.toString());
            FragmentController.getInstance().popLastEntry(FragmentType.MY_ADDRESSES_LOGIN.toString());
            Bundle args = new Bundle();
            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
            baseActivity.onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);

        } else {
            Log.d(TAG, "NEXT STEP: BACK");
            baseActivity.onBackPressed();
        }
    }
}
