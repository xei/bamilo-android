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
import com.mobile.helpers.checkout.GetShippingMethodsHelper;
import com.mobile.helpers.checkout.SetShippingMethodHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.Fulfillment;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
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

    private static final String SELECTION_STATE = "selection";

    private static final String SUB_SELECTION_STATE = "sub_selection";

    private ViewGroup mShippingContainer;

    private ViewGroup mShippingMethodsContainer;

    private ShippingMethodFormBuilder mFormResponse;

    private int mSelectionSaved = -1;

    private int mSubSelectionSaved = -1;
    
    /**
     * Get instance
     * @return CheckoutShippingMethodsFragment
     */
    public static CheckoutShippingMethodsFragment getInstance() {
        return new CheckoutShippingMethodsFragment();
    }

    /**
     * Empty constructor
     */
    public CheckoutShippingMethodsFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_shipping_main,
                R.string.checkout_label,
                KeyboardState.NO_ADJUST_CONTENT,
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
        if(savedInstanceState != null) {
            mSelectionSaved = savedInstanceState.getInt(SELECTION_STATE, -1);
            mSubSelectionSaved = savedInstanceState.getInt(SUB_SELECTION_STATE, -1);
        } else{
            Print.i(TAG, "SAVED CONTENT VALUES IS NULL");
        }

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
        // Get containers
        mShippingContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_container);
        mShippingMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_methods_container);
        // Buttons
        view.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
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
        if(mFormResponse == null) 
            return;
        int itemId = mFormResponse.getSelectionId(0);
        if (itemId != -1)
            outState.putInt(SELECTION_STATE, itemId);
        int subItemId = mFormResponse.getSubSelectionId(0, itemId);
        if (itemId != -1 && subItemId != -1)
            outState.putInt(SUB_SELECTION_STATE, subItemId);
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
        mFormResponse = form;
        mShippingMethodsContainer.removeAllViews();
        mFormResponse.generateForm(getBaseActivity(), mShippingMethodsContainer);
        //mShippingMethodsContainer.addView(nFormContainer);
        mShippingMethodsContainer.refreshDrawableState();
//        mShippingContainer.addView(FulfillmentUiBuilder.getView(this.getActivity(),));
        // Set the saved selection
        if(mSelectionSaved != -1) mFormResponse.setSelections(0, mSelectionSaved, mSubSelectionSaved);
        
        showFragmentContentContainer();
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
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_button_enter) onClickSubmitShippingMethod();
        // Unknown view
        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }
    
    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
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
        if(values != null && values.size() > 0){
            triggerSubmitShippingMethod(values);
        }
    }

    /**
     * ############# RESPONSE #############
     */
  
    /**
     * Process the success response
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            onSuccessGetShippingMethods(bundle);
            break;
        case SET_SHIPPING_METHOD_EVENT:
            onSuccessSetShippingMethods(bundle);
            break;
        default:
            break;
        }
        
        return true;
    }

    /**
     * Process the error response
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        //ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        //Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            onErrorGetShippingMethods();
            break;
        case SET_SHIPPING_METHOD_EVENT:
            onErrorSetShippingMethods();
            break;
        default:
            break;
        }
        
        return false;
    }

    public void onSuccessGetShippingMethods(Bundle bundle){
        Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
        // Get order summary
        PurchaseEntity orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
        // Form
        ShippingMethodFormBuilder form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        loadForm(form);
        ArrayList<Fulfillment> fulfillmentList = bundle.getParcelableArrayList(Constants.BUNDLE_FORM_DATA_KEY);
        loadFulfillment(fulfillmentList);



        //Total price
        CheckoutStepManager.showCheckoutTotal(getView().findViewById(R.id.total_view_stub), orderSummary);
    }

    public void onSuccessSetShippingMethods(Bundle bundle){
        Print.i(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
        // Get next step
        FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
        nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.PAYMENT_METHODS;
        getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    public void onErrorGetShippingMethods(){
        Print.w(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
        super.showFragmentErrorRetry();

    }

    public void onErrorSetShippingMethods(){
        Print.w(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
        super.showUnexpectedErrorWarning();
    }

    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger to set the shipping method
     * @author sergiopereira
     */
    private void triggerSubmitShippingMethod(ContentValues values) {
        Print.i(TAG, "TRIGGER: SET SHIPPING METHOD");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new SetShippingMethodHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the shipping methods
     * @author sergiopereira
     */
    private void triggerGetShippingMethods(){
        Print.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEvent(new GetShippingMethodsHelper(), null, this);
    }
    
    /**
     * ########### RESPONSE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
     
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
}
