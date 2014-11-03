/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.holoeverywhere.widget.CheckBox;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Address;
import pt.rocket.framework.objects.Addresses;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.address.SetBillingAddressHelper;
import pt.rocket.helpers.checkout.GetBillingFormHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.GenericRadioGroup;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the my addresses and set the on process checkout the billing and shipping address. 
 * @author sergiopereira
 */
public class CheckoutMyAddressesFragment extends BaseFragment implements OnClickListener, IResponseCallback, RadioGroup.OnCheckedChangeListener{

    private static final String TAG = LogTagHelper.create(CheckoutMyAddressesFragment.class);
    
    private static final String IS_SAME_ADDRESS = "1";
    
    private static final String ISNT_SAME_ADDRESS = "0";
    
    private static final String BILLING_ID_TAG = "billingAddressId";
    
    private static final String SHIPPING_ID_TAG = "shippingAddressId";
    
    private static final String IS_SAME_TAG = "shippingAddressDifferent";
    
    private GenericRadioGroup mTopRadioGroup;

    private GenericRadioGroup mBottomRadioGroup;

    private Addresses addresses;

    private CheckBox mIsSameCheckBox;

    private TextView mTopTitle;

    private TextView mBottomTitle;

    private View mTopAddContainer;

    private ScrollView mMainScrollView;

    private Form hiddenForm;
    
    private static String sameAddress = "";
    
    private long loadTime = 0;
    
    /**
     * Get instance
     * @return CheckoutMyAddressesFragment
     */
    public static CheckoutMyAddressesFragment getInstance(Bundle bundle) {
        return new CheckoutMyAddressesFragment();
    }


