package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.errors.ErrorCode;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/02/25
 */
public class MyAccountEditAddressFragment extends EditAddressFragment {

    private static final String TAG = MyAccountEditAddressFragment.class.getSimpleName();

    public MyAccountEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.string.edit_address,
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
        // Hide order summary
        // TODO: 8/28/18 farshid
//        UIUtils.showOrHideViews(View.GONE, view.findViewById(super.ORDER_SUMMARY_CONTAINER));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onClickRetryButton() {

    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        super.onGetEditAddressFormErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        super.onGetRegionsErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        super.onGetCitiesErrorEvent(baseResponse);
        onErrorOccurred();
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        onErrorOccurred();
    }

    private void onErrorOccurred(){
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
        getBaseActivity().onBackPressed();
    }
}
