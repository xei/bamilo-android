/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Addresses;
import pt.rocket.framework.objects.Addresses.Address;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.CreateAddressHelper;
import pt.rocket.helpers.GetCustomerAddressesHelper;
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
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutCustomerAddressesFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutCustomerAddressesFragment.class);
    
    private static CheckoutCustomerAddressesFragment customerAddressesFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutCustomerAddressesFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        customerAddressesFragment = new CheckoutCustomerAddressesFragment();
        return customerAddressesFragment;
    }

    private ViewGroup defaultContainer;

    private ViewGroup otherContainer;

    private Addresses addresses;

    /**
     * Empty constructor
     */
    public CheckoutCustomerAddressesFragment() {
        super(EnumSet.of(EventType.GET_CUSTOMER_ADDRESSES_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.my_addresses);
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
        return inflater.inflate(R.layout.checkout_customer_addresses, viewGroup, false);
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
        defaultContainer = (ViewGroup) view.findViewById(R.id.checkout_addresses_default_container);
        otherContainer = (ViewGroup) view.findViewById(R.id.checkout_addresses_other_container);
        // Buttons
        view.findViewById(R.id.checkout_addresses_button_enter).setOnClickListener(this);
        view.findViewById(R.id.checkout_addresses_button_add).setOnClickListener(this);
        
        // Get and show addresses
        triggerGetAddresses();
                
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
        else {
            Log.i(TAG, "ON CLICK: UNKNOWN VIEW " + view.getTag());
            
            String otherAddressId = view.getTag().toString();
            Address selectedAddress = addresses.getAddresses().get(otherAddressId);
            Bundle bundle = new Bundle();
            bundle.putParcelable(CheckoutEditAddressFragment.SELECTED_ADDRESS, selectedAddress);
            getBaseActivity().onSwitchFragment(FragmentType.EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }
    
    
    
    private void onClickSubmitAddressesButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        //triggerSubmitAddresses(null, null);
        getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, null, FragmentController.ADD_TO_BACK_STACK);
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
        
        if(eventType == EventType.GET_CUSTOMER_ADDRESSES_EVENT) {
            Log.d(TAG, "RECEIVED GET_CREATE_ADDRESS_FORM_EVENT");
            // Save and load form
            Addresses addresses = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showAddresses(addresses);
        } else if(eventType == EventType.CREATE_ADDRESS_EVENT) {
            Log.d(TAG, "RECEIVED SEND_BILLING_ADDRESS_EVENT");
        }
        
        return true;
    }

    /**
     * Load the dynamic form
     * 
     * @param addresses
     */
    private void showAddresses(Addresses addresses) {
        Log.i(TAG, "SHOW ADDRESSES");
        
        this.addresses = addresses;
        
        // Show billing addresses
        Address billingAddress = addresses.getBillingAddress();
        Log.d(TAG, "BILLING ADDRESS: " + billingAddress.getAddress());
        TextView text1 = new TextView(getBaseActivity());
        text1.setText("" + billingAddress.getAddress());
        defaultContainer.addView(text1);
        
        // Show shipping addresses
        Address shippingAddress = addresses.getShippingAddress();
        Log.d(TAG, "SHIPPING ADDRESS: " + shippingAddress.getAddress());
        TextView text2 = new TextView(getBaseActivity());
        text2.setText("" + shippingAddress.getAddress());
        defaultContainer.addView(text2);
        
        // Show other addresses
        HashMap<String, Address> otherAddresses = addresses.getAddresses();
        Log.d(TAG, "OTHER ADDRESSES: " + otherAddresses.size());
        Iterator<Entry<String, Address>> it = otherAddresses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Address> item = (Map.Entry<String, Address>) it.next();
            Log.d(TAG, "ADDRESS: " + item.getValue().getAddress());
            TextView text = new TextView(getBaseActivity());
            text.setText("" + item.getValue().getAddress());
            text.setTag(item.getValue());
            text.setOnClickListener(this);
            otherContainer.addView(text);
        }

        getBaseActivity().showContentContainer(false);
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
        
        if (eventType == EventType.GET_CUSTOMER_ADDRESSES_EVENT) {
            Log.d(TAG, "RECEIVED GET_CUSTOMER_ADDRESSES_EVENT");
        }
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitAddresses() {
        Log.i(TAG, "TRIGGER: LOGIN");
        triggerContentEvent(new CreateAddressHelper(), null, this);
    }
    
    private void triggerGetAddresses(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetCustomerAddressesHelper(), null, this);
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
