/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.CheckoutFinishHelper;
import pt.rocket.helpers.checkout.GetShippingMethodsHelper;
import pt.rocket.helpers.checkout.SetShippingMethodHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
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
public class CheckoutFinishFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutFinishFragment.class);
    
    private static CheckoutFinishFragment finishFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutFinishFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        finishFragment = new CheckoutFinishFragment();
        return finishFragment;
    }

    private ViewGroup shippingMethodsContainer;

    private ViewGroup cartContainer;

    /**
     * Empty constructor
     */
    public CheckoutFinishFragment() {
        super(EnumSet.of(EventType.GET_SHIPPING_METHODS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.finish_checkout);
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
        return inflater.inflate(R.layout.checkout_my_addresses, viewGroup, false);
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
        view.findViewById(R.id.checkout_addresses_button_enter).setOnClickListener(this);
        
        // Get and show addresses
        triggerCheckoutFinish();
                
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
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if (null != loginForm) {
//            Iterator<DynamicFormItem> iterator = loginForm.iterator();
//            while (iterator.hasNext()) {
//                DynamicFormItem item = iterator.next();
//                item.saveState(outState);
//            }
//            savedInstanceState = outState;
//        }
//        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
    }
    
    
    
    
    
    /**
     * ############# CLICK LISTENER #############
     */
    
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_addresses_button_enter) onClickSubmitAddressesButton();
        // Add new
        else if(id == R.id.checkout_addresses_button_add) onClickCreateAddressButton(); 
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    
    
    private void onClickSubmitAddressesButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        //triggerSubmitAddresses(null, null);
    }
    
    private void onClickCreateAddressButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        getBaseActivity().onSwitchFragment(FragmentType.CREATE_ADDRESS, null, FragmentController.ADD_TO_BACK_STACK);
    }
    
   
    /**
     * ############# RESPONSE #############
     */
  
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case CHECKOUT_FINISH_EVENT:
            Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
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
        case CHECKOUT_FINISH_EVENT:
            Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
            break;
        default:
            break;
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerCheckoutFinish(){
        Log.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEvent(new CheckoutFinishHelper(), null, this);
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
