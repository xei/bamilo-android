package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.forms.ShippingMethodFormBuilder;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepShippingHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepShippingHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.checkout.Fulfillment;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.FulfillmentUiBuilder;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutShippingMethodsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutShippingMethodsFragment.class.getSimpleName();

    private ViewGroup mShippingContainer;

    private ShippingMethodFormBuilder mFormResponse;

    private View mCheckoutTotalBar;

    private Bundle mSavedState;

    /**
     * Empty constructor
     */
    public CheckoutShippingMethodsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_shipping_main,
                R.string.checkout_label,
                NO_ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_SHIPPING);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Validate the saved values
        mSavedState = savedInstanceState;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get container
        mShippingContainer = view.findViewById(R.id.checkout_shipping_methods_container);
        // Get total bar
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
        // Buttons
        mCheckoutTotalBar.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
        // Get and show addresses
        triggerGetShippingMethods();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save state case rotation
        if (mFormResponse != null) {
            mFormResponse.saveState(outState);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        // Save state case next step
        if (mFormResponse != null) {
            mSavedState = new Bundle();
            mFormResponse.saveState(mSavedState);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * Load the dynamic form
     */
    private void loadForm(ShippingMethodFormBuilder form) {
        // Save form response
        mFormResponse = form;
        // Create form layout
        mFormResponse.generateForm(getBaseActivity(), mShippingContainer);
        // Set the saved selection
        mFormResponse.loadSavedState(mSavedState);
    }


    private void loadFulfillment(ArrayList<Fulfillment> fulfillmentList) {
        if(CollectionUtils.isNotEmpty(fulfillmentList)){
            FulfillmentUiBuilder.addToView(this.getActivity(), mShippingContainer, fulfillmentList);
        }
    }
    
    /**
     * ############# CLICK LISTENER #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_button_enter) onClickSubmitShippingMethod();
        // Super
        else super.onClick(view);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        if(null != BamiloApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on submit button
     * @author sergiopereira
     */
    private void onClickSubmitShippingMethod() {
        ContentValues values = mFormResponse.getValues();
        if(CollectionUtils.isNotEmpty(values)){
            triggerSubmitShippingMethod(mFormResponse.action, values);
        }
    }

    /**
     * ############# RESPONSE #############
     */
  
    /**
     * Process the success response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
        case GET_MULTI_STEP_SHIPPING:
            onSuccessGetShippingMethods(baseResponse);
            break;
        case SET_MULTI_STEP_SHIPPING:
            onSuccessSetShippingMethods(baseResponse);
            break;
        default:
            break;
        }
    }

    /**
     * Process the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
        case GET_MULTI_STEP_SHIPPING:
            showFragmentErrorRetry();
            break;
        case SET_MULTI_STEP_SHIPPING:
            showWarningErrorMessage(baseResponse.getValidateMessage());
            showFragmentContentContainer();
            break;
        default:
            break;
        }
    }

    public void onSuccessGetShippingMethods(BaseResponse baseResponse){
        //
        GetStepShippingHelper.ShippingMethodFormStruct shippingMethodsForm = (GetStepShippingHelper.ShippingMethodFormStruct) baseResponse.getContentData();
        // Get order summary
        PurchaseEntity orderSummary = shippingMethodsForm.getOrderSummary();

        // TODO: 8/28/18 farshid
//        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);

        // Form
        loadForm(shippingMethodsForm.getFormBuilder());
        // Set fulfillment
        loadFulfillment(shippingMethodsForm.getFulfillmentList());
        // Set the checkout total bar
        CheckoutStepManager.setTotalBar(mCheckoutTotalBar, orderSummary);
        // Show
        showFragmentContentContainer();
    }

    public void onSuccessSetShippingMethods(BaseResponse baseResponse){
        // Get next step
        NextStepStruct methodStruct = (NextStepStruct) baseResponse.getContentData();
        FragmentType nextFragment = methodStruct.getFragmentType();
        nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.CHECKOUT_PAYMENT;
        getBaseActivity().onSwitchFragment(nextFragment, null, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger to set the shipping method
     * @author sergiopereira
     */
    private void triggerSubmitShippingMethod(String endpoint, ContentValues values) {
        triggerContentEvent(new SetStepShippingHelper(), SetStepShippingHelper.createBundle(endpoint, values), this);
    }
    
    /**
     * Trigger to get the shipping methods
     * @author sergiopereira
     */
    private void triggerGetShippingMethods(){
        triggerContentEvent(new GetStepShippingHelper(), null, this);
    }

}
