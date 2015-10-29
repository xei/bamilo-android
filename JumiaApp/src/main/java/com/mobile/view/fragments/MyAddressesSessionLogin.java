//package com.mobile.view.fragments;
//
//import android.os.Bundle;
//
//import com.mobile.app.JumiaApplication;
//import com.mobile.controllers.fragments.FragmentController;
//import com.mobile.controllers.fragments.FragmentType;
//import com.mobile.helpers.NextStepStruct;
//import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
//import com.mobile.newFramework.objects.customer.Customer;
//import com.mobile.newFramework.pojo.BaseResponse;
//import com.mobile.newFramework.tracking.gtm.GTMValues;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.output.Print;
//import com.mobile.preferences.CustomerPreferences;
//import com.mobile.utils.TrackerDelegator;
//import com.mobile.view.BaseActivity;
//
///**
// * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
// *
// * Unauthorized copying of this file, via any medium is strictly prohibited
// * Proprietary and confidential.
// *
// * @author ricardosoares
// * @version 1.0
// * @date 2015/04/06
// */
//public class MyAddressesSessionLogin extends SessionLoginFragment {
//
//    private static final String TAG = SessionLoginFragment.class.getSimpleName();
//
//    /**
//     *
//     * @return
//     */
//    public static MyAddressesSessionLogin getInstance(Bundle bundle) {
//        MyAddressesSessionLogin fragment = new MyAddressesSessionLogin();
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    public MyAddressesSessionLogin() {
//        super();
//    }
//
//    @Override
//    protected void onLoginSuccessEvent(BaseResponse baseResponse) {
//        Print.d(TAG, "ON SUCCESS EVENT: LOGIN_EVENT");
//
//        BaseActivity baseActivity = getBaseActivity();
//        JumiaApplication.INSTANCE.setLoggedIn(true);
//        // Get Customer
//        baseActivity.hideKeyboard();
//        NextStepStruct nextStepStruct = (NextStepStruct)baseResponse.getMetadata().getData();
//
//        // NullPointerException on orientation change
//        if (baseActivity != null && !cameFromRegister) {
//            Customer customer = ((CheckoutStepLogin)nextStepStruct.getCheckoutStepObject()).getCustomer();
//            JumiaApplication.CUSTOMER = customer;
//
//            Bundle params = new Bundle();
//            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
//            params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutoLogin);
//            params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, false);
//            params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
//            TrackerDelegator.trackLoginSuccessful(params);
//
//            // Persist user email or empty that value after successfully login
//            CustomerPreferences.setRememberedEmail(baseActivity,
//                    rememberEmailCheck.isChecked() ? customer.getEmail() : null);
//        }
//
//        cameFromRegister = false;
//        // Validate the next step
//        FragmentType fragmentType = nextStepStruct.getFragmentType();
//        if(fragmentType != null){
//            FragmentController.getInstance().popLastEntry(FragmentType.MY_ADDRESSES_LOGIN.toString());
//            Bundle args = new Bundle();
//            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
//            if(fragmentType == FragmentType.CREATE_ADDRESS){
//                fragmentType = FragmentType.MY_ACCOUNT_CREATE_ADDRESS;
//                baseActivity.onSwitchFragment(fragmentType, args, FragmentController.ADD_TO_BACK_STACK);
//            } else {
//                baseActivity.onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);
//            }
//        } else if (nextFragmentType != null) {
//            Print.d(TAG, "NEXT STEP: " + nextFragmentType.toString());
//            FragmentController.getInstance().popLastEntry(FragmentType.MY_ADDRESSES_LOGIN.toString());
//            Bundle args = new Bundle();
//            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
//            baseActivity.onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);
//
//        } else {
//            Print.d(TAG, "NEXT STEP: BACK");
//            baseActivity.onBackPressed();
//        }
//    }
//}
