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
import pt.rocket.framework.objects.Address;
import pt.rocket.framework.objects.Addresses;
import pt.rocket.framework.objects.ShippingMethods;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.address.GetMyAddressesHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
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
public class CheckoutMyAddressesFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutMyAddressesFragment.class);
    
    private static CheckoutMyAddressesFragment customerAddressesFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutMyAddressesFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        customerAddressesFragment = new CheckoutMyAddressesFragment();
        return customerAddressesFragment;
    }

    private ViewGroup defaultContainer;

    private ViewGroup otherContainer;

    private Addresses addresses;

    /**
     * Empty constructor
     */
    public CheckoutMyAddressesFragment() {
        super(EnumSet.of(EventType.GET_CUSTOMER_ADDRESSES_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                BaseActivity.CHECKOUT_STEP_2);
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
        // Edit button
        else if(id == R.id.checkout_address_item_btn_edit) onClickEditAddressButton(view);
        // Delete button
        else if(id == R.id.checkout_address_item_btn_delete) onClickDeleteAddressButton(view);
        // Delete button
        else if(id == R.id.checkout_address_item_btn_radio) onSelectedAddressButton(view);
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW " + view.getTag());
         
    }
    

    private void onSelectedAddressButton(View view){
        // Get tag
        String addressId = view.getTag().toString();
        Log.i(TAG, "ON CLICK: SELECT ADDRESS " + addressId);
        // TODO
    }

    
    private void onClickDeleteAddressButton(View view){
        // Get tag
        String addressId = view.getTag().toString();
        Log.i(TAG, "ON CLICK: DELETE ADDRESS " + addressId);
        // TODO
    }

    
    private void onClickEditAddressButton(View view){
        // Get tag
        int addressId = Integer.parseInt(view.getTag().toString());
        Log.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
        
        Address selectedAddress = null;
        
        // Validate
        Address shippingAddress = addresses.getShippingAddress();
        Address billingAddress = addresses.getBillingAddress();
        if (shippingAddress != null && shippingAddress.getId() == addressId)
            selectedAddress = shippingAddress;
        else if (billingAddress != null && billingAddress.getId() == addressId)
            selectedAddress = billingAddress;
        else
            selectedAddress = addresses.getAddresses().get(addressId);
        
        // Validate selected address
        if(selectedAddress != null){
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

        switch (eventType) {
        case GET_CUSTOMER_ADDRESSES_EVENT:
            Log.d(TAG, "RECEIVED GET_CUSTOMER_ADDRESSES_EVENT");
            Addresses addresses = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showAddresses(addresses);
            break;
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            break;
        default:
            break;
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
        
        
        /**
         * TODO : Improve this method
         */
        
        // Show shipping addresses
        Address shippingAddress = addresses.getShippingAddress();
        Log.d(TAG, "SHIPPING ADDRESS: " + shippingAddress.getAddress());
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, defaultContainer, false);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(shippingAddress.getFirstName() + " " + shippingAddress.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(shippingAddress.getAddress());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(shippingAddress.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(shippingAddress.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+shippingAddress.getPhone());
        // Buttons
        View radioBtn = shippingAddressView.findViewById(R.id.checkout_address_item_btn_radio);
        View editBtn = shippingAddressView.findViewById(R.id.checkout_address_item_btn_edit);
        View deleteBtn = shippingAddressView.findViewById(R.id.checkout_address_item_btn_delete);
        radioBtn.setTag(shippingAddress.getId());
        editBtn.setTag(shippingAddress.getId());
        deleteBtn.setTag(shippingAddress.getId());
        radioBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        defaultContainer.addView(shippingAddressView);
        
        // Show billing addresses
        Address billingAddress = addresses.getBillingAddress();
        Log.d(TAG, "BILLING ADDRESS: " + billingAddress.getAddress());
        View billingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, defaultContainer, false);
        ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_name)).setText(billingAddress.getFirstName() + " " + shippingAddress.getLastName());
        ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_street)).setText(billingAddress.getAddress());
        ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_region)).setText(billingAddress.getCity());
        ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(billingAddress.getPostcode());
        ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+billingAddress.getPhone());
        // Buttons
        View radioBtn1 = billingAddressView.findViewById(R.id.checkout_address_item_btn_radio);
        View editBtn1 = billingAddressView.findViewById(R.id.checkout_address_item_btn_edit);
        View deleteBtn1 = billingAddressView.findViewById(R.id.checkout_address_item_btn_delete);
        radioBtn1.setTag(billingAddress.getId());
        editBtn1.setTag(billingAddress.getId());
        deleteBtn1.setTag(billingAddress.getId());
        radioBtn1.setOnClickListener(this);
        editBtn1.setOnClickListener(this);
        deleteBtn1.setOnClickListener(this);
        defaultContainer.addView(billingAddressView);
        
        // Show other addresses
        HashMap<String, Address> otherAddresses = addresses.getAddresses();
        Log.d(TAG, "OTHER ADDRESSES: " + otherAddresses.size());
        Iterator<Entry<String, Address>> it = otherAddresses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Address> item = (Map.Entry<String, Address>) it.next();
            Address otherAddress = item.getValue();
            Log.d(TAG, "ADDRESS: " + otherAddress.getAddress());
            //ViewGroup.inflate(getBaseActivity(), R.layout.checkout_address_item, otherContainer);
            View otherAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, otherContainer, false);
            ((TextView) otherAddressView.findViewById(R.id.checkout_address_item_name)).setText(otherAddress.getFirstName() + " " + shippingAddress.getLastName());
            ((TextView) otherAddressView.findViewById(R.id.checkout_address_item_street)).setText(otherAddress.getAddress());
            ((TextView) otherAddressView.findViewById(R.id.checkout_address_item_region)).setText(otherAddress.getCity());
            ((TextView) otherAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(otherAddress.getPostcode());
            ((TextView) otherAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+otherAddress.getPhone());
            // Buttons
            View radioBtn2 = otherAddressView.findViewById(R.id.checkout_address_item_btn_radio);
            View editBtn2 = otherAddressView.findViewById(R.id.checkout_address_item_btn_edit);
            View deleteBtn2 = otherAddressView.findViewById(R.id.checkout_address_item_btn_delete);
            radioBtn2.setTag(item.getKey());
            editBtn2.setTag(item.getKey());
            deleteBtn2.setTag(item.getKey());
            radioBtn2.setOnClickListener(this);
            editBtn2.setOnClickListener(this);
            deleteBtn2.setOnClickListener(this);
            // Add view
            otherContainer.addView(otherAddressView);
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
        
        switch (eventType) {
        case GET_CUSTOMER_ADDRESSES_EVENT:
            Log.d(TAG, "RECEIVED GET_CUSTOMER_ADDRESSES_EVENT");
            break;
        case CREATE_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED CREATE_ADDRESS_EVENT");
            break;
        default:
            break;
        }
        
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerGetAddresses(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        triggerContentEvent(new GetMyAddressesHelper(), null, this);
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
