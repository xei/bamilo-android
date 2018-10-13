package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutEditAddressFragment extends EditAddressFragment {

    private static final String TAG = CheckoutEditAddressFragment.class.getSimpleName();

    /**
     * Empty constructor
     */
    public CheckoutEditAddressFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.string.checkout_edit_address,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO: 8/28/18 farshid
//        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
    }

    @Override
    protected void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if (null == BamiloApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // TODO: 11/21/2017 IMPLEMENT
    }

    protected void onGetEditAddressFormErrorEvent(BaseResponse baseResponse){
        super.onGetEditAddressFormErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onGetRegionsErrorEvent(BaseResponse baseResponse){
        super.onGetRegionsErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onGetCitiesErrorEvent(BaseResponse baseResponse){
        super.onGetCitiesErrorEvent(baseResponse);
        super.showFragmentErrorRetry();
    }

    protected void onEditAddressErrorEvent(BaseResponse baseResponse){
        super.onEditAddressErrorEvent(baseResponse);
        showFragmentContentContainer();
    }
}
