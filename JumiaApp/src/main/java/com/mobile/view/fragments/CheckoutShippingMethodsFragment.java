/**
 * 
 */
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
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.checkout.GetShippingMethodsHelper;
import com.mobile.helpers.checkout.SetShippingMethodHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutShippingMethodsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutShippingMethodsFragment.class);

    private static final String SELECTION_STATE = "selection";

    private static final String SUB_SELECTION_STATE = "sub_selection";
    
    private static CheckoutShippingMethodsFragment shippingMethodsFragment;

    private ViewGroup mShippingMethodsContainer;

    private ShippingMethodFormBuilder mFormResponse;

    private int mSelectionSaved = -1;

    private int mSubSelectionSaved = -1;
    
    /**
     * Get instance
     * @return CheckoutShippingMethodsFragment
     */
    public static CheckoutShippingMethodsFragment getInstance(Bundle bundle) {
        shippingMethodsFragment = new CheckoutShippingMethodsFragment();
        return shippingMethodsFragment;
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
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Validate the saved values 
        if(savedInstanceState != null) {
            mSelectionSaved = savedInstanceState.getInt(SELECTION_STATE, -1);
            mSubSelectionSaved = savedInstanceState.getInt(SUB_SELECTION_STATE, -1);
        }
        else
            Log.i(TAG, "SAVED CONTENT VALUES IS NULL");
        
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_SHIPPING);
        
        TrackerDelegator.trackCheckoutStep(params);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get containers
        mShippingMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_methods_container);
        // Buttons
        view.findViewById(R.id.checkout_shipping_button_enter).setOnClickListener(this);
        
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show addresses
            triggerGetShippingMethods();
        } else {
            showFragmentErrorRetry();
        }

    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
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
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
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
     * 
     * @param form
     */
    private void loadForm(ShippingMethodFormBuilder form) {
        Log.i(TAG, "LOAD FORM");
        mFormResponse = form;
        mShippingMethodsContainer.removeAllViews();
        mFormResponse.generateForm(getBaseActivity(), mShippingMethodsContainer);
        //mShippingMethodsContainer.addView(nFormContainer);
        mShippingMethodsContainer.refreshDrawableState();
        
        // Set the saved selection
        if(mSelectionSaved != -1) mFormResponse.setSelections(0, mSelectionSaved, mSubSelectionSaved);
        
        showFragmentContentContainer();
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
        if(id == R.id.checkout_shipping_button_enter) onClickSubmitShippingMethod();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickErrorButton(View view) {
        super.onClickErrorButton(view);
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
        Log.i(TAG, "ON CLICK: SET SHIPPING METHOD");
        ContentValues values = mFormResponse.getValues();
        if(values != null && values.size() > 0){
            // JumiaApplication.INSTANCE.setShippingMethod(values);
            triggerSubmitShippingMethod(values);
        }
    }
    
   
    /**
     * ############# RESPONSE #############
     */
  
    /**
     * Process the success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            OrderSummary orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
            // Form
            ShippingMethodFormBuilder form = (ShippingMethodFormBuilder) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            loadForm(form);
            break;
        case SET_SHIPPING_METHOD_EVENT:
            Log.d(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.PAYMENT_METHODS;
            // Switch
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            break;
        }
        
        return true;
    }

    /**
     * Process the error response
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.w(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_SHIPPING_METHOD_EVENT:
            Log.w(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED SET_SHIPPING_METHOD_EVENT");
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger to set the shipping method
     * @param values
     * @author sergiopereira
     */
    private void triggerSubmitShippingMethod(ContentValues values) {
        Log.i(TAG, "TRIGGER: SET SHIPPING METHOD");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetShippingMethodHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetShippingMethodHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the shipping methods
     * @author sergiopereira
     */
    private void triggerGetShippingMethods(){
        Log.i(TAG, "TRIGGER: GET SHIPPING METHODS");
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
