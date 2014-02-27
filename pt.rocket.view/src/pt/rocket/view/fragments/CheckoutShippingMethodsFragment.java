/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.ShippingMethodFormBuilder;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.GetShippingMethodsHelper;
import pt.rocket.helpers.checkout.SetShippingMethodHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.ShippingRadioGroupList;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * FIXME: Waiting for: 
 * > NAFAMZ-5435:  MOBILE API - Add parameter in the Shipping/Payment Methods to get form with success  
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutShippingMethodsFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutShippingMethodsFragment.class);
    
    private static CheckoutShippingMethodsFragment shippingMethodsFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutShippingMethodsFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        shippingMethodsFragment = new CheckoutShippingMethodsFragment();
        return shippingMethodsFragment;
    }

    private ViewGroup shippingMethodsContainer;

    private ViewGroup cartContainer;

    private ShippingMethodFormBuilder formResponse;

    private View formContainer;

    /**
     * Empty constructor
     */
    public CheckoutShippingMethodsFragment() {
        super(EnumSet.of(EventType.GET_SHIPPING_METHODS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.Unknown, 
                BaseActivity.CHECKOUT_SHIPPING);
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
        setRetainInstance(true);
    }
    

    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.checkout_shipping_methods, viewGroup, false);
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
        shippingMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_methods_container);
        cartContainer = (ViewGroup) view.findViewById(R.id.checkout_shipping_cart_container);
        // Buttons
        view.findViewById(R.id.checkout_shipping_button_enter).setOnClickListener(this);
        
        // Get and show addresses
        triggerGetShippingMethods();
                
    }
    
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
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
        formResponse = form;
        shippingMethodsContainer.removeAllViews();
        formContainer = formResponse.generateForm(getActivity());
        shippingMethodsContainer.addView(formContainer);        
        shippingMethodsContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
    }
    
    /**
     * ############# CLICK LISTENER #############
     */
    
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_shipping_button_enter) onClickSubmitShippingMethod();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    
    
    private void onClickSubmitShippingMethod() {
        Log.i(TAG, "ON CLICK: SET SHIPPING METHOD");
        
            ContentValues values = formResponse.getValues();
            if(values != null && values.size() > 0){
                Log.i(TAG, "code1values : "+values.toString());
                JumiaApplication.INSTANCE.setShippingMethod(values);
                triggerSubmitShippingMethod(values);
            }
    }
    
   
    /**
     * ############# RESPONSE #############
     */
  
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
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




    protected boolean onErrorEvent(Bundle bundle) {
    	if(!isVisible()){
    		return true;
    	}
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHIPPING_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_SHIPPING_METHOD_EVENT:
            Log.d(TAG, "RECEIVED SET_SHIPPING_METHOD_EVENT");
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitShippingMethod(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetShippingMethodHelper.FORM_CONTENT_VALUES, values);
        Log.i(TAG, "TRIGGER: SET SHIPPING METHOD");
        triggerContentEvent(new SetShippingMethodHelper(), bundle, this);
    }
    
    private void triggerGetShippingMethods(){
        Log.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEvent(new GetShippingMethodsHelper(), null, this);
    }
    
    /**
     * ########### RESPONSE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
        
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
}
