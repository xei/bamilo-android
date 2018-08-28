package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.AnalyticsGoogle;
import com.bamilo.android.framework.service.tracking.TrackingEvent;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/24
 */
public class MyAccountCreateAddressFragment extends CreateAddressFragment {

    private static final String TAG = MyAccountCreateAddressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public MyAccountCreateAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.string.my_profile_add_new_address,
                ADJUST_CONTENT,
                ConstantsCheckout.NO_CHECKOUT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideActivityProgress();
        // Set total bar
        if (view.findViewById(R.id.checkout_total_label) != null) {
            CheckoutStepManager.setTotalBarWithFullButton(view);
        }
        // Hide order summary
        // TODO: 8/28/18 farshid
//        UIUtils.showOrHideViews(View.GONE, view.findViewById(super.ORDER_SUMMARY_CONTAINER));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onClickRetryButton() {
        triggerGetRegions(getRegionApi);
    }

    @Override
    protected void onCreateAddressSuccessEvent(BaseResponse baseResponse) {
        super.onCreateAddressSuccessEvent(baseResponse);
        AnalyticsGoogle.get().trackAddressCreation(TrackingEvent.ACCOUNT_CREATE_ADDRESS,
                (BamiloApplication.CUSTOMER != null) ? BamiloApplication.CUSTOMER.getIdAsString() + "" : ""); //replaced getID because doesn't come from api

        FragmentController.getInstance().popLastEntry(FragmentType.MY_ACCOUNT_CREATE_ADDRESS.toString());
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);

        showWarningSuccessMessage(baseResponse.getSuccessMessage(), R.string.create_addresses_success);
    }

    @Override
    protected void onGetCreateAddressFormErrorEvent(BaseResponse baseResponse) {
        super.onGetCreateAddressFormErrorEvent(baseResponse);
        onErrorOccurred();
    }

    @Override
    protected void onGetRegionsErrorEvent(BaseResponse baseResponse) {
        super.onGetRegionsErrorEvent(baseResponse);
        onErrorOccurred();
    }

    @Override
    protected void onGetCitiesErrorEvent(BaseResponse baseResponse) {
        super.onGetCitiesErrorEvent(baseResponse);
        onErrorOccurred();

    }

    @Override
    protected void onCreateAddressErrorEvent(BaseResponse baseResponse) {
        super.onCreateAddressErrorEvent(baseResponse);
    }

    private void onErrorOccurred() {
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
        getBaseActivity().onBackPressed();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