    /**
     * Empty constructor
     */
    public CheckoutMyAddressesFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_my_addresses,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_BILLING);
        // ConstantsCheckout.CHECKOUT_BILLING
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
        loadTime = System.currentTimeMillis();
        setRetainInstance(true);
        // Flag
        sameAddress = "";
        // Get arguments
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_ADDRESSES);
        // Tracking checkout step
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
        if(loadTime == 0)loadTime = System.currentTimeMillis();
        // Get the main scroll view that can be null
        mMainScrollView = (ScrollView) view.findViewById(R.id.checkout_addresses_one_scroll);
        // Get containers
        mTopTitle = (TextView) view.findViewById(R.id.checkout_addresses_default_title);
        mTopRadioGroup = (GenericRadioGroup) view.findViewById(R.id.checkout_addresses_default_container);
        mTopRadioGroup.setOnCheckedChangeListener(this);
        mBottomTitle = (TextView) view.findViewById(R.id.checkout_addresses_other_title);
        mBottomRadioGroup = (GenericRadioGroup) view.findViewById(R.id.checkout_addresses_other_container);
        mBottomRadioGroup.setOnCheckedChangeListener(this);
        mIsSameCheckBox = (CheckBox) view.findViewById(R.id.checkout_address_billing_checkbox);
        mIsSameCheckBox.setOnClickListener(this);
        // Buttons
        mTopAddContainer = view.findViewById(R.id.checkout_addresses_top_button_container);
        view.findViewById(R.id.checkout_addresses_default_add).setOnClickListener(this);
        view.findViewById(R.id.checkout_addresses_other_add).setOnClickListener(this);
        view.findViewById(R.id.checkout_addresses_button_enter).setOnClickListener(this);
        // Get and show addresses
        triggerGetBillingForm();
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
        TrackerDelegator.trackPage(TrackingPage.ADDRESS_SCREEN, loadTime, true);
        
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
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_addresses_button_enter) onClickSubmitAddressesButton();
        // Add new
        else if(id == R.id.checkout_addresses_default_add) onClickCreateAddressButton();
        // Add new
        else if(id == R.id.checkout_addresses_other_add) onClickCreateAddressButton();
        // Edit button
        else if(id == R.id.checkout_address_item_btn_edit) onClickEditAddressButton(view);
        // Delete button
        else if(id == R.id.checkout_address_item_btn_delete) onClickDeleteAddressButton(view);
        // Check box
        else if(id == R.id.checkout_address_billing_checkbox) onClickCheckBox((CheckBox) view);
        //retry button
        else if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Unknown view   
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW " + view.getTag());
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
//            restartAllFragments();
        }
    }
    
    /**
     * Process the click on add button.
     * @author sergiopereira
     */
    private void onClickCreateAddressButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        getBaseActivity().onSwitchFragment(FragmentType.CREATE_ADDRESS, null, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the click on check box.
     * @param view
     * @author sergiopereira
     */
    private void onClickCheckBox(final CheckBox view){
        Log.d(TAG, "SAME ADDRESS CLICK: " + view.isChecked());
        // Show loading
        showFragmentLoading();
        // Validate the current selection
        validateCurrentShippingSelection();
        // Clean containers
        cleanContainers();
        // Show loading for new redesign
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isVisible()){
                    // Show address
                    showAddresses(view.isChecked());
                }
            }
        }, 750);
    }

    /**
     * Validate the shipping selection and if not zero switch the addresses
     * @author sergiopereira
     */
    private void validateCurrentShippingSelection(){
        // Validate if is not the current shipping address
        int position = mTopRadioGroup.getCheckedPosition();
        if(position > 0) addresses.switchShippingAddress(position - 1);
    }
    
    
    /**
     * Process the click on delete button.
     * @param view
     * @author sergiopereira
     */
    private void onClickDeleteAddressButton(View view){
        String addressId = view.getTag().toString();
        Log.i(TAG, "ON CLICK: DELETE ADDRESS " + addressId);
    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag. 
     * @param view
     * @author sergiopereira
     */
    private void onClickEditAddressButton(View view){
        // Get tag that contains the address id
        int addressId = Integer.parseInt(view.getTag().toString());
        Log.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
        // Selected address
        Address selectedAddress = null;
        // Get addresses
        Address shippingAddress = addresses.getShippingAddress();
        Address billingAddress = addresses.getBillingAddress();
        HashMap<String, Address> otherAddresses = addresses.getAddresses();
        // Validate
        if (shippingAddress != null && shippingAddress.getId() == addressId) selectedAddress = shippingAddress;
        else if (billingAddress != null && billingAddress.getId() == addressId) selectedAddress = billingAddress;
        else if(otherAddresses != null && otherAddresses.containsKey(""+addressId)) selectedAddress = otherAddresses.get(""+addressId);
        // Validate selected address
        if(selectedAddress != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(CheckoutEditAddressFragment.SELECTED_ADDRESS, selectedAddress);
            getBaseActivity().onSwitchFragment(FragmentType.EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Log.i(TAG, "SELECTED ADDRESS ID: " + addressId + " NO MATCH");
        }
    }    

    /**
     * Process the click on submit button.</br>
     */
    private void onClickSubmitAddressesButton() {
        Log.i(TAG, "ON CLICK: SUBMIT");
        // Validate the is same check box
        if (mIsSameCheckBox.isChecked()) submitSameAddresses();
        else submitDifferentAddresses();
    }
    
    /**
     * Submit the same address for shipping and billing
     * @author sergiopereira
     */
    private void submitSameAddresses(){
        // Validate radio group
        if (mTopRadioGroup.isValidateGroup()){
            // Get addresses id and submit
            String addressId = mTopRadioGroup.getCheckedTag().toString();
            submitForm(addressId, addressId, IS_SAME_ADDRESS);
        } else if(mBottomRadioGroup.isValidateGroup()){
            // Get addresses id and submit
            String addressId = mBottomRadioGroup.getCheckedTag().toString();
            submitForm(addressId, addressId, IS_SAME_ADDRESS);
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.billing_choose_address), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Submit the different addresses for shipping and billing
     * @author sergiopereira
     */
    private void submitDifferentAddresses() {
        // Validate radio groups
        if (mTopRadioGroup.isValidateGroup() && mBottomRadioGroup.isValidateGroup()) {
            // Get addresses id
            String shippingAddressId = mTopRadioGroup.getCheckedTag().toString();
            String billingAddressId = mBottomRadioGroup.getCheckedTag().toString();
            String isSameAddress = ISNT_SAME_ADDRESS;
            if (shippingAddressId != null) {
                isSameAddress = (shippingAddressId.equals(billingAddressId)) ? IS_SAME_ADDRESS : ISNT_SAME_ADDRESS;
            }
            // Submit values
            submitForm(shippingAddressId, billingAddressId, isSameAddress);
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.billing_choose_address), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Submit the current values using the form
     * @param shippingAddressId
     * @param billingAddressId
     * @param isDifferent
     */
    private void submitForm(String shippingAddressId, String billingAddressId, String isDifferent){
        Log.d(TAG, "SUBMIT ADDRESSES SHIP: " + shippingAddressId + " BIL: " + billingAddressId + " SAME: " + isDifferent);
        // Create content values from form
        ContentValues contentValues = new ContentValues();
        for (FormField field : hiddenForm.fields) {
            if (field.getKey().contains(BILLING_ID_TAG)) contentValues.put(field.getName(), billingAddressId);
            else if (field.getKey().contains(SHIPPING_ID_TAG)) contentValues.put(field.getName(), shippingAddressId);
            else if (field.getKey().contains(IS_SAME_TAG)) contentValues.put(field.getName(), isDifferent);
        }
        // Trigger
        triggetSetBilling(contentValues);
    }
    
    /**
     * ########## ON CHECK CHANGE LISTENER ##########  
     */
    
    /*
     * (non-Javadoc)
     * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedPos) {
        Log.d(TAG, "CHECKED RADIO TAG: " + ((GenericRadioGroup) group).getCheckedTag());
        // Is address is same for all
        if(mIsSameCheckBox.isChecked()) switchBetweenRadioGroups((GenericRadioGroup) group);
    }
    
    /**
     * Switch focus between radio group
     * @param parent
     * @author sergiopereira
     */
    private void switchBetweenRadioGroups(GenericRadioGroup parent){
        // Validate radio group
        int radioGroupId = parent.getId();
        // Checked on top group uncheck bottom group
        if(radioGroupId == mBottomRadioGroup.getId()) mTopRadioGroup.clearCheckGroup();
        // Checked on bottom group uncheck top group
        else if (radioGroupId == mTopRadioGroup.getId()) mBottomRadioGroup.clearCheckGroup();
    }
    
    /**
     * ########## FILL CONTENT ##########  
     */
    
    /**
     * Show my addresses
     * @param isSameAddress
     * @author sergiopereira
     */
    private void showAddresses(boolean isSameAddress) {
        Log.d(TAG, "SHOW ADDRESSES: " + isSameAddress);
        // Set flag
        sameAddress = "" + isSameAddress;
        // Validate current address
        if(isSameAddress){
            // Set top container
            mTopTitle.setText(getString(R.string.billing_def_shipping_label));
            mTopTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            // Set Shipping Address (checked)
            mTopRadioGroup.setCheckedItem(0);
            addAddress(mTopRadioGroup, addresses.getShippingAddress());
            // Set the check box checked
            mIsSameCheckBox.setChecked(isSameAddress);
            // Hide add button
            mTopAddContainer.setVisibility(View.GONE);
            // Set bottom container
            mBottomTitle.setText(getString(R.string.billing_others_label));
            // Add billing address if different
            if(!addresses.hasDefaultShippingAndBillingAddress()) addAddress(mBottomRadioGroup, addresses.getBillingAddress());
            // Set the other addresses
            addAddresses(mBottomRadioGroup, addresses.getAddresses());
        }else{
            // Set top container
            mTopTitle.setText(getString(R.string.billing_shipping_label));
            // mTopTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_addaddress_orange, 0);
            // Set Shipping Address (checked) and others
            mTopRadioGroup.setCheckedItem(0);
            addAddress(mTopRadioGroup, addresses.getShippingAddress());
            addAddresses(mTopRadioGroup, addresses.getAddresses());
            // Set the check box checked
            mIsSameCheckBox.setChecked(isSameAddress);
            // Show add button
            mTopAddContainer.setVisibility(View.VISIBLE);
            // Set bottom container
            mBottomTitle.setText(getString(R.string.billing_billing_label));
            // Set Billing address (checked) and others
            mBottomRadioGroup.setCheckedItem(0);
            addAddress(mBottomRadioGroup, addresses.getBillingAddress());
            addAddresses(mBottomRadioGroup, addresses.getAddresses());
        }
        // Show content
        showFragmentContentContainer();
    }
    
    /**
     * Clean the view
     * @author sergiopereira 
     */
    private void cleanContainers(){
        if(mMainScrollView != null) mMainScrollView.fullScroll(ScrollView.FOCUS_UP); 
        if(mTopRadioGroup != null) mTopRadioGroup.removeAllViews();
        if(mBottomRadioGroup != null) mBottomRadioGroup.removeAllViews();
    }

    /**
     * Add addresses to the radio group
     * @param container the radio group
     * @param addresses
     * @author sergiopereira
     */
    private void addAddresses(GenericRadioGroup container, HashMap<String, Address> addresses) {
        Iterator<Entry<String, Address>> it = addresses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Address> item = (Map.Entry<String, Address>) it.next();
            Address otherAddress = item.getValue();
            addAddress(container, otherAddress);
        }
    }
    
    /**
     * Add the current address to the radio group
     * @param container
     * @param address
     * @author sergiopereira
     */
    private void addAddress(GenericRadioGroup container, Address address){
        Log.d(TAG, "ADD ADDRESS: " + address.getId());
        View addressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, container, false);
        setAddressView(addressView, address, "" + address.getId());
        container.addView(addressView, "" + address.getId());
    }

    /**
     * Set the address view
     * @param parent the main view
     * @param address the current address
     * @param tag the tag to associate
     * @author sergiopereira
     */
    private void setAddressView(View parent, Address address, String tag){
        // Text
        ((TextView) parent.findViewById(R.id.checkout_address_item_name)).setText(address.getFirstName() + " " + address.getLastName());
        ((TextView) parent.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());

        // Only use region if is available
        StringBuilder regionString = new StringBuilder();
        if(!TextUtils.isEmpty(address.getRegion())) {
            regionString.append(address.getRegion()).append(" ");
        }
        regionString.append(address.getCity());
        ((TextView) parent.findViewById(R.id.checkout_address_item_region)).setText(regionString.toString());

        ((TextView) parent.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) parent.findViewById(R.id.checkout_address_item_phone)).setText(""+address.getPhone());
        parent.findViewById(R.id.checkout_address_item_divider).setVisibility(View.VISIBLE);
        // Buttons
        View editBtn = parent.findViewById(R.id.checkout_address_item_btn_edit);
        View deleteBtn = parent.findViewById(R.id.checkout_address_item_btn_delete);
        // deleteBtn.setVisibility(View.VISIBLE);
        editBtn.setTag(tag);
        deleteBtn.setTag(tag);
        editBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger to set the billing form
     * @param contentValues
     */
    private void triggetSetBilling(ContentValues contentValues){
        Log.d(TAG, "TRIGGER SET BILLING");
        // Submit values
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetBillingAddressHelper.FORM_CONTENT_VALUES, contentValues);
        triggerContentEvent(new SetBillingAddressHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the billing form
     */
    private void triggerGetBillingForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        //Validate is service is available
        if(JumiaApplication.mIsBound) {
            triggerContentEvent(new GetBillingFormHelper(), null, this);
        } else {
            showFragmentRetry(this);
        }
    }
    
   
    /**
     * ############# RESPONSE #############
     */
  
    /**
     * Parse success response
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
        case GET_BILLING_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_BILLING_FORM_EVENT");
            this.hiddenForm = bundle.getParcelable(Constants.BUNDLE_FORM_DATA_KEY);
            Addresses addresses = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            this.addresses = addresses;
            // Validate response
            if(!isValidateResponse()){ super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_BILLING_FORM_EVENT"); return true; }
            // Show addresses using saved value, if is the same address for Bill and Ship
            if(!TextUtils.isEmpty(sameAddress)) showAddresses(Boolean.parseBoolean(sameAddress));
            else showAddresses(addresses.hasDefaultShippingAndBillingAddress());
            // Get order summary
            OrderSummary orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_BILLING, orderSummary);
            break;
        case SET_BILLING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.SHIPPING_METHODS;
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            break;
        }
        
        return true;
    }
    
    /**
     * Parse error response
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
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_BILLING_FORM_EVENT:
            Log.w(TAG, "RECEIVED GET_BILLING_FORM_EVENT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED GET_BILLING_FORM_EVENT");
            break;
        case SET_BILLING_ADDRESS_EVENT:
            Log.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
            showFragmentContentContainer();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors, R.string.add_address);
            }else{
                Log.w(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT: " + errorCode.name());
                super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED SET_BILLING_ADDRESS_EVENT: ");
            }
        default:
            break;
        }
        
        
        return false;
    }
    
    /**
     * Method used to validate if the user has shipping and billing address
     * @return true or false
     * @author sergiopereira
     */
    private boolean isValidateResponse(){
        // Validate addresses at this point the user should have addresses
        try {
            if(addresses.hasBillingAddress() && addresses.hasShippingAddress()){ return true; } 
            else { Log.w(TAG, "WARNING: CUSTOMER SHIPPING OR BILLING ADDRESS IS NULL"); return false; }
        } catch (NullPointerException e) { Log.w(TAG, "WARNING: ADDRESSES IS NULL"); return false; }
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
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

    
    /**
     * ########### DIALOGS ###########  
     */    
    
    /**
     * Dialog used to show an error
     * @param errors
     */
    private void showErrorDialog(HashMap<String, List<String>> errors, int titleId) {
        Log.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(titleId),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dialog.dismiss();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        }
    }
}
