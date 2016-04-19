package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.forms.ShippingMethodFormBuilder;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.checkout.GetStepShippingHelper;
import com.mobile.helpers.checkout.SetStepShippingHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.Fulfillment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.FulfillmentUiBuilder;
import com.mobile.view.R;

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
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Validate the saved values
        mSavedState = savedInstanceState;
        // Tracking
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_SHIPPING);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get container
        mShippingContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_methods_container);
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
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
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
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
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
        Print.i(TAG, "LOAD FORM");
        // Save form response
        mFormResponse = form;
        // Create form layout
        mFormResponse.generateForm(getBaseActivity(), mShippingContainer);
        // Set the saved selection
        mFormResponse.loadSavedState(mSavedState);
    }


    private void loadFulfillment(ArrayList<Fulfillment> fulfillmentList) {
        Print.i(TAG, "LOAD FULFILLMENT");
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
        if(null != JumiaApplication.CUSTOMER){
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
        Print.i(TAG, "ON CLICK: SET SHIPPING METHOD");
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
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
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
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return;
        }
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
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
        Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
        //
        GetStepShippingHelper.ShippingMethodFormStruct shippingMethodsForm = (GetStepShippingHelper.ShippingMethodFormStruct) baseResponse.getContentData();
        // Get order summary
        PurchaseEntity orderSummary = shippingMethodsForm.getOrderSummary();
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
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
        Print.i(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
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
        Print.i(TAG, "TRIGGER: SET SHIPPING METHOD");
        triggerContentEvent(new SetStepShippingHelper(), SetStepShippingHelper.createBundle(endpoint, values), this);
    }
    
    /**
     * Trigger to get the shipping methods
     * @author sergiopereira
     */
    private void triggerGetShippingMethods(){
        Print.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEvent(new GetStepShippingHelper(), null, this);
    }

}
